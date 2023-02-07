package unina.game.development.savethatbridge.physicsapp.sounds;

import unina.game.development.savethatbridge.logic.Audio;
import unina.game.development.savethatbridge.logic.Music;

public class BallSound {
    private static Music ballSound;

    public static void init(Audio audio) {
        ballSound = audio.newMusic("ball.wav");
    }

    public static Music getBallSound() {
        return ballSound;
    }
}