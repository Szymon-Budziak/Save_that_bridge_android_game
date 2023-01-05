package unina.game.development.savethatbridge.physicsapp;

import android.util.Log;

import unina.game.development.savethatbridge.logic.Sound;
import unina.game.development.savethatbridge.liquidfun.Body;
import unina.game.development.savethatbridge.liquidfun.Contact;
import unina.game.development.savethatbridge.liquidfun.ContactListener;
import unina.game.development.savethatbridge.liquidfun.Fixture;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by mfaella on 01/03/16.
 */
public class MyContactListener extends ContactListener {

    private Collection<Collision> cache = new HashSet<>();

    public Collection<Collision> getCollisions() {
        Collection<Collision> result = new HashSet<>(cache);
        cache.clear();
        return result;
    }

    /**
     * Warning: this method runs inside world.step
     * Hence, it cannot change the physical world.
     */
    @Override
    public void beginContact(Contact contact) {
        //Log.d("MyContactListener", "Begin contact");
        Fixture fa = contact.getFixtureA(), fb = contact.getFixtureB();
        Body ba = fa.getBody(), bb = fb.getBody();
        Object userdataA = ba.getUserData(), userdataB = bb.getUserData();
        GameObject a = (GameObject) userdataA, b = (GameObject) userdataB;

        // TO DO: use an object pool instead
        cache.add(new Collision(a, b));

        // Sound sound = CollisionSounds.getSound(a.getClass(), b.getClass());
        //if (sound!=null)
        //    sound.play(0.7f);
        // Log.d("MyContactListener", "contact bwt " + a.name + " and " + b.name);
    }
}
