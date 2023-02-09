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

public class BridgeReinforcement extends GameObject {
    private static final float density = 3f;
    private static final float friction = 0.1f;
    private static final float restitution = 0.4f;
    private final float screenSemiWidth, screenSemiHeight;
    private static int instances = 0;

    private final Canvas canvas;
    private final Paint paint;

    private final RectF dest = new RectF();
    private final Bitmap bitmap;
    private final boolean hasAnchor = false;

    public BridgeReinforcement(GameWorld gw, float x, float y, float width, float height, float angle) {
        super(gw);
        instances++;

        this.canvas = new Canvas(gw.getBuffer());
        this.paint = new Paint();
        this.screenSemiHeight = gw.toPixelsYLength(height) / 2;
        this.screenSemiWidth = gw.toPixelsXLength(width) / 2;

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.dynamicBody);

        // a body
        this.body = gw.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(false);
        this.body.setTransform(x, y, angle);
        this.name = "BridgeReinforcement" + instances;
        this.body.setUserData(this);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(polygonShape);
        fixtureDef.setFriction(friction);
        fixtureDef.setRestitution(restitution);
        fixtureDef.setDensity(density);
        this.body.createFixture(fixtureDef);

        if (this.hasAnchor) {
            int color = Color.argb(200, 250, 0, 0);
            this.paint.setColor(color);
            this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        }

        // prevents scaling
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.box, o);

        // clean up native objects
        bodyDef.delete();
        polygonShape.delete();
        fixtureDef.delete();
    }

    public void setColor(boolean selected) {
        int color;
        if (selected) {
            color = Color.argb(200, 0, 250, 0);
        } else {
            color = Color.argb(200, 250, 0, 0);
        }
        this.paint.setColor(color);
    }


    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.dest.left = x - this.screenSemiWidth;
        this.dest.bottom = y + this.screenSemiHeight;
        this.dest.right = x + this.screenSemiWidth;
        this.dest.top = y - this.screenSemiHeight;
        this.canvas.drawBitmap(this.bitmap, null, this.dest, null);
        if (this.hasAnchor)
            this.canvas.drawCircle(x, y, this.gw.toPixelsXLength(Anchor.getWidth() - 0.1f) / 2, paint);
        this.canvas.restore();
    }
}