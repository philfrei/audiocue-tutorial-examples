package demo4_soundscape;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.adonax.audiocue.AudioMixer;
import com.adonax.audiocue.AudioMixerTrack;

public abstract class StreamingWave implements AudioMixerTrack {

	/*
	 * This is a first crack at a helper class for playing large WAV assets as
	 * AudioMixer tracks. See the "Helicopter" class for a usage example. 
	 * It should be possible to further implement real time controls for volume, 
	 * panning, and pitch for a class like this by working with an internal PCM 
	 * buffer. Less clear to me would be implenting some form of concurrency. 
	 * For now, this will be left here as a possible utility rather than 
	 * formally making it part of the AudioCue library.
	 */
	
	private AudioInputStream audioInputStream;
	private boolean isRunning;
	private boolean isOpen;
	
	private float[] pcmBuffer;
	private byte[] byteBuffer;

	private URL url;
	private float vol = 1;
	
	public void setVol(float vol) {
		this.vol = vol;
	}
	
	StreamingWave (String cueUrl) {
		url = SoundHandler4.class.getResource(cueUrl);
		startAudioInputStream();
	}
	
	private void startAudioInputStream() {
		// Will restart AudioInputStream from beginning of .wav
		try {
			audioInputStream = AudioSystem.getAudioInputStream(url);
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void open(AudioMixer audioMixer) {
		if (isOpen) 
		{
			throw new IllegalStateException("Already open.");
		}
		isOpen = true;
		
		// assigned size is frames * stereo
		pcmBuffer = new float[audioMixer.readBufferSize];
		// two bytes per float (16-bit encoding)
		byteBuffer = new byte[audioMixer.sdlByteBufferSize];
		
		audioMixer.addTrack(this);
		audioMixer.updateTracks();
	}
	
	@Override
	public boolean isTrackRunning() {
		return isRunning;
	}

	@Override
	public void setTrackRunning(boolean isRunning) {
		if (isRunning) {
			startAudioInputStream();
			this.isRunning = true;
		} else {
			this.isRunning = false;
			audioInputStream = null;
		}
	}

	@Override
	public float[] readTrack() throws IOException {
				
		// read bytes from .wav
		int bytesRead = audioInputStream.read(byteBuffer, 0, byteBuffer.length);
		int framesRead = bytesRead / 2;
		
		// convert raw bytes to PCM
		for (int i = 0; i < framesRead; i++) {
			// 16-bits (two bytes) concatenated in little endian order
			pcmBuffer[i] =  ( byteBuffer[i * 2] & 0xff ) | ( byteBuffer[(i * 2) + 1] << 8 );
			// normalize
			pcmBuffer[i] /= 32767;
			// set volume
			pcmBuffer[i] *= vol;
		}
		
		// If the buffer wasn't filled (i.e., reached the end of the file) pad with 0
		for (int i = framesRead; i < pcmBuffer.length; i++)
		{
			pcmBuffer[i] = 0;
		}
			
		// cue has played to end
		if (bytesRead == -1) {
			setTrackRunning(false);
		}
		
		return pcmBuffer;
	}
}
