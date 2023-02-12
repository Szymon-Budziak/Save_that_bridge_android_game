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
    private static int instances = 0;

    private final Canvas canvas;
    private final Paint paint;
    private final RectF dest = new RectF();
    private final Bitmap bitmap;
    private final GameWorld gameWorld;
    private MyRevoluteJoint joint;

    private final float screenSemiWidth, screenSemiHeight;
    private final float x, y;

    public Bomb(GameWorld gw, float x, float y, MyRevoluteJoint joint, Resources resources) {
        super(gw);
        instances++;

        this.x = x;
        this.y = y;
        this.joint = joint;
        this.gameWorld = gw;

        this.canvas = new Canvas(gw.getBitmapBuffer());
        this.paint = new Paint();

        float size = (resources.getInteger(R.integer.worldXMax) - resources.getInteger(R.integer.worldXMin)) / 20;
        this.screenSemiHeight = gw.toPixelsYLength(size) / 2;
        this.screenSemiWidth = gw.toPixelsXLength(size) / 2;

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.kinematicBody);

        // a body
        this.body = gw.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(true);
        this.name = "Bomb" + instances;
        this.body.setUserData(this);

        this.paint.setTextSize(200);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);

        // prevents scaling and sets bomb to a picture
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.bomb, options);

        // clean up native objects
        bodyDef.delete();
    }

    public synchronized void explode() {
        Music explosion = ExplosionSound.getSound();
        explosion.stop();
        explosion.play();
        GameWorld.getJointsToDestroy().add(this.joint.getJoint());
        GameWorld.getGameJoints().remove(this.joint);
        GameWorld.setPreviousObjectsDestroyed(false);
        this.gameWorld.summonBombParticles(this.x, this.y);
        this.joint = null;
    }

    // draw bomb
    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.dest.top = y - 2 * this.screenSemiHeight;
        this.dest.bottom = y;
        this.dest.right = x + this.screenSemiWidth;
        this.dest.left = x - this.screenSemiWidth;
        this.canvas.drawBitmap(this.bitmap, null, this.dest, null);

        displayBombTimer();

        this.canvas.restore();
    }

    // display bomb bombTimer after the terrorist crosses the bridge
    private void displayBombTimer() {
        if (GameWorld.bombTimer == 4) {
            this.paint.setARGB(255, 255, 255, 0);
            this.canvas.drawText("3", this.gameWorld.getScreenSize().getxMax() / 7, this.gameWorld.getScreenSize().getyMax() / 4, this.paint);
        } else if (GameWorld.bombTimer == 3) {
            this.paint.setARGB(255, 255, 125, 0);
            this.canvas.drawText("2", this.gameWorld.getScreenSize().getxMax() / 7, this.gameWorld.getScreenSize().getyMax() / 4, this.paint);
        } else if (GameWorld.bombTimer == 2) {
            this.paint.setARGB(255, 255, 0, 0);
            this.canvas.drawText("1", this.gameWorld.getScreenSize().getxMax() / 7, this.gameWorld.getScreenSize().getyMax() / 4, this.paint);
        } else if (GameWorld.bombTimer == 1) {
            this.paint.setARGB(255, 255, 0, 0);
            this.canvas.drawText("BOOM", this.gameWorld.getScreenSize().getxMax() / 100, this.gameWorld.getScreenSize().getyMax() / 4, this.paint);
        }
    }
}