package be.humanoids.ma;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
/**
 * the main Window Object, is a singleton.
 * @author Martin
 */
public class Window extends JFrame {
    private static Window singletonWindow;
    private boolean recording = false;
    private JButton record;
    AudioInput a;
    Visualizer visual;
    ImageIcon img;
    
    private Window() {     
        img = new ImageIcon();
        JLabel label = new JLabel(img);
        
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
        
        setSize(400, 300);
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
        record.setEnabled(true);
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
        
    }
    
}
