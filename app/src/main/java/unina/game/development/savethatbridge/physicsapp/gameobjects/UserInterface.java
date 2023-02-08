package unina.game.development.savethatbridge.physicsapp.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;

import unina.game.development.savethatbridge.physicsapp.general.GameWorld;

public class UserInterface extends GameObject {
    private static int instances = 0;

    private final Canvas canvas;
    private final Paint paint;
    private static int level = 0;

    public UserInterface(GameWorld gw) {
        super(gw);

        instances++;

        this.canvas = new Canvas(gw.getBuffer());
        this.paint = new Paint();

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setType(BodyType.staticBody);

        // a body
        this.body = gw.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(false);
        this.name = "UI" + instances;
        this.body.setUserData(this);

        int color = Color.argb(255, 0, 0, 0);
        this.paint.setColor(color);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.paint.setTextSize(Math.abs(this.gw.getScreenSize().getyMax() / 30 - this.gw.getScreenSize().getyMax() / 20));
        this.paint.setTextAlign(Paint.Align.CENTER);

        // clean up native objects
        bodyDef.delete();
    }

    public static void setLevel(int lvl) {
        level = lvl;
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.canvas.drawText("Level : " + level, this.gw.getScreenSize().getxMax() / 8, this.gw.getScreenSize().getyMax() / 30, this.paint);
        this.canvas.restore();
    }
}
