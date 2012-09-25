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
public class Window extends JFrame implements TransformedEventListener,InputEventListener,VisualizerEventListener {
    private static Window singletonWindow;
    private boolean recording = false;
    private JButton record;
    private JRadioButton waveform;
    private JRadioButton transform;
            
            
    AudioInput a;
    Visualizer visual;
    ImageIcon img;
    private JLabel label;
    private Timer transformTime;
    
    private Window() {     
        transformTime = new Timer();
        
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
        
        waveform = new JRadioButton(Visualizer.WAVEFORM);
        waveform.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisualizer(Visualizer.WAVEFORM);
            }
        });
        transform = new JRadioButton(Visualizer.FFT);
        transform.setSelected(true);
        transform.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisualizer(Visualizer.FFT);
            }
        });
        
        waveform.setEnabled(false);
        transform.setEnabled(false);

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
        visual = new Visualizer(this.getWidth(),this.getHeight()-100);
        visual.setType(Visualizer.FFT);

        record.setEnabled(true);
        waveform.setEnabled(true);
        transform.setEnabled(true);

        visual.addEventListener(this);
    }
    
    private void toggleRecording() {
        if(a!=null) {
            if(recording) {
                a.stopRecording();
                record.setText("Start");
                //record.setEnabled(false); // due to a bug in starting again with threads
            }
            else {
                a.startRecording();
                a.arthread.setEventTarget(this);
                a.arthread.addEventListener(this);
                a.resumeRecording();
                record.setText("Stop");
                visual.clearImage();
                img = new ImageIcon(visual.getImage());
                label.setIcon(img);
            }
            
            recording = !recording;
        }
    }
    
    private void getVisualizer(EventObject e) {
        img = new ImageIcon(visual.getImage());
        label.setIcon(img);
        if(transformTime.running()) {
            transformTime.stop();
            System.out.println((float)transformTime.toHerz()+ " Hz");
        }
        transformTime.start();
    }
    
    private void setVisualizer(String type) {
        if(!type.equals(visual.getType()))
            visual.toggleType();
    }
    
    @Override
    public void handleTransformEvent(EventObject e, Tone[] freq) {
        visual.updateFrequencies(freq);
    }
    
    @Override
    public void handleInputEvent(EventObject e,float[] d) {
        visual.updateData(d);
    }
    
    @Override
    public void handleVisualizerEvent(EventObject e) {
        getVisualizer(e);
    }
    
}
