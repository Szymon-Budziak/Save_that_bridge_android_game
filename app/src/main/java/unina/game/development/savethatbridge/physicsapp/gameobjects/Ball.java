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
    private static final float width = 1f, height = 1f, density = 0.7f;
    private static final float friction = 1f;
    private static final float restitution = 0.1f;
    private static float screen_semi_width, screen_semi_height;
    private static int instances = 0;

    private final Canvas canvas;
    private final Paint paint;
    private final RectF dest = new RectF();

    public Ball(GameWorld gw, float x, float y) {
        super(gw);
        instances++;

        this.canvas = new Canvas(gw.getBuffer());
        this.paint = new Paint();
        screen_semi_height = gw.toPixelsYLength(height) / 2;
        screen_semi_width = gw.toPixelsXLength(width) / 2;

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.dynamicBody);
        bodyDef.setLinearVelocity(new Vec2((float) 9, 0));

        // a body
        this.body = gw.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(false);
        this.name = "Ball" + instances;
        this.body.setUserData(this);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(width / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(circleShape);
        fixtureDef.setFriction(friction);
        fixtureDef.setRestitution(restitution);
        fixtureDef.setDensity(density);
        this.body.createFixture(fixtureDef);

        int color = Color.argb(255, 10, 10, 10);
        this.paint.setColor(color);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);

        // clean up native objects
        bodyDef.delete();
        circleShape.delete();
        fixtureDef.delete();
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.dest.left = x - screen_semi_width;
        this.dest.bottom = y + screen_semi_height;
        this.dest.right = x + screen_semi_width;
        this.dest.top = y - screen_semi_height;
        this.canvas.drawCircle(x, y, screen_semi_width, this.paint);
        this.canvas.restore();
    }
}
