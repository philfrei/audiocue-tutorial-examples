package demo1_playback_basics;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.adonax.audiocue.AudioCue;

public class SimplePlayback {

	public static void main(String[] args) {
 
	    EventQueue.invokeLater(new Runnable(){
	    
	    	public void run()
	    	{	
	    		SimpleDemoFrame frame = new SimpleDemoFrame();
	    		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    		frame.setVisible(true);
	    	}
	    });
	}	
}

class SimpleDemoFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private AudioCue trainhorn;
	
	public SimpleDemoFrame(){
		
		setTitle("AudioCue demo");
		setSize(300, 100);
		
		JPanel panel = new JPanel();

		JButton button = new JButton("Play sound");
		button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				trainhorn.play();
			}
		});
		
		panel.add(button);
		add(panel);

		/*
		 * The following sound file was cropped and faded, and its frame
		 * rate was converted from 32 to 16 fps:
		 * https://freesound.org/people/eliasheuninck/sounds/170464/
 		 * https://creativecommons.org/licenses/by/4.0/
		 */
		
		// Set up the AudioCue
		URL url = this.getClass().getResource("steam-train-horn.wav");
		try {
			trainhorn = AudioCue.makeStereoCue(url, 5); // allows 5 concurrent plays
			trainhorn.open();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}
