package unina.game.development.savethatbridge.physicsapp.general;

import android.util.Log;

import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.RayCastCallback;
import com.google.fpl.liquidfun.Vec2;

import unina.game.development.savethatbridge.physicsapp.gameobjects.GameObject;

public class MyThread extends Thread {
    private volatile int counter;
    private final GameWorld gw;

    public MyThread(GameWorld gw) {
        this.gw = gw;
    }

    private void testRayCasting() {
        Log.i("MyThread", "Objects across the short middle line:");
        RayCastCallback listener = new RayCastCallback() {
            @Override
            public float reportFixture(Fixture f, Vec2 point, Vec2 normal, float fraction) {
                Log.i("MyThread", ((GameObject) f.getBody().getUserData()).name + " (" + fraction + ")");
                return 1;
            }
        };
        this.gw.getWorld().rayCast(listener, -10, 0, 10, 0);
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(3000);
                this.counter++;
                Log.i("MyThread", "counter: " + this.counter);
                // inverts gravity
                testRayCasting();
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    // getters
    public int getCounter() {
        return this.counter;
    }

    // setters
    public void setCounter(int counter) {
        this.counter = counter;
    }
}