package be.humanoids.ma;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
/**
 * the main Window Object, is a singleton.
 * @author Martin
 */
public class Window extends JFrame {
    private static Window singletonWindow;
    private boolean recording = false;
    private JButton record;
    AudioInput a;
    private Window() {
        // Create Recording button
        record = new JButton("Start");
        // Add event handler for OK button
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
        
        setSize(400, 300);
        setTitle("Audio Analyzer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().add(buttonPanel,BorderLayout.SOUTH);
        setVisible(true);
    }
    
    public static Window getWindow() {
       if(singletonWindow==null) {
           singletonWindow = new Window();
           singletonWindow.init();
       }
       return singletonWindow;
    }
    
    private boolean init() {
        
        return true;
    }
    
    public void setAudioInput(AudioInput ai) {
        a = ai;
        record.setEnabled(true);
    }
    
    private void toggleRecording() {
        if(a!=null) {
            if(recording) {
                AudioFile af = new AudioFile("E:/Users/Martin/Temp/TestFile.wav");
                af.writeFile(a.stopRecording(),a.getAudioFormat());
                record.setText("Start");
            }
            else {
                a.resumeRecording();
                record.setText("Stop");
            }
            
            recording = !recording;
        }
    }
    
}
