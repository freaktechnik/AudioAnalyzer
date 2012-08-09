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
 * An audio input is a sample recorded from the audio input of the soundcard.
 * @author Martin
 */
public class AudioInput {
    
  ByteArrayOutputStream byteArrayOutputStream;
  LocalAudioFormat audioFormat;
  TargetDataLine targetDataLine;
  AudioRecorderThread thread;
  DataLine.Info dataLineInfo;
    
    AudioInput() {
        audioFormat = new LocalAudioFormat();
        thread = new AudioRecorderThread();
        dataLineInfo = new DataLine.Info(TargetDataLine.class,audioFormat.getAudioFormat());
    }
    
    /**
     * Makes the system ready to capture sound from the input line and starts
     * the recording thread
     */
    public void startRecording() {
        try{
            if(targetDataLine==null) {
                targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
                targetDataLine.open(audioFormat.getAudioFormat());
            }
                thread.setT(targetDataLine);
                thread.run();
            
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
    
    /**
     * starts capturing
     */
    public void resumeRecording() {
        thread.run();
    }
    
    public AudioFormat getAudioFormat() {
        return audioFormat.getAudioFormat();
    }
}