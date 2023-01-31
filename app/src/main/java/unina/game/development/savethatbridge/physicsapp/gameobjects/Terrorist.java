package unina.game.development.savethatbridge.physicsapp.gameobjects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import unina.game.development.savethatbridge.R;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.Vec2;

import unina.game.development.savethatbridge.physicsapp.AndroidFastRenderView;
import unina.game.development.savethatbridge.physicsapp.GameObject;
import unina.game.development.savethatbridge.physicsapp.GameWorld;

public class Terrorist extends GameObject {
    private static float screenSemiWidth, screenSemiHeight;
    private final Canvas canvas;

    private Rect src;
    private final RectF dest = new RectF();
    private final Bitmap bitmap;
    private int sprite = 1;

    private int test_timer = 0;
    private boolean bombPlanted = false;

    public Terrorist(GameWorld gw, float x, float y) {
        super(gw);
        int width = 2;
        int height = 2;

        this.canvas = new Canvas(gw.buffer);
        screenSemiHeight = gw.toPixelsYLength(height) / 2;
        screenSemiWidth = gw.toPixelsXLength(width) / 2;

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.kinematicBody);
        bodyDef.setLinearVelocity(new Vec2((float) 3, 0));

        // a body
        this.body = gw.world.createBody(bodyDef);
        this.body.setSleepingAllowed(true);
        this.name = "Terrorist";
        this.body.setUserData(this);

        // Prevents scaling
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.activity.getResources(), R.drawable.terrorist, o);

        // clean up native objects
        bodyDef.delete();
    }

    private void updateAnimation() {
        if (this.sprite == 5)
            this.sprite = 0;
        else
            this.sprite++;
        this.src.top = this.sprite * 15;
        this.src.bottom = this.sprite * 15 + 15;
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.src = new Rect(0, 0, 21, 15);
        this.test_timer++;
        if (this.test_timer == 8) {
            updateAnimation();
            this.test_timer = 0;
        }

        if (this.body.getPositionX() > this.gw.physicalSize.xMax - 1) {
            AndroidFastRenderView.removeTerrorist = true;
        }

        if (!this.bombPlanted && this.body.getPositionX() > GameWorld.bomb.body.getPositionX()) {
            AndroidFastRenderView.spawnBomb = true;
            this.bombPlanted = true;
        }

        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.dest.left = x - screenSemiWidth;
        this.dest.bottom = y + screenSemiHeight;
        this.dest.right = x + screenSemiWidth;
        this.dest.top = y - screenSemiHeight;
        this.canvas.drawBitmap(this.bitmap, this.src, this.dest, null);
        this.canvas.restore();
    }
}