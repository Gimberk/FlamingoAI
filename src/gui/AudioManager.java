package gui;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class AudioManager {
    public static void playCheck(){
        try {
            final File file = new File("assets/audio/move-check.wav");
            AudioInputStream theme = AudioSystem.getAudioInputStream(file.toURI().toURL());
            Clip clip = AudioSystem.getClip();
            clip.open(theme);
            clip.start();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void playMove(){
        try {
            final File file = new File("assets/audio/move-self.wav");
            AudioInputStream theme = AudioSystem.getAudioInputStream(file.toURI().toURL());
            Clip clip = AudioSystem.getClip();
            clip.open(theme);
            clip.start();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void playCapture(){
        try {
            final File file = new File("assets/audio/capture.wav");
            AudioInputStream theme = AudioSystem.getAudioInputStream(file.toURI().toURL());
            Clip clip = AudioSystem.getClip();
            clip.open(theme);
            clip.start();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void playStart(){
        try {
            final File file = new File("assets/audio/game-start.wav");
            AudioInputStream theme = AudioSystem.getAudioInputStream(file.toURI().toURL());
            Clip clip = AudioSystem.getClip();
            clip.open(theme);
            clip.start();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void playEnd(){
        try {
            final File file = new File("assets/audio/game-end.wav");
            AudioInputStream theme = AudioSystem.getAudioInputStream(file.toURI().toURL());
            Clip clip = AudioSystem.getClip();
            clip.open(theme);
            clip.start();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void playCastle(){
        try {
            final File file = new File("assets/audio/castle.wav");
            AudioInputStream theme = AudioSystem.getAudioInputStream(file.toURI().toURL());
            Clip clip = AudioSystem.getClip();
            clip.open(theme);
            clip.start();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
