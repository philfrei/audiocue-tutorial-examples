package demo2_listener;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.adonax.audiocue.AudioCue;
import com.adonax.audiocue.AudioCueInstanceEvent;
import com.adonax.audiocue.AudioCueListener;

/*
 * This class shows how the start and end of an AudioCue can be used 
 * to trigger a graphics event.
 * A class (named SoundHandler) is used to hold the sound-related
 * functionality in one place.
 * 
 * Crow images created by Moby Theobald.
 */
public class AudioCueListenerDemo {

	public static void main(String[] args) {
 
	    EventQueue.invokeLater(new Runnable(){
	    
	    	public void run()
	    	{	
	    		DemoFrame frame = new DemoFrame();
	    		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    		frame.pack();
	    		frame.setVisible(true);
	    	}
	    });
	}	
}

class DemoFrame extends JFrame implements AudioCueListener {

	private static final long serialVersionUID = 1L;
	
	private ImageIcon iicCrowSilent;
	private ImageIcon iicCrowCawing;
	private JButton button;
	private boolean isPlaying;
	
	public DemoFrame(){
		
		setTitle("AudioCue demo2 AudioCueListener");
		
		SoundHandler.addListener(this);
		
		try {
			BufferedImage crow0 = ImageIO.read(this.getClass().getResource("crow0.jpg"));
			BufferedImage crow1 = ImageIO.read(this.getClass().getResource("crow1.jpg"));
			
			iicCrowSilent = new ImageIcon(crow0);
			iicCrowCawing = new ImageIcon(crow1);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		JPanel panel = new JPanel();
		
		button = new JButton(iicCrowSilent);
		button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (!isPlaying) {
					isPlaying = true;
					SoundHandler.playCrow();
				}
			}
		});
		
		panel.add(button);
		add(panel);
	}

	// AudioCueListener methods
	@Override
	public void audioCueOpened(long now, int threadPriority, int bufferSize, AudioCue source) {}

	@Override
	public void audioCueClosed(long now, AudioCue source) {}

	@Override
	public void instanceEventOccurred(AudioCueInstanceEvent event) {
		
		switch (event.type) {
		case OBTAIN_INSTANCE:
			break;
		case START_INSTANCE:
			button.setIcon(iicCrowCawing);
			break;
		case LOOP:
			break;
		case STOP_INSTANCE:
			isPlaying = false;
			button.setIcon(iicCrowSilent);
			break;
		case RELEASE_INSTANCE:
			break;
		}
	}
}