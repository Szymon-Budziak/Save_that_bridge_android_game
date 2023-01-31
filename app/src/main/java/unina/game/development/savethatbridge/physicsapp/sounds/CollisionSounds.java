package unina.game.development.savethatbridge.physicsapp;

import android.util.SparseArray;

import unina.game.development.savethatbridge.logic.Audio;
import unina.game.development.savethatbridge.logic.Sound;
import unina.game.development.savethatbridge.physicsapp.general.DynamicBoxGO;
import unina.game.development.savethatbridge.physicsapp.general.EnclosureGO;

public class CollisionSounds {
    private static Sound metallicSound;
    private static Sound dumbSound;

    private static SparseArray<Sound> map;

    private static int myHash(Class<?> a, Class<?> b) {
        return a.hashCode() ^ b.hashCode();
    }

    public static void init(Audio audio) {
        metallicSound = audio.newSound("urto1.wav");
        dumbSound = audio.newSound("urto2.wav");
        map = new SparseArray<>();

        map.put(myHash(DynamicBoxGO.class, DynamicBoxGO.class), metallicSound);
        map.put(myHash(DynamicBoxGO.class, EnclosureGO.class), dumbSound);
    }

    public static Sound getSound(Class<?> a, Class<?> b) {
        int hash = myHash(a, b);
        return map.get(hash);
    }
}
