package demo1_playback_basics;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.adonax.audiocue.AudioCue;
import com.adonax.audiocue.AudioCueFunctions.PanType;
import com.adonax.audiocue.AudioCueFunctions.VolType;

/*
 * Dual purpose demo: 
 * 	(1) Show an example of the real-time control of volume, panning, and speed.
 *  (2) Contrast the play() method (drawing from the pool of instances) with the 
 *      start(instanceID) method, where one reserves and reuses a single instance.
 */
public class RealTimeSlidersCue {

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

	private AudioCue bell;
	
	public DemoFrame(){
		
		setTitle("AudioCue Real-time Sliders Demo");
		setSize(200, 300);
		
		// Set up the AudioCue
		URL url = this.getClass().getResource("a3bell.wav");
		try {
			bell = AudioCue.makeStereoCue(url, 6);
			bell.open();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
		
		// VolType, PanType control how the linear values are scaled.
		bell.setVolType(VolType.EXP_X2);  
		bell.setPanType(PanType.SINE_LAW);
				
		// This button will draw from the pool of available instances.
		JButton btnPlay = new JButton("Play random bell");
		btnPlay.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnPlay.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				double vol = 0.7 + Math.random() / 5.0; // 0.5 to 0.9
				double pan = -1 + Math.random() * 2; // -1 to 1
				double speed = Math.pow(2, Math.random()); // 1 to 2 (one octave) 
				int instanceID = bell.play(vol, pan, speed, 0);
				System.out.println("random bell, instanceID:" + instanceID
						+ "\tvolume:" + vol + "\tpan:" + pan + "\tspeed:" + speed);
			}
		});
		
		// Reserve an instance for use with real-time controls.
		int instanceID = bell.obtainInstance();	
		// Initialize vol, pan, speed to match starting slider values
		bell.setVolume(instanceID, 0.5);
		bell.setPan(instanceID, 0);
		bell.setSpeed(instanceID, 1);		
		
		JButton btnInstance = new JButton("Play instance");
		btnInstance.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnInstance.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// This method halts the playback, then restarts playing
				// from the beginning of the cue.
				// I have added a ramp down to 0 plus some sleep, in order to 
				// prevent a click occurring due to the abrupt stop. Seems like 
				// it might be a good idea to come up with some sort of "soft" 
				// release option for a future version, so we don't have to 
				// ever code Thread.sleep().
				double vol = bell.getVolume(instanceID);
				bell.setVolume(instanceID, 0);
				try {
					Thread.sleep(20);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				bell.stop(instanceID);
				bell.setFramePosition(instanceID, 0);
				bell.setVolume(instanceID, vol);
				bell.start(instanceID);
				System.out.println("playing instanceID:" + instanceID
						+ "\tvol:" + bell.getVolume(instanceID)
						+ "\tpan:" + bell.getPan(instanceID)
						+ "\tspeed:" + bell.getSpeed(instanceID));
			}
		});
		
		JLabel lblVol = new JLabel("Volume");
		lblVol.setAlignmentX(Component.CENTER_ALIGNMENT);
		JSlider volumeSlider = new JSlider();
		volumeSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				bell.setVolume(instanceID, volumeSlider.getValue()/100.0);
				System.out.println("volume: " + volumeSlider.getValue()/100.0);
			}
		});

		JLabel lblPan = new JLabel("Pan");
		lblPan.setAlignmentX(Component.CENTER_ALIGNMENT);
		JSlider panSlider = new JSlider();
		panSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// input to pan normalized to range [-1..1]
				bell.setPan(instanceID, panSlider.getValue()/50.0 - 1);
				System.out.println("pan: " + (panSlider.getValue()/50.0 - 1));
			}
		});

		JLabel lblSpeed = new JLabel("Speed");
		lblSpeed.setAlignmentX(Component.CENTER_ALIGNMENT);
		JSlider speedSlider = new JSlider();
		speedSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				double val = speedSlider.getValue()/100.0;
				double speed = Math.pow(2, val * 4) / 4;
				// Range will be +/- two octaves, from 0.25 to 4).
				bell.setSpeed(instanceID, speed);
				System.out.println("speed: " + speed);
			}
		});
		
		// GUI Layout
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);

		panel.add(btnPlay);
		Dimension dimSpacer = new Dimension(100, 25);
		panel.add(new Box.Filler(dimSpacer, dimSpacer, dimSpacer));
		panel.add(btnInstance);
		panel.add(lblVol);
		panel.add(volumeSlider);
		panel.add(lblPan);
		panel.add(panSlider);
		panel.add(lblSpeed);
		panel.add(speedSlider);

		add(panel);
	}
}
