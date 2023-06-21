package demo2_listener;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.adonax.audiocue.AudioCue;
import com.adonax.audiocue.AudioCueListener;

public class SoundHandler {

	private static AudioCue crowCaw;
	
	static {
	
		URL url = SoundHandler.class.getResource("crow.wav");
		
		try {
			crowCaw = AudioCue.makeStereoCue(url, 1);
			crowCaw.open();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static void playCrow() {
		crowCaw.play(1, 0, 1, (int)(Math.random() * 4));
	}
	
	public static void addListener(AudioCueListener listener) {
		crowCaw.addAudioCueListener(listener);
	}
	
}
