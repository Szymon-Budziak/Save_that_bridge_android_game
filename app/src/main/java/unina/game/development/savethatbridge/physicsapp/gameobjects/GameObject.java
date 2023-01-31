package unina.game.development.savethatbridge.physicsapp;

import android.graphics.Bitmap;

import com.google.fpl.liquidfun.Body;
import unina.game.development.savethatbridge.physicsapp.general.Box;

public abstract class GameObject {
    public Body body;
    public String name;
    protected GameWorld gw;

    public GameObject(GameWorld gw) {
        this.gw = gw;
    }

    public boolean draw(Bitmap buffer) {
        if (body != null) {
            // Physical position of the center
            float x = body.getPositionX(), y = body.getPositionY(), angle = body.getAngle();
            // Cropping
            Box view = gw.currentView;
            if (x > view.xMin && x < view.xMax && y > view.yMin && y < view.yMax) {
                // Screen position
                float screen_x = gw.worldToFrameBufferX(x);
                float screen_y = gw.worldToFrameBufferY(y);
                this.draw(buffer, screen_x, screen_y, angle);
                return true;
            } else return false;
        } else {
            this.draw(buffer, 0, 0, 0);
            return true;
        }
    }

    public abstract void draw(Bitmap buf, float x, float y, float angle);

    @Override
    public String toString() {
        return name;
    }
}
