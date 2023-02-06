package unina.game.development.savethatbridge.physicsapp.sounds;

import unina.game.development.savethatbridge.logic.Audio;
import unina.game.development.savethatbridge.logic.Music;

public class BallSound {
    private static Music ball;

    public static void init(Audio audio) {
        ball = audio.newMusic("ball.wav");
    }

    public static Music getBall() {
        return ball;
    }
}