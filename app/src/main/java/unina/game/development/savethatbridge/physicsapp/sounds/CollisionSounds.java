package unina.game.development.savethatbridge.physicsapp.sounds;

import android.util.SparseArray;

import unina.game.development.savethatbridge.logic.Audio;
import unina.game.development.savethatbridge.logic.Sound;
import unina.game.development.savethatbridge.physicsapp.gameobjects.DynamicBoxGO;
import unina.game.development.savethatbridge.physicsapp.general.EnclosureGO;

public class CollisionSounds {
    private static int myHash(Class<?> a, Class<?> b) {
        return a.hashCode() ^ b.hashCode();
    }

    public static void init(Audio audio) {
        Sound metallicSound = audio.newSound("urto1.wav");
        Sound dumbSound = audio.newSound("urto2.wav");
        SparseArray<Sound> map = new SparseArray<>();

        map.put(myHash(DynamicBoxGO.class, DynamicBoxGO.class), metallicSound);
        map.put(myHash(DynamicBoxGO.class, EnclosureGO.class), dumbSound);
    }
}