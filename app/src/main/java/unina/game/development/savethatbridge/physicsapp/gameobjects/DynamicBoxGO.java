package unina.game.development.savethatbridge.physicsapp.gameobjects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import unina.game.development.savethatbridge.R;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;

import unina.game.development.savethatbridge.physicsapp.general.GameWorld;

public class DynamicBoxGO extends GameObject {
    private static final float width = 2.5f, height = 2.5f;
    private static final float density = 2.5f;
    private static final float friction = 0.1f;
    private static final float restitution = 0.4f;
    private static int instances = 0;

    private final Canvas canvas;
    private final Paint paint;
    private final RectF dest = new RectF();
    private final Bitmap bitmap;

    private final float screenSemiWidth, screenSemiHeight;

    public DynamicBoxGO(GameWorld gw, float x, float y) {
        super(gw);
        instances++;

        this.canvas = new Canvas(gw.getBuffer());
        this.paint = new Paint();
        this.screenSemiWidth = gw.toPixelsXLength(width) / 2;
        this.screenSemiHeight = gw.toPixelsYLength(height) / 2;

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.dynamicBody);

        // a body
        this.body = gw.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(false);
        this.name = "DynamicBox" + instances;
        this.body.setUserData(this);

        // dynamic box shape
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(polygonShape);
        fixtureDef.setFriction(friction);
        fixtureDef.setRestitution(restitution);
        fixtureDef.setDensity(density);
        this.body.createFixture(fixtureDef);

        // color of the dynamic box
        int green = (int) (255 * Math.random());
        int color = Color.argb(200, 255, green, 0);
        this.paint.setColor(color);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);

        // prevents scaling and sets displays dynamic box
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.icona, o);

        // clean up native objects
        bodyDef.delete();
        polygonShape.delete();
        fixtureDef.delete();
    }

    // draw dynamic box
    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.dest.top = y - this.screenSemiHeight;
        this.dest.bottom = y + this.screenSemiHeight;
        this.dest.right = x + this.screenSemiWidth;
        this.dest.left = x - this.screenSemiWidth;
        this.canvas.drawBitmap(this.bitmap, null, this.dest, null);
        this.canvas.restore();
    }
}