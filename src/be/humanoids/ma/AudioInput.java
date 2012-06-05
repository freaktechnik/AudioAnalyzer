/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.humanoids.ma;

import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Martin
 */
public class AudioInput {
    
  ByteArrayOutputStream byteArrayOutputStream;
  LocalAudioFormat audioFormat;
  TargetDataLine targetDataLine;
  AudioRecorderThread thread;
    
    AudioInput() {
        audioFormat = new LocalAudioFormat();
        thread = new AudioRecorderThread(targetDataLine);
    }
    
    /**
     * Makes the system ready to capture sound from the input line and starts
     * the recording thread
     */
    public void startRecording() {
        try{
            if(targetDataLine==null) {
                DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class,audioFormat.getAudioFormat());
                targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
                targetDataLine.open(audioFormat.getAudioFormat());
            }
            thread.run(); // Error on this line (Nullpointer)
            
        } catch(Exception e) {
            System.out.println("Error while inizializing recording: "+e);
            System.exit(0);
        }
    }
    
    /**
     * stops capturing the input audio
     * @return Captured audio
     */
    public ByteArrayOutputStream stopRecording() {
        return thread.stop();
    }
    
    public AudioFormat getAudioFormat() {
        return audioFormat.getAudioFormat();
    }
}