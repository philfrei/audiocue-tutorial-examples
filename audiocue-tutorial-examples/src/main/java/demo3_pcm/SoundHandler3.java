package demo3_pcm;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.adonax.audiocue.AudioCue;

public class SoundHandler3 {

	private static AudioCue wave;
	private static AudioCue noise;
	private static int instanceWaveId;
	private static int instanceNoiseId;
	private static int NOISE_FRAMES = 1300; // arbitrary
	private static int SAMPLE_RATE = 44100;
	// The sawtooth array is given a size that results in playback frequency of 100 Hz.
	private static int SAWTOOTH_WAVE_FRAMES = SAMPLE_RATE / 100; 
	
	static {
		try {
			float[] noisePCM = getPcmForNoise();
			noise = AudioCue.makeStereoCue(noisePCM, "noise", 1);
		} catch (UnsupportedAudioFileException | IOException e1) {
			e1.printStackTrace();
		}
		instanceNoiseId = noise.obtainInstance();
		noise.setVolume(instanceNoiseId, 0.8);
		noise.setLooping(instanceNoiseId, -1);
	
		float[] wavePCM = getPcmForSawtooth();
		wave = AudioCue.makeStereoCue(wavePCM, "sawtooth", 1);
		instanceWaveId = wave.obtainInstance();
		wave.setVolume(instanceWaveId, 0.5);
		wave.setLooping(instanceWaveId, -1);
		
			
		try {
			wave.open();
			noise.open();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	private static float[] getPcmForNoise() throws UnsupportedAudioFileException, IOException {
		
		URL tempURL = SoundHandler3.class.getResource("pink_500millis.wav");
		AudioCue tempAC = AudioCue.makeStereoCue(tempURL, 1);
	    float[] cuePcmCopy = tempAC.getPcmCopy();
	    float[] cuePCM = Arrays.copyOf(cuePcmCopy, NOISE_FRAMES * 2);
	    
		int attackFrames = 128; 	
		float lastAttack = attackFrames - 1;
		
		// Attack
	    for (int i = 0; i < attackFrames; i++) { 	
	    		// vol ranges from 0 to 1 as i increases over 128 frames
	    		float vol = i / lastAttack;  
	    		cuePCM[i * 2] *= vol;
	    		cuePCM[i * 2 + 1] = cuePCM[i * 2];
	    }
	    
	    // Release
	    int releaseFrames = NOISE_FRAMES - attackFrames;
	    float lastReleaseFrame = releaseFrames - 1;
	    for (int i = 0; i < releaseFrames; i++) {
	    	// vol ranges from 1 to 0 as i increases over the remaining frames
	    	float vol = (lastReleaseFrame - i) / lastReleaseFrame;
	    	int frame = (attackFrames + i) * 2;
	    	cuePCM[frame] *= vol;
	    	cuePCM[frame + 1] = cuePCM[frame];
	    }
	    
	    return cuePCM;
	}

	private static float[] getPcmForSawtooth() {
		/*
		 *  Creates a PCM data for a sawtooth by summing 32 harmonics.
		 *  
		 *  We use the formula:
		 *      sin x + (1/2)sin 2*x + (1/3)sin 3*x + ... 
		 *  
		 *  Given a sample rate of 44100 fps, we have a Nyquist of 22050 Hz.
		 *  Keeping things simple here, a cycling PCM table with 441 steps will 
		 *  run at 100 Hz (near G#2). If we support 32 overtones, that spans a 
		 *  5-octave range, i.e., 100 Hz to 3200 Hz. We should be able to play 
		 *  up to E5 (MIDI 76, 659.26 Hz)) without aliasing. The pitch range in 
		 *  this demo is from 12.5 Hz to 800 Hz, but the amount of aliasing for 
		 *  the top if this range is at a low amplitude and hard to make out. 
		 *  Of course, for the lower pitches, more overtones would be desirable. 
		 *
		 *  Currently, if (cursor > cueFrameLength - 1) we restart at 0.
		 *  Better for looping playback would be to preserve the increment, 
		 *  i.e., 
		 *      if (cursor > cueFrameLength) cursor -= cueFrameLength;
		 *      
		 *  Should there be two modes possible in the API? (Restart at 0, vs. 
		 *  restart at (cursor - curFrameLength)? I'm thinking of adding more
		 *  options to the looping anyway (e.g., start/end points and transition
		 *  choices). These points will be considered when designing an expanded 
		 *  API for looping in a future version.
		 */
		float[] collector = new float[SAWTOOTH_WAVE_FRAMES]; // to hold PCM sums of sines
		for (int harmonic = 1; harmonic <= 32 ; harmonic++) {
			for (int i = 0; i < SAWTOOTH_WAVE_FRAMES; i++) {
				// Coded here to show the formula, can be optimized by pre-calculating 
				// invariants.
				collector[i] += 
						Math.sin(2 * Math.PI * harmonic * i / SAWTOOTH_WAVE_FRAMES)
									/ harmonic;
			}
		}
		
		// normalize
		float max = 0;
		for (int i = 0; i < SAWTOOTH_WAVE_FRAMES; i++) {
			if (Math.abs(collector[i]) > max) max = Math.abs(collector[i]);
		}
		System.out.println("largest absolute collector value is: " + max
				+ "\nnormalizing...");
		for (int i = 0; i < 441; i++) {
			collector[i] /= max;
		}
		
		// make into stereo PCM array
		float[] stereoPCM = new float[SAWTOOTH_WAVE_FRAMES * 2];
		for (int i = 0; i < SAWTOOTH_WAVE_FRAMES; i++) {
			stereoPCM[i * 2] = collector[i];
			stereoPCM[i * 2 + 1] = collector[i];
		}
		
		return stereoPCM;
	}

	public static void startWave() {
		wave.start(instanceWaveId);
	}
	
	public static void stopWave() {
		wave.stop(instanceWaveId);
	}

	public static void setWaveSpeed(double speed) {
		wave.setSpeed(instanceWaveId, speed);
	}

	public static void startNoise() {
		noise.start(instanceNoiseId);
	}
	
	public static void stopNoise() {
		noise.stop(instanceNoiseId);
	}

	public static void setNoiseSpeed(double speed) {
		noise.setSpeed(instanceNoiseId, speed);
	}

}
