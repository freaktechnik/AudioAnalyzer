package be.humanoids.ma;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import javax.swing.*;
/**
 * the main Window Object, is a singleton.
 * @author Martin
 */
public class Window extends JFrame implements TransformedEventListener {
    private static Window singletonWindow;
    private boolean recording = false;
    private JButton record;
    AudioInput a;
    Visualizer visual;
    ImageIcon img;
    private JLabel label;
    String visualizerType;
    
    private Window() {     
        img = new ImageIcon();
        label = new JLabel(img);
        
        record = new JButton("Start");
        record.addActionListener(new ActionListener() {
              @Override
	      public void actionPerformed(ActionEvent e) {
                  toggleRecording();
	      }
        });
        record.setEnabled(false);

        // Add button to a panel
        JPanel buttonPanel = new JPanel( );
        buttonPanel.add(record);
        
        JRadioButton waveform = new JRadioButton("Waveform");
        waveform.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisualizer("Waveform");
            }
        });
        JRadioButton transform = new JRadioButton("Transform");
        transform.setSelected(true);
        transform.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisualizer("Transform");
            }
        });

        ButtonGroup visualizer = new ButtonGroup();
        visualizer.add(waveform);
        visualizer.add(transform);
        buttonPanel.add(waveform);
        buttonPanel.add(transform);
        
        setSize(400, 350);
        setTitle("Audio Analyzer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().add(label,BorderLayout.NORTH);
        getContentPane().add(buttonPanel,BorderLayout.SOUTH);
        setVisible(true);
    }
    
    public static Window getWindow() {
       if(singletonWindow==null) {
           singletonWindow = new Window();
       }
       return singletonWindow;
    }
    
    public void setAudioInput(AudioInput ai) {
        a = ai;
        visual = new Visualizer(a.freq);
        visualizerType = "Transform";
        record.setEnabled(true);
        
        a.arthread.fourier.addEventListener(this);
    }
    
    private void toggleRecording() {
        if(a!=null) {
            if(recording) {
                a.stopRecording();
                record.setText("Start");
                record.setEnabled(false); // due to a bug in starting again with threads
            }
            else {
                a.resumeRecording();
                record.setText("Stop");
                visual.clearImage();
                img = new ImageIcon(visual.img);
            }
            
            recording = !recording;
        }
    }
    
    private void getVisualizer() {
        if(visualizerType=="Waveform") {
            img = new ImageIcon(visual.createWaveformImage());
        }
        else {
            img = new ImageIcon(visual.createTransformImage());
        }
        label.setIcon(img);

    }
    
    private void setVisualizer(String type) {
        visualizerType = type;
    }
    
    @Override
    public void handleTransformEvent(EventObject e, Tone[] freq, double[] d) {
        a.freq = freq;
        visual.updateFrequencies(freq);
        visual.updateData(d);
        getVisualizer();
    }
    
}
