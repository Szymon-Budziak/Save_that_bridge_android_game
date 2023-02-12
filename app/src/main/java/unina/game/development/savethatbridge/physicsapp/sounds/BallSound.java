package unina.game.development.savethatbridge.physicsapp.sounds;

import unina.game.development.savethatbridge.logic.Audio;
import unina.game.development.savethatbridge.logic.Music;

public class BallSound {
    private static Music ballSound;

    public static void initialize(Audio audio) {
        ballSound = audio.newMusic("ball.wav");
        ballSound.setVolume(0.5f);
    }

    public static Music getSound() {
        return ballSound;
    }
}
