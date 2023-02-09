package unina.game.development.savethatbridge.physicsapp.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.FixtureDef;

import unina.game.development.savethatbridge.physicsapp.general.GameWorld;

public class Anchor extends GameObject {
    private static final float width = 0.7f;
    private static final float friction = 0f;
    private static int instances = 0;

    private final Canvas canvas;
    private final Paint paint;
    private final float screenSemiWidth;

    public Anchor(GameWorld gw, float x, float y) {
        super(gw);
        instances++;

        this.canvas = new Canvas(gw.getBuffer());
        this.paint = new Paint();
        this.screenSemiWidth = gw.toPixelsXLength(width) / 2;

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.staticBody);

        // a body
        this.body = gw.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(false);
        this.name = "Anchor" + instances;
        this.body.setUserData(this);

        // anchor's shape
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(width / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(circleShape);
        fixtureDef.setFriction(friction);
        this.body.createFixture(fixtureDef);

        // initial anchor color
        int color = Color.argb(200, 255, 0, 0);
        this.paint.setColor(color);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);

        // clean up native objects
        bodyDef.delete();
        circleShape.delete();
        fixtureDef.delete();
    }

    // getters
    public static float getWidth() {
        return width;
    }

    // setters
    public void setAnchorColor(boolean selected) {
        int color;
        if (selected) {
            color = Color.argb(200, 0, 250, 0);
        } else {
            color = Color.argb(200, 250, 0, 0);
        }
        this.paint.setColor(color);
    }

    // draw anchor
    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.canvas.drawCircle(x, y, this.screenSemiWidth, this.paint);
        this.canvas.restore();
    }
}