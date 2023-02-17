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

public class Bridge extends GameObject {
    private static final float DENSITY = 7f;
    private static final float FRICTION = 0.1f;
    private static final float RESTITUTION = 0.4f;
    private static int INSTANCE_COUNT = 0;

    private final Canvas canvas;
    private final Paint paint;
    private final RectF dest = new RectF();
    private final Bitmap bitmap;

    private final float screenSemiHeight, screenSemiWidth;
    private boolean hasAnchor = false;

    public Bridge(GameWorld gw, float x, float y, float width, float height) {
        super(gw);
        INSTANCE_COUNT++;

        this.canvas = new Canvas(gw.getBitmapBuffer());
        this.paint = new Paint();
        this.screenSemiHeight = gw.toPixelsYLength(height) / 2;
        this.screenSemiWidth = gw.toPixelsXLength(width) / 2;

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x + width / 2, y);
        bodyDef.setType(BodyType.dynamicBody);

        // a body
        this.body = gw.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(false);
        this.name = "Bridge" + INSTANCE_COUNT;
        this.body.setUserData(this);

        // bridge's shape
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(polygonShape);
        fixtureDef.setFriction(FRICTION);
        fixtureDef.setRestitution(RESTITUTION);
        fixtureDef.setDensity(DENSITY);
        this.body.createFixture(fixtureDef);

        // color of the bridge anchors
        int color = Color.argb(200, 255, 0, 0);
        this.paint.setColor(color);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);

        // prevents scaling and sets bridge decks to a picture
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.box, options);

        // clean up native objects
        bodyDef.delete();
        polygonShape.delete();
        fixtureDef.delete();
    }

    // getters
    public boolean getHasAnchor() {
        return this.hasAnchor;
    }

    // setters
    public void setBridgeAnchorColor(boolean selected) {
        int color;
        if (selected) {
            color = Color.argb(200, 0, 250, 0);
        } else {
            color = Color.argb(200, 250, 0, 0);
        }
        this.paint.setColor(color);
    }

    public void setHasAnchor(boolean hasAnchor) {
        this.hasAnchor = hasAnchor;
    }

    // draw bridge
    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.dest.top = y - this.screenSemiHeight;
        this.dest.bottom = y + this.screenSemiHeight;
        this.dest.right = x + this.screenSemiWidth;
        this.dest.left = x - this.screenSemiWidth;
        this.canvas.drawBitmap(this.bitmap, null, this.dest, null);
        if (this.hasAnchor)
            this.canvas.drawCircle(x, y, this.gw.toPixelsXLength(Anchor.getWidth() - 0.1f) / 2, this.paint);
        this.canvas.restore();
    }
}