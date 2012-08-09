package be.humanoids.ma;

public class AnalyzeAudio {

    public static void main(String[] args) {
        Window mainWin = Window.getWindow();
        AudioInput a = new AudioInput();
        a.startRecording();
        mainWin.setAudioInput(a);        
    }
}
