package demo3_pcm;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * This demo shows two examples of loading an AudioCue directly with PCM.
 * The format for PCM used by AudioCue uses stereo floats, ranging from
 * -1 to 1, at 44100 fps.
 * 
 * In the first example, PCM is extracted from a .wav file. An AudioCue 
 * is loaded with a cue consisting of filtered pink noise, and a copy of 
 * the PCM is extracted with the getPcmCopy() method. The amplitudes are 
 * then given a simple AR (attack/release) envelope, and the result saved 
 * as a new AudioCue.
 * 
 * In the second example, a Wavetable array, such as might be used with 
 * Wavetable Sythesis, is constructed and played back. The PCM data is 
 * obtained by adding the sine waves that give a classic sawtooth wave.
 * 
 * For another example of working with PCM, see the SoundScapeBattleField
 * demo, where the PCM for a gunshot SFX has its long, reverberant tail 
 * trimmed to create a loopable cue used for a machine gun shot SFX.
 */
public class PCMDemo {

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
	private boolean playingWave, playingNoise;

	public DemoFrame(){
		
		setTitle("AudioCue demo3 direct PCM load");
		setSize(300, 150); 
		
		JPanel panel = new JPanel();
		
		JButton btnNoise = new JButton("Play noise");
		btnNoise.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (playingNoise) {
					SoundHandler3.stopNoise();
					btnNoise.setText("Play noise");
				} else {
					SoundHandler3.startNoise();
					btnNoise.setText("Stop noise");
				}
				playingNoise = !playingNoise;
			}
		});
		
		// The slider range is set to match the allowable range 
		// of speed changes.
		JSlider sliderNoise = new JSlider(-300, 300);
		sliderNoise.setValue(0);
		sliderNoise.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// Exponential curve distributes pitches evenly for 
				// human pitch perception. 
				double val = Math.pow(2, sliderNoise.getValue()/100.0);
				SoundHandler3.setNoiseSpeed(val);
			}
		});
		
		JButton btnSawtooth = new JButton("Play wave");
		btnSawtooth.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (playingWave) {
					SoundHandler3.stopWave();
					btnSawtooth.setText("Play wave");
				} else {
					SoundHandler3.startWave();
					btnSawtooth.setText("Stop wave");
				}
				playingWave = !playingWave;
			}
		});
		
		JSlider sliderSawtooth = new JSlider(-300, 300);
		sliderSawtooth.setValue(0);
		sliderSawtooth.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// AudioCue speed range: min is 0.125, max is 8. 
				double val = Math.pow(2, sliderSawtooth.getValue()/100.0);
				SoundHandler3.setWaveSpeed(val);
				System.out.println("wave speed:" + val);
			}
		});
		
		panel.add(btnNoise);
		panel.add(sliderNoise);
		panel.add(btnSawtooth);
		panel.add(sliderSawtooth);
		
		add(panel);		
	}
}