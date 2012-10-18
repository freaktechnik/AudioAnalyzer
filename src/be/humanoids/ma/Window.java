package be.humanoids.ma;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;
/**
 * the main Window Object, is a singleton.
 * @author Martin
 */
public class Window extends JFrame implements TransformedEventListener {
    private static Window singletonWindow;
    private boolean recording = false;
    private JButton record;
    private JLabel label,label1,label2,label3,label4;
    private PointerDisplay pIndicator;
    private ImageIcon normalRecord;
    private ImageIcon recordingRecord;
    
    private Filter stabilizer;
    AudioInput a;
    
    private Window() {     
        
        stabilizer = new Filter();
        
        // ---------------------
        //          GUI
        // ---------------------
        
        GraphicsEnvironment ge =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gds = ge.getDefaultScreenDevice();
        
        final Color alphaZero = new Color(0, true);
        
        // start/stop button
        normalRecord = new ImageIcon(getClass().getResource("/assets/button_normal.png"));
        recordingRecord = new ImageIcon(getClass().getResource("/assets/button_pressed.png"));
        record = new JButton(normalRecord);
        record.setPressedIcon(recordingRecord);
        record.setOpaque(false);
        record.setUI(new BasicButtonUI());
        record.setBorderPainted(false);
        Dimension bSize = new Dimension(60,65);
        record.setPreferredSize(bSize);
        record.setSize(bSize);
        record.addActionListener(new ActionListener() {
              @Override
	      public void actionPerformed(ActionEvent e) {
                  toggleRecording();
	      }
        });
        record.setEnabled(false);
        
        // close button
        
        JButton close = new JButton(new ImageIcon(getClass().getResource("/assets/close_normal.png")));
        close.setPressedIcon(new ImageIcon(getClass().getResource("/assets/close.png")));
        close.setOpaque(false);
        close.setUI(new BasicButtonUI());
        close.setBorderPainted(false);
        Dimension cSize = new Dimension(22,27);
        close.setPreferredSize(cSize);
        close.setSize(cSize);
        close.addActionListener(new ActionListener() {
              @Override
	      public void actionPerformed(ActionEvent e) {
                  System.exit(0);
	      }
        });
        
        // spinner for ToneOffset
        String[] pitches = {"C","Bb","F","Eb"};
        SpinnerModel tosm = new SpinnerListModel(pitches);
        final JSpinner toSpin = new JSpinner(tosm);
        Dimension sSize = new Dimension(60,20);
        toSpin.setPreferredSize(sSize);
        toSpin.setSize(sSize);
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
        bfSpin.setPreferredSize(sSize);
        bfSpin.setSize(sSize);
        
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
        JLabel bg = new JLabel(new ImageIcon(getClass().getResource("/assets/ui_bg.png")));
        bg.setBackground(alphaZero);
        // little red arrow in the tone display
        JLabel fIndicator = new JLabel(new ImageIcon(getClass().getResource("/assets/pointer_f.png")));
        
        // offset indicator
        pIndicator = new PointerDisplay();
        
        SpringLayout layout = new SpringLayout();
        JPanel content = new JPanel(layout);
        content.setSize(450, 320);
        content.add(record);
        content.add(bfSpin);
        content.add(toSpin);
        content.add(fIndicator);
        content.add(label);
        content.add(label1);
        content.add(label2);
        content.add(label3);
        content.add(label4);
        content.add(pIndicator);
        if(gds.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT));
            content.add(close);
        content.add(bg);
        
        layout.putConstraint(SpringLayout.WEST, record, 40, SpringLayout.WEST, content);
        layout.putConstraint(SpringLayout.NORTH, record, 40, SpringLayout.NORTH, content);
        
        layout.putConstraint(SpringLayout.WEST, bfSpin, 40, SpringLayout.WEST, content);
        layout.putConstraint(SpringLayout.NORTH, bfSpin, 220, SpringLayout.NORTH, content);
        
        layout.putConstraint(SpringLayout.WEST, toSpin, 40, SpringLayout.WEST, content);
        layout.putConstraint(SpringLayout.NORTH, toSpin, 170, SpringLayout.NORTH, content);
        
        layout.putConstraint(SpringLayout.WEST, label, 260, SpringLayout.WEST, content);
        layout.putConstraint(SpringLayout.NORTH, label, 220, SpringLayout.NORTH, content);
        
        layout.putConstraint(SpringLayout.WEST, label1, 160, SpringLayout.WEST, content);
        layout.putConstraint(SpringLayout.NORTH, label1, 220, SpringLayout.NORTH, content);
        layout.putConstraint(SpringLayout.WEST, label2, 210, SpringLayout.WEST, content);
        layout.putConstraint(SpringLayout.NORTH, label2, 220, SpringLayout.NORTH, content);
        layout.putConstraint(SpringLayout.WEST, label3, 310, SpringLayout.WEST, content);
        layout.putConstraint(SpringLayout.NORTH, label3, 220, SpringLayout.NORTH, content);
        layout.putConstraint(SpringLayout.WEST, label4, 360, SpringLayout.WEST, content);
        layout.putConstraint(SpringLayout.NORTH, label4, 220, SpringLayout.NORTH, content);
        
        layout.putConstraint(SpringLayout.WEST, pIndicator, 136, SpringLayout.WEST, content);
        layout.putConstraint(SpringLayout.NORTH, pIndicator, 56, SpringLayout.NORTH, content);
        
        layout.putConstraint(SpringLayout.WEST, fIndicator, 263, SpringLayout.WEST, content);
        layout.putConstraint(SpringLayout.NORTH, fIndicator, 233, SpringLayout.NORTH, content);
        
        layout.putConstraint(SpringLayout.WEST, close, 415, SpringLayout.WEST, content);
        layout.putConstraint(SpringLayout.NORTH, close, 0, SpringLayout.NORTH, content);
        
        content.setOpaque(false);
        
        WindowMover.addMoving(this);
        
        setTitle("PerfectTone");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        ArrayList icons = new ArrayList();
        icons.add(new ImageIcon(getClass().getResource("/assets/logo.png")).getImage());
        icons.add(new ImageIcon(getClass().getResource("/assets/logo_32.png")).getImage());
        icons.add(new ImageIcon(getClass().getResource("/assets/logo_16.png")).getImage());
        icons.add(new ImageIcon(getClass().getResource("/assets/logo_64.png")).getImage());
        
        setIconImages(icons);

        getContentPane().add(content);
        setSize(450, 320);
        if(gds.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT));
            setUndecorated(true);
        setBackground(alphaZero);
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
                record.setIcon(normalRecord);
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
                a.resumeRecording();
                record.setIcon(recordingRecord);
            }
            
            recording = !recording;
        }
    }
    
    @Override
    public void handleTransformEvent(EventObject e, Tone[] freq) {
        stabilizer.setNewTone(freq);
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
