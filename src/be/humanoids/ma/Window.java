package be.humanoids.ma;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
/**
 * the main Window Object, is a singleton.
 * @author Martin
 */
public class Window extends JFrame implements TransformedEventListener, InputEventListener {
    private static Window singletonWindow;
    private boolean recording = false;
    private JButton record;
    private JLabel label,label1,label2,label3,label4;
    private PointerDisplay pIndicator;
    private ImageIcon normalRecord;
    private ImageIcon recordingRecord;
    
    private Visualizer equalizer;
    private Visualizer waveform;
    
    private Filter stabilizer;
    AudioInput a;
    
    private Window() {
        
        stabilizer = new Filter();
        
        // ---------------------
        //          GUI
        // ---------------------
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {
            System.out.println("Couldn't use native look and feel");
        }       
        
        // start/stop button
        record = new JButton("Start");//normalRecord);

        record.addActionListener(new ActionListener() {
              @Override
	      public void actionPerformed(ActionEvent e) {
                  toggleRecording();
	      }
        });
        record.setEnabled(false);

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
        
        // label for the Tonename
        label = new JLabel(" ",JLabel.CENTER);
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Sans Serif", Font.PLAIN, 21));
        
        // label for previous/next toneneames
        
        Font labelFont = new Font("Sans Serif", Font.PLAIN, 21);
        label1 = new JLabel(" ",JLabel.CENTER);
        label1.setForeground(Color.GRAY);
        label1.setFont(labelFont);
        label2 = new JLabel(" ",JLabel.CENTER);
        label2.setForeground(Color.DARK_GRAY);
        label2.setFont(labelFont);
        label3 = new JLabel(" ",JLabel.CENTER);
        label3.setForeground(Color.DARK_GRAY);
        label3.setFont(labelFont);
        label4 = new JLabel(" ",JLabel.CENTER);
        label4.setForeground(Color.GRAY);
        label4.setFont(labelFont);

        // bg image
        //JLabel bg = new JLabel(new ImageIcon(getClass().getResource("/assets/ui_bg.png")));
       // bg.setBackground(alphaZero);
        
        // little red arrow in the tone display
        JLabel fIndicator = new JLabel(new ImageIcon(getClass().getResource("/assets/pointer_f.png")));
        
        // offset indicator
        pIndicator = new PointerDisplay();
        
        // fft equalizer
        equalizer = new Visualizer(Visualizer.Type.EQUALIZER);
        
        // waveform display
        waveform = new Visualizer(Visualizer.Type.WAVEFORM);
        
        JPanel controls = new JPanel( new GridBagLayout() );
        
        GridBagConstraints c = new GridBagConstraints();
        
        // tone name indicators
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weighty = 0;
        c.weightx = 0.2;
        c.insets = new Insets( 5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        controls.add(label1, c);
        
        c.gridx = 1;
        controls.add( label2, c );
        
        c.gridx = 2;
        controls.add( fIndicator, c );
        controls.add( label, c );
        
        c.gridx = 3;
        controls.add(label3, c);
        
        c.gridx = 4;
        controls.add(label4, c);
        
        // Record button
        c.gridx = 0;
        c.gridy = 4;
        c.gridheight = 2;
        c.gridwidth = 2;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        controls.add(record, c);
        
        // spinners
        c.gridx = 2;
        c.gridheight = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        controls.add( bfSpin, c);
        
        c.gridy = 5;
        controls.add( toSpin, c);
        
        GridLayout mainLayout = new GridLayout(2, 2, 10, 10);
        JPanel content = new JPanel();
        content.setLayout( mainLayout );
        
        content.add( pIndicator );
        content.add( waveform );
        content.add( controls );
        content.add( equalizer );
        
        setTitle("PerfectTone");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        ArrayList icons = new ArrayList();
        icons.add(new ImageIcon(getClass().getResource("/assets/logo.png")).getImage());
        icons.add(new ImageIcon(getClass().getResource("/assets/logo_32.png")).getImage());
        icons.add(new ImageIcon(getClass().getResource("/assets/logo_16.png")).getImage());
        icons.add(new ImageIcon(getClass().getResource("/assets/logo_64.png")).getImage());
        
        setIconImages(icons);

        getContentPane().add(content);
        setSize(450, 430);

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
                label1.setText(" ");
                label2.setText(" ");
                label3.setText(" ");
                label4.setText(" ");
                pIndicator.setAngle(0);
                stabilizer.reset();
                this.repaint();
            }
            else {
                a.startRecording();
                a.arthread.setEventTarget(this);
                a.arthread.addEventListener(this);
                a.resumeRecording();
                record.setText("Stop");
            }
            
            recording = !recording;
        }
    }
    
    @Override
    public void handleInputEvent( EventObject e, float[] data) {
        waveform.updateData( data );
    }
    
    @Override
    public void handleTransformEvent(EventObject e, Tone[] freq) {
        stabilizer.setNewTone(freq);
        equalizer.updateFrequencies( freq );
        if(recording) {
            Tone temp = stabilizer.getTone();
            label.setText(temp.getAbsoluteName());
            if(temp.getFrequency()>0) {
                label1.setText(temp.getAbsoluteName(-2));
                label2.setText(temp.getAbsoluteName(-1));
            }
            else {
                label1.setText(" ");
                label2.setText(" ");
            }
            if(temp.getFrequency()<a.getAudioFormat().getSampleRate()/2) {
                label3.setText(temp.getAbsoluteName(1));
                label4.setText(temp.getAbsoluteName(2));
            }
            else {
                label3.setText(" ");
                label4.setText(" ");
            }
            pIndicator.setAngle(temp.getOffset());
            this.repaint();
        }
    }
}
