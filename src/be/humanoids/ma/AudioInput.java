package be.humanoids.ma;

import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * An audio input is a sample recorded from the audio input of the soundcard.
 * @author Martin
 */
public class AudioInput {
    
  ByteArrayOutputStream byteArrayOutputStream;
  LocalAudioFormat audioFormat;
  TargetDataLine targetDataLine;
  public AudioRecorderThread arthread;
  Thread thread;
  DataLine.Info dataLineInfo;
  static final int samplelength = (int)Math.pow(2,13);
  static final int startf = 50;
    
    AudioInput() {
        audioFormat = new LocalAudioFormat();
        dataLineInfo = new DataLine.Info(TargetDataLine.class,audioFormat.getAudioFormat());
    }
    
    /**
     * Makes the system ready to capture sound from the input line and starts
     * the recording thread
     */
    public void startRecording() {
        try{
            if(targetDataLine==null||!targetDataLine.isOpen()) {
                targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
                targetDataLine.open(audioFormat.getAudioFormat());
            }
                arthread = new AudioRecorderThread(startf,samplelength);
                thread = new Thread(arthread);
                arthread.setT(targetDataLine);
            
        } catch(Exception e) {
            System.out.println("Error while inizializing recording: "+e);
            System.exit(0);
        }
    }
    
    /**
     * stops capturing the input audio
     * 
     * @return Captured audio
     */
    public byte[] stopRecording() {
        return arthread.stop();
    }
    
    /**
     * starts capturing
     */
    public void resumeRecording() {
        thread.start();
    }
    
    /**
     * 
     * @return used AudioFormat Object
     */
    public AudioFormat getAudioFormat() {
        return audioFormat.getAudioFormat();
    }
}