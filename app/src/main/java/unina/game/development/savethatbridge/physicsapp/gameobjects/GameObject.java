package unina.game.development.savethatbridge.physicsapp.gameobjects;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.fpl.liquidfun.Body;

import unina.game.development.savethatbridge.physicsapp.general.GameWorld;
import unina.game.development.savethatbridge.physicsapp.general.Box;

public abstract class GameObject {
    protected Body body;
    protected String name;
    protected GameWorld gw;

    public GameObject(GameWorld gw) {
        this.gw = gw;
    }

    public boolean draw(Bitmap buffer) {
        if (this.body != null) {
            // Physical position of the center
            float x = this.body.getPositionX();
            float y = this.body.getPositionY();
            float angle = this.body.getAngle();
            // Cropping
            Box view = this.gw.currentView;
            if (x > view.getxMin() && x < view.getxMax() && y > view.getyMin() && y < view.getyMax()) {
                // Screen position
                float screen_x = this.gw.worldToFrameBufferX(x);
                float screen_y = this.gw.worldToFrameBufferY(y);
                this.draw(buffer, screen_x, screen_y, angle);
                return true;
            } else return false;
        } else {
            this.draw(buffer, 0, 0, 0);
            return true;
        }
    }

    public abstract void draw(Bitmap buf, float x, float y, float angle);

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}