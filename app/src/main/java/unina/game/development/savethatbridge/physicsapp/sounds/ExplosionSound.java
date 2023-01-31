package unina.game.development.savethatbridge.physicsapp;

import unina.game.development.savethatbridge.logic.Audio;
import unina.game.development.savethatbridge.logic.Music;

public class ExplosionSound {
    private static Music explosion;

    public static void init(Audio audio) {
        explosion = audio.newMusic("explosion.wav");
    }

    public static Music getExplosion() {
        return explosion;
    }
}