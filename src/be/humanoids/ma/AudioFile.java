
package be.humanoids.ma;

import java.io.*;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
/**
 *
 * @author Markus Jordi
 */
public class AudioFile
{
    File file;
    
    AudioFile(String path) {
        file = new File(path);
    }
    
    /**
     * Write a raw Audiostream to a file
     */
    public void writeFile(ByteArrayOutputStream data,AudioFormat format) {
       byte[] rawAudioData = data.toByteArray();
       InputStream inputStream = new ByteArrayInputStream(rawAudioData);
       AudioInputStream audioInputStream = new AudioInputStream(inputStream, format, rawAudioData.length/format.getFrameSize());
       
       try {
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
       }
       catch(IOException e) {
           System.out.println("Error when writing to file: "+e);
       }
    }
    
    /**
     * Read an existing audio file
     */
    /*public ByteArrayOutputStream readFile(path) {
        
    }*/
}
