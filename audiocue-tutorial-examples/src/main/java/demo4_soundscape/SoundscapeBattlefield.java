package demo4_soundscape;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/*
 * Demonstrates the construction of a soundscape from a minimum
 * of assets. The PCM of a gunshot that is used for a single-shooting
 * rifle is also used to create a machine-gun sfx, and when played back
 * at lower speeds provides the sound for bombs.
 * 
 * Two methods are shown for associating SFX with virtual instances. 
 * The use of the AudioMixer is shown, running in an independent thread, 
 * mixing all contributing sound entities into a single stereo output. 
 * 
 * Also shown is an abstract class that can be used to play, as an 
 * AudioMixer track, an asset that is too large to hold and play from memory.
 */
public class SoundscapeBattlefield {

	public static void main(String[] args) {
 
	    EventQueue.invokeLater(new Runnable(){
	    
	    	public void run()
	    	{	
	    		DemoFrame frame = new DemoFrame();
	    		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    		frame.setVisible(true);
	    	}
	    });
	}	
}

class DemoFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private boolean playing;

	public DemoFrame(){
		
		setTitle("AudioCue demo4 soundscape");
		setSize(300, 200); 
				
		/*
		 * This combination of "button" and slider allows one to hear
		 * the effect of different speeds with the gunshot SFX. I 
		 * was surprised that when greatly slowed, it works as well 
		 * as it does for bomb blasts. It's inclusion here also  
		 * shows that we can have a real time entity initiating sounds 
		 * (as in a single-person shooter) mixed with the ambient 
		 * battlefield sounds.
		 * 
		 * I made this control a JLabel instead of a JButton because
		 * a JButton only activates after the button is released. The 
		 * JLabel is set to trigger on a mouse-pressed event, making it
		 * a little easier to imagine the cue used in game play.
		 */
		JSlider singleShotSlider = new JSlider(-300, 300);
		singleShotSlider.setValue(0);
		
		JLabel lblSingleShot = new JLabel("  Play Single Shot  ");
		lblSingleShot.setBackground(Color.WHITE);
		lblSingleShot.setOpaque(true);
		lblSingleShot.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lblSingleShot.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {
				double val = Math.pow(2, singleShotSlider.getValue()/100.0);
				SoundHandler4.playSingleShot(val);	
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				lblSingleShot.setBackground(Color.WHITE);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				lblSingleShot.setBackground(new Color(224, 208, 224));
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		JButton buttonSoundscape = new JButton("Play Battlefield Soundscape");
		buttonSoundscape.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (playing) {
					SoundHandler4.stop();
					buttonSoundscape.setText("Play Battlefield Soundscape");
				} else {
					SoundHandler4.start();
					buttonSoundscape.setText("Stop Battlefield Soundscape");
				}
				playing = !playing;
			}
		});
		
		// The helicoptor-transit SFX asset is a large wav file. It will be
		// read and played from its file location rather than held in memory.
		JButton buttonHelicopter = new JButton("Launch Helicopter");
		buttonHelicopter.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
					SoundHandler4.playHelicopter();
			}
		});

		// Layout the GUI
		JPanel panel = new JPanel();

		panel.add(lblSingleShot);
		panel.add(singleShotSlider, BorderLayout.PAGE_END);
		panel.add(buttonSoundscape);
		panel.add(buttonHelicopter);
		
		add(panel);
	}

}