package be.humanoids.ma;

public class AnalyzeAudio {

    public static void main(String[] args) {
        Window mainWin = Window.getWindow();
        AudioInput a = new AudioInput();
        mainWin.setAudioInput(a);
        a.startRecording();
    }
}
