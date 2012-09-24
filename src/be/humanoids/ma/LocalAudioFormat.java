package be.humanoids.ma;

/**
 *
 * @author Martin
 */
public class LocalAudioFormat {
    int channels;
    
    LocalAudioFormat() {
        channels = 1;
    }
    
    public void setAudioFormat(String param,int value) {
        switch(param) {
            case "channels":
                if(0<value&&value<3)
                    channels = value;
                break;
        }
    }
    
    
    public javax.sound.sampled.AudioFormat getAudioFormat() {
        float sampleRate = 22050.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 8;
        //8,16
        boolean signed = true;

        boolean bigEndian = true;

        return new javax.sound.sampled.AudioFormat(
                        sampleRate,
                        sampleSizeInBits,
                        channels,
                        signed,
                        bigEndian);
    }
}
