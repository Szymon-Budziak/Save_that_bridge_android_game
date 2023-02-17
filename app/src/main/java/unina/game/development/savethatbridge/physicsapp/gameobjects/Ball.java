package unina.game.development.savethatbridge.physicsapp.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.Vec2;

import unina.game.development.savethatbridge.physicsapp.general.GameWorld;

public class Ball extends GameObject {
    private static final float WIDTH = 1.5f;
    private static final float HEIGHT = 1.5f;
    private static final float DENSITY = 0.8f;
    private static final float FRICTION = 1f;
    private static final float RESTITUTION = 0.1f;
    private static int INSTANCE_COUNT = 0;

    private final Canvas canvas;
    private final Paint paint;
    private final RectF dest = new RectF();

    private final float screenSemiWidth, screenSemiHeight;

    public Ball(GameWorld gw, float x, float y) {
        super(gw);
        INSTANCE_COUNT++;

        this.canvas = new Canvas(gw.getBitmapBuffer());
        this.paint = new Paint();
        this.screenSemiWidth = gw.toPixelsXLength(WIDTH) / 2;
        this.screenSemiHeight = gw.toPixelsYLength(HEIGHT) / 2;

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.dynamicBody);
        bodyDef.setLinearVelocity(new Vec2(12f, 12f));

        // a body
        this.body = gw.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(false);
        this.name = "Ball" + INSTANCE_COUNT;
        this.body.setUserData(this);

        // ball's shape
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(WIDTH / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(circleShape);
        fixtureDef.setFriction(FRICTION);
        fixtureDef.setRestitution(RESTITUTION);
        fixtureDef.setDensity(DENSITY);
        this.body.createFixture(fixtureDef);

        // color of the ball
        int color = Color.argb(255, 0, 0, 0);
        this.paint.setColor(color);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);

        // clean up native objects
        bodyDef.delete();
        circleShape.delete();
        fixtureDef.delete();
    }

    // draw ball
    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.dest.top = y - this.screenSemiHeight;
        this.dest.bottom = y + this.screenSemiHeight;
        this.dest.right = x + this.screenSemiWidth;
        this.dest.left = x - this.screenSemiWidth;
        this.canvas.drawCircle(x, y, this.screenSemiWidth, this.paint);
        this.canvas.restore();
    }
}
