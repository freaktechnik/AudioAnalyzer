package be.humanoids.ma;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.*;
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
    private JLabel label;
    
    private Filter stabilizer;
    AudioInput a;
    
    private Window() {     
        
        stabilizer = new Filter();
        
        // ---------------------
        //          GUI
        // ---------------------
        // start/stop button
        record = new JButton("Start");
        record.addActionListener(new ActionListener() {
              @Override
	      public void actionPerformed(ActionEvent e) {
                  toggleRecording();
	      }
        });
        record.setEnabled(false);

        // Add button to a panel
        JPanel buttonPanel = new JPanel(new GridLayout(8,1,10,10));
        buttonPanel.add(record);
        
        // spinner for ToneOffset
        String[] pitches = {"C","Bb","F","Eb"};
        SpinnerModel tosm = new SpinnerListModel(pitches);
        final JSpinner toSpin = new JSpinner(tosm);
        toSpin.addChangeListener(
                new ChangeListener(){
                    @Override
                   public void stateChanged(ChangeEvent e) {
                        // stupid conversion because you can only get an object
                        String newF = toSpin.getModel().getValue().toString();
                                  
                        switch(newF) {
                            case "Bb":Tone.setOffsetInSteps(2);
                                break;
                            case "Eb":Tone.setOffsetInSteps(9);
                                break;
                            case "F": Tone.setOffsetInSteps(7);
                                break;
                            default: Tone.setOffsetInSteps(0);
                        }
                   }
                }
        );
        buttonPanel.add(toSpin);
        
        
        // spinner for BaseFrequency
        SpinnerNumberModel bfnm = new SpinnerNumberModel((int)Tone.getBaseFrequency(),1,10000,1);
        final JSpinner bfSpin = new JSpinner(bfnm);
        bfSpin.addChangeListener(
                new ChangeListener(){
                    @Override
                   public void stateChanged(ChangeEvent e) {
                        // stupid conversion because you can only get an object
                        String newF = bfSpin.getModel().getValue().toString();
                        double newD = Double.parseDouble(newF);
                        
                        Tone.setBaseFrequency(newD);
                   }
                }
        );
        buttonPanel.add(bfSpin);
        
        // label for the Tonename
        label = new JLabel(" ",JLabel.CENTER);
        
        setSize(400, 350);
        setTitle("Audio Analyzer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().add(label,BorderLayout.SOUTH);
        getContentPane().add(buttonPanel,BorderLayout.WEST);
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
                label.setText(" ");
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
        if(stabilizer.ready()&&recording) {
            label.setText(stabilizer.getTone().getAbsoluteName());
            System.out.println(stabilizer.getTone());
        }
    }
}
