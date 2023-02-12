package unina.game.development.savethatbridge.physicsapp.sounds;

import unina.game.development.savethatbridge.logic.Audio;
import unina.game.development.savethatbridge.logic.Music;

public class ExplosionSound {
    private static Music explosionSound;

    public static void initialize(Audio audio) {
        explosionSound = audio.newMusic("explosion.wav");
        explosionSound.setVolume(0.5f);
    }

    public static Music getSound() {
        return explosionSound;
    }
}
