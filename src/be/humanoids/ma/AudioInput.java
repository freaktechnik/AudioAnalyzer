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
  AudioFormat audioFormat;
  TargetDataLine targetDataLine;
  AudioRecorderThread thread = new AudioRecorderThread(targetDataLine);
    
    AudioInput() {
        audioFormat = getAudioFormat();
    }
    
    /**
     * Makes the system ready to capture sound from the input line and starts
     * the recording thread
     */
    public void startRecording() {
        try{
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class,audioFormat);
            targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
           
            thread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }
    
    public ByteArrayOutputStream stopRecording() {
        return thread.stopRecording();
    }
    
    public AudioFormat getAudioFormat() {
        float sampleRate = 44100.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 1;
        //1,2
        boolean signed = true;
        //true,false
        boolean bigEndian = false;
        //true,false
        return new AudioFormat(
                        sampleRate,
                        sampleSizeInBits,
                        channels,
                        signed,
                        bigEndian);
    }
}