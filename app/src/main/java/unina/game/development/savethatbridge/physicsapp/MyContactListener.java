package unina.game.development.savethatbridge.physicsapp;

import unina.game.development.savethatbridge.liquidfun.Body;
import unina.game.development.savethatbridge.liquidfun.Contact;
import unina.game.development.savethatbridge.liquidfun.ContactListener;
import unina.game.development.savethatbridge.liquidfun.Fixture;

import java.util.Collection;
import java.util.HashSet;

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
        Fixture fa = contact.getFixtureA(), fb = contact.getFixtureB();
        Body ba = fa.getBody(), bb = fb.getBody();
        Object userdataA = ba.getUserData(), userdataB = bb.getUserData();
        GameObject a = (GameObject) userdataA, b = (GameObject) userdataB;

        cache.add(new Collision(a, b));
    }
}
