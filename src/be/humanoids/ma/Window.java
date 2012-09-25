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
    private Filter stabilizer;
            
            
    AudioInput a;
    private JLabel label;
    private Timer transformTime;
    
    private Window() {     
        transformTime = new Timer();
        
        stabilizer = new Filter();
        
        // ---------------------
        //          GUI
        // ---------------------
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
        
        label = new JLabel(" ",JLabel.CENTER);
        
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
        record.setEnabled(true);
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
                a.resumeRecording();
                record.setText("Stop");
            }
            
            recording = !recording;
        }
    }
    
    @Override
    public void handleTransformEvent(EventObject e, Tone[] freq) {
        stabilizer.setNewTone(freq);
        if(stabilizer.ready()) {
            label.setText(stabilizer.getTone().getAbsoluteName());
        }
    }
}
