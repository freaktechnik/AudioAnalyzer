package be.humanoids.ma;

public class AnalyzeAudio {

    public static void main(String[] args) {
        AudioInput a = new AudioInput();
        a.startRecording();
        
        AudioFile af = new AudioFile("E:/Users/Martin/Temp/TestFile.wav");
        af.writeFile(a.stopRecording(),a.getAudioFormat());
        
    }
}
