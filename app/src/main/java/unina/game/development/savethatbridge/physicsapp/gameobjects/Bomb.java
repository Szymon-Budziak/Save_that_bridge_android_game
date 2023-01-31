package unina.game.development.savethatbridge.physicsapp.gameobjects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import unina.game.development.savethatbridge.R;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;

import unina.game.development.savethatbridge.logic.Music;
import unina.game.development.savethatbridge.physicsapp.sounds.ExplosionSound;
import unina.game.development.savethatbridge.physicsapp.general.GameWorld;
import unina.game.development.savethatbridge.physicsapp.general.MyRevoluteJoint;

public class Bomb extends GameObject {
    private static float screenSemiWidth, screenSemiHeight;
    private static int instances = 0;

    private final Canvas canvas;
    private final Paint paint;
    private MyRevoluteJoint joint;
    private final GameWorld gw;
    private final float x, y;

    private final RectF dest = new RectF();
    private final Bitmap bitmap;

    public Bomb(GameWorld gw, float x, float y, MyRevoluteJoint joint, Resources resources) {
        super(gw);

        instances++;
        this.x = x;
        this.y = y;
        this.joint = joint;
        this.gw = gw;

        this.canvas = new Canvas(gw.buffer);
        this.paint = new Paint();

        float size = (resources.getInteger(R.integer.worldXMax) - resources.getInteger(R.integer.worldXMin)) / 20;
        screenSemiHeight = gw.toPixelsYLength(size) / 2;
        screenSemiWidth = gw.toPixelsXLength(size) / 2;

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.kinematicBody);

        // a body
        this.body = gw.world.createBody(bodyDef);
        this.body.setSleepingAllowed(true);
        this.name = "Bomb" + instances;
        this.body.setUserData(this);

        this.paint.setTextSize(150);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);

        // Prevents scaling
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.activity.getResources(), R.drawable.bomb, o);

        // clean up native objects
        bodyDef.delete();
    }

    public synchronized void explode() {
        Music explosion = ExplosionSound.getExplosion();
        explosion.stop();
        explosion.play();
        GameWorld.jointsToDestroy.add(this.joint.getJoint());
        GameWorld.myJoints.remove(this.joint);
        GameWorld.setOldObjectsRemoved(false);
        this.gw.summonParticles(this.x, this.y);
        this.joint = null;
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.dest.left = x - screenSemiWidth;
        this.dest.bottom = y;
        this.dest.right = x + screenSemiWidth;
        this.dest.top = y - 2 * screenSemiHeight;
        this.canvas.drawBitmap(this.bitmap, null, this.dest, null);

        if (GameWorld.timer == 3) {
            this.paint.setARGB(255, 255, 255, 0);
            this.canvas.drawText("3", this.gw.screenSize.getxMax() / 7, this.gw.screenSize.getyMax() / 4, this.paint);
        } else if (GameWorld.timer == 2) {
            this.paint.setARGB(255, 255, 150, 0);
            this.canvas.drawText("2", this.gw.screenSize.getxMax() / 7, this.gw.screenSize.getyMax() / 4, this.paint);
        } else if (GameWorld.timer == 1) {
            this.paint.setARGB(255, 255, 0, 0);
            this.canvas.drawText("1", this.gw.screenSize.getxMax() / 7, this.gw.screenSize.getyMax() / 4, this.paint);
        }
        this.canvas.restore();
    }
}