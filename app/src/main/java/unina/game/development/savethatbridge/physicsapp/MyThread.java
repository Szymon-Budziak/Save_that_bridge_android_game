package unina.game.development.savethatbridge.physicsapp;

import android.util.Log;

import unina.game.development.savethatbridge.liquidfun.Fixture;
import unina.game.development.savethatbridge.liquidfun.RayCastCallback;
import unina.game.development.savethatbridge.liquidfun.Vec2;

public class MyThread extends Thread {
    public volatile int counter;
    private GameWorld gw;

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
        this.gw.world.rayCast(listener, -10, 0, 10, 0);
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(3000);
                counter++;
                Log.i("MyThread", "counter: " + counter);
                // inverts gravity
                testRayCasting();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}