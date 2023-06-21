package demo4_soundscape;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.adonax.audiocue.AudioCue;
import com.adonax.audiocue.AudioMixer;

public class SoundHandler4 {

	private static AudioCue shot;
	private static AudioCue machineGunRound;
	private static AudioMixer audioMixer;
	
	private static Helicopter helicopter;
	
	private static ExecutorService executorService;
	private static SoundScapePlayer soundScapePlayer;
	// private static volatile boolean running;
	
	static {
		
		URL url = SoundHandler4.class.getResource("gunshot.wav");
		audioMixer = new AudioMixer();
		
		try {
			shot = AudioCue.makeStereoCue(url, 12);
			float[] shortShotPcm = getPcmForMachineGunShot(shot.getPcmCopy());
			machineGunRound = AudioCue.makeStereoCue(shortShotPcm, "machineGunRound", 3);

			shot.open(audioMixer);
			machineGunRound.open(audioMixer);
			
			helicopter = new Helicopter();
			helicopter.setVol(0.5f);
			helicopter.open(audioMixer);
			
			audioMixer.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
		
		executorService = Executors.newFixedThreadPool(1);
		soundScapePlayer = new SoundScapePlayer();
	}

	/*
	 * The original gunshot cue has a long, reverberant tail, making it 
	 * unsuitable for rapid repetition. Here we alter the PCM by truncating
	 * the tail and add a quick fade to silence in order to avoid a
	 * click caused by the discontinuity of abruptly stopping playback. 
	 * Idea for future release: allow specification of start and end frames
	 * for looping, with crossfade options for the iteration transition.
	 */
	private static float[] getPcmForMachineGunShot(float[] cuePCM) {

		int shortShotFrames = 6000; 
	    float[] shortShot = Arrays.copyOf(cuePCM,  shortShotFrames * 2);
	    int fadeBegins = 3000;
	    int fadeFrames = shortShotFrames - fadeBegins;
	    
	    for (int i = 0; i < fadeFrames; i++) {
	    	float shotVol = ((fadeFrames - 1f) - i)/ (fadeFrames - 1);
	    	shortShot[(i + fadeBegins) * 2] = shotVol;
	    	shortShot[(i + fadeBegins) * 2 + 1] = shotVol;
	    }
	    
	    return shortShot;
	}
	
	public static void start() {
		executorService.execute(soundScapePlayer);
		soundScapePlayer.setRunning(true);
	}
	
	public static void stop() {
		soundScapePlayer.setRunning(false);
	}
	
	public static void playSingleShot(double speed) {
		shot.play(6, 0, speed, 0);
	}
	
	public static void playHelicopter() {
		if (!helicopter.isTrackRunning()) {
			helicopter.setTrackRunning(true);
			System.out.println("heli");
		}
	}
	
	/*
	 * Runs the ambient sound entities in their own thread.
	 * Not shown here: use of loose coupling to control the entities and 
	 * instances, only the running variable demonstrates loose coupling.
	 * Thought for future consideration: It would be good to make the 
	 * granularity of the run loop smaller in order to give a more timely 
	 * responses to entity/instance variable changes, or to allow the 
	 * "real time" shots to be included without noticeable lag. Part of
	 * solution that I am considering for a future release is to make the 
	 * size of the processing block in the AudioMixer "while loop" 
	 * independent of the buffer size used for outputting to the SDL. 
	 * For example, Miller Puckette ("Theory and Technique of Electronic
	 * Music")cites Pd as having a default processing block of 64 samples, 
	 * whereas an efficient buffer for Java's SourceDataLine is more 
	 * likely in the 1000's of frames.  
	 *  
	 */
	static class SoundScapePlayer implements Runnable {
		private Random random = new Random(System.currentTimeMillis());
		
		private AudioEntity rifle0 = new AudioEntity(0.65, 0.75, 1);
		private AudioEntity rifle1 = new AudioEntity(0.55, -0.85, 1.1);
		private AudioEntity rifle2 = new AudioEntity(0.5, 0.25, 1.1);
		private AudioEntity rifle3 = new AudioEntity(0.6, -0.25, 1);

		private ACInstance machineGun0 = new ACInstance(machineGunRound, 0.52, -0.2, 1.35);
		private ACInstance machineGun1 = new ACInstance(machineGunRound, 0.45, -0.67, 1.4);
		private ACInstance machineGun2 = new ACInstance(machineGunRound, 0.47, 0.85, 1.35);

		private boolean running;
		
		public void setRunning(boolean running) {
			this.running = running;
		}
		public boolean getRunning() {
			return running;
		}
		
		public void run() {
			
			// Slight cheat: the Bomb SF/X has a long reverberant tail that helps 
			// establish a first impression of a larger space, so lead with it.
			bombDrop();
			randomPause();
	
			while(running) {

				int switchInt = (int)(Math.random() * 10);
				switch(switchInt) {

				// Rifles
				case 0: // near-right rifle
					shot.play(rifle0.vol, rifle0.pan, rifle0.speed, random.nextInt(2));
					break;
				case 1: // far-left rifle
					shot.play(rifle1.vol, rifle1.pan, rifle1.speed, 0);
					break;
				case 2: // near-left rifle
					shot.play(rifle2.vol, rifle2.pan, rifle2.speed, random.nextInt(2));
					break;
				case 3: // far-right rifle
					shot.play(rifle3.vol, rifle3.pan, rifle3.speed, 0);
					break;
				// Bombs	
				case 4:
				case 5: 
					bombDrop();
					break;	
				// Machine guns
				case 6: 
				case 7: 
					if (!machineGun0.isPlaying()) {
						machineGun0.fire(2 + random.nextInt(6));
					}
					break;
				case 8: 
					if (!machineGun1.isPlaying()) {
						machineGun1.fire(2 + random.nextInt(10));
					}
					break;
				case 9: 
					if (!machineGun2.isPlaying()) {
						machineGun2.fire(2 + random.nextInt(5));
					}
				}
				
				randomPause();			
			}			
		}

		private void randomPause() {
			// minimum 100, maximum 1300 millis
			try {
				Thread.sleep((int)(Math.random() * 1200) + 100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		private void bombDrop() {
			shot.play(
					0.99 - (Math.random() * 0.4), // vol
					0.9 - Math.random() * 1.8,    // pan
					0.15 + (Math.random() * 0.2), // speed
					0
				);
		}
		
		// This class is used to provide parameters to an AudioCue.
		class AudioEntity {
			double vol;
			double pan;
			double speed;
			
			AudioEntity(double vol, double pan, double speed) {
				this.vol = vol;
				this.pan = pan;
				this.speed = speed;
			}			
		}
	
		// This class is used to associate an AudioCue instance with a
		// set of parameters and a predefined "behavior".
		class ACInstance {
			AudioCue cue;
			int id;
			
			ACInstance(AudioCue cue, double vol, double pan, double speed) {
				this.cue = cue;
				id = cue.obtainInstance();
				cue.setVolume(id, vol);
				cue.setPan(id, pan);
				cue.setSpeed(id, speed);
			}
			
			boolean isPlaying() {
				return cue.getIsPlaying(id);
			}
			
			void fire(int nRounds) {
				cue.setFramePosition(id, 0);
				cue.setLooping(id, nRounds - 1);
				cue.start(id);	
			}
		}
	}
}