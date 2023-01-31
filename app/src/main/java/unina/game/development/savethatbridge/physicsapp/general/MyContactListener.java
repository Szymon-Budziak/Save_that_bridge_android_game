package unina.game.development.savethatbridge.physicsapp.general;

import java.util.Collection;
import java.util.HashSet;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Contact;
import com.google.fpl.liquidfun.ContactListener;
import com.google.fpl.liquidfun.Fixture;

import unina.game.development.savethatbridge.physicsapp.gameobjects.GameObject;

public class MyContactListener extends ContactListener {
    private final Collection<Collision> cache = new HashSet<>();

    public Collection<Collision> getCollisions() {
        Collection<Collision> result = new HashSet<>(this.cache);
        this.cache.clear();
        return result;
    }

    /**
     * Warning: this method runs inside world.step
     * Hence, it cannot change the physical world.
     */
    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA(), fb = contact.getFixtureB();
        Body ba = fa.getBody(), bb = fb.getBody();
        Object userdataA = ba.getUserData(), userdataB = bb.getUserData();
        GameObject a = (GameObject) userdataA, b = (GameObject) userdataB;
        this.cache.add(new Collision(a, b));
    }
}