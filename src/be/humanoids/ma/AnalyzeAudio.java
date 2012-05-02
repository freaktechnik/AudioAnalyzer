package be.humanoids.ma;

public class AnalyzeAudio {

    public static void main(String[] args) {
        AudioInput a = new AudioInput();
        a.startRecording();
        
        AudioFile af = new AudioFile();
        af.writeFile(a.stopRecording(),a.getAudioFormat(),"TestFile");
        
    }
}
