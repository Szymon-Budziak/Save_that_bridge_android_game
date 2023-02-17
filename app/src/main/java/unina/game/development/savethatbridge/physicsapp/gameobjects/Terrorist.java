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

import unina.game.development.savethatbridge.physicsapp.general.AndroidFastRenderView;
import unina.game.development.savethatbridge.physicsapp.general.GameWorld;

public class Terrorist extends GameObject {
    private final Canvas canvas;
    private final RectF dest = new RectF();
    private final Bitmap bitmap;
    private final Rect src;

    private int sprite = 0;
    private long updateTime = 0;
    private boolean allBombsPlanted = false;
    private final float screenSemiWidth, screenSemiHeight;
    private static final int FRAME_WIDTH = 46;

    public Terrorist(GameWorld gw, float x, float y) {
        super(gw);
        int width = 3;
        int height = 3;

        this.canvas = new Canvas(gw.getBitmapBuffer());
        this.src = new Rect(0, 150, FRAME_WIDTH, 200);
        this.screenSemiHeight = gw.toPixelsYLength(height) / 2;
        this.screenSemiWidth = gw.toPixelsXLength(width) / 2;

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.kinematicBody);
        bodyDef.setLinearVelocity(new Vec2((float) 3, 0));

        // a body
        this.body = gw.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(true);
        this.name = "Terrorist";
        this.body.setUserData(this);

        // prevents scaling and sets terrorist to a picture
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.terrorist, options);

        // clean up native objects
        bodyDef.delete();
    }

    private void updateSprite() {
        this.src.left = this.sprite * FRAME_WIDTH;
        this.src.right = this.sprite * FRAME_WIDTH + FRAME_WIDTH;
        this.sprite = (this.sprite + 1) % 8;
    }

    // draw terrorist
    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.updateTime++;
        if (this.updateTime == 6) {
            updateSprite();
            this.updateTime = 0;
        }

        checkBombPlanted();

        checkRemoveTerrorist();

        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.dest.top = y - this.screenSemiHeight;
        this.dest.bottom = y + this.screenSemiHeight;
        this.dest.right = x + this.screenSemiWidth;
        this.dest.left = x - this.screenSemiWidth;
        this.canvas.drawBitmap(this.bitmap, this.src, this.dest, null);
        this.canvas.restore();
    }

    private void checkBombPlanted() {
        if (!this.allBombsPlanted && this.body.getPositionX() > GameWorld.getBomb().body.getPositionX()) {
            AndroidFastRenderView.setIsSpawningBomb(true);
            this.allBombsPlanted = true;
        }
    }

    private void checkRemoveTerrorist() {
        if (this.body.getPositionX() > this.gw.getPhysicalSize().getxMax() - 1) {
            AndroidFastRenderView.setIsRemovingTerrorist(true);
        }
    }
}