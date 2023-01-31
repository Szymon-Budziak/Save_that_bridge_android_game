package unina.game.development.savethatbridge.physicsapp.general;

import unina.game.development.savethatbridge.physicsapp.gameobjects.GameObject;

public class Collision {
    private final GameObject a, b;

    public Collision(GameObject a, GameObject b) {
        this.a = a;
        this.b = b;
    }

    public GameObject getA() {
        return this.a;
    }

    public GameObject getB() {
        return this.b;
    }

    @Override
    public int hashCode() {
        return this.a.hashCode() ^ this.b.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Collision)) return false;
        Collision otherCollision = (Collision) other;
        return (this.a.equals(otherCollision.a) && this.b.equals(otherCollision.b)) || (this.a.equals(otherCollision.b) && this.b.equals(otherCollision.a));
    }
}
