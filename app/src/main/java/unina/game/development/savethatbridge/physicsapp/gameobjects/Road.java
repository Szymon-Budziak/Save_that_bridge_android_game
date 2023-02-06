package unina.game.development.savethatbridge.physicsapp.gameobjects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import unina.game.development.savethatbridge.R;
import unina.game.development.savethatbridge.physicsapp.general.GameWorld;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.PolygonShape;

public class Road extends GameObject {
    private static int instances = 0;
    private final Canvas canvas;
    private final Bitmap bitmap;
    private final RectF dest = new RectF();

    private final float width;
    private final float height;

    public Road(GameWorld gw, float xMin, float xMax, float yMin, float yMax) {
        super(gw);

        instances++;
        this.canvas = new Canvas(gw.buffer);

        this.width = Math.abs(xMax - xMin);
        this.height = Math.abs(yMax - yMin);

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(xMin + this.width / 2, yMin + this.height / 2);

        // a body
        this.body = gw.world.createBody(bodyDef);
        this.name = "Road" + instances;
        this.body.setUserData(this);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(this.width / 2, this.height / 2);
        this.body.createFixture(polygonShape, 0);

        // Prevents scaling
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.activity.getResources(), R.drawable.road, o);

        this.dest.top = 200;
        this.dest.bottom = 400;
        if (xMin > 0) {
            this.dest.left = 500;
            this.dest.right = 600;
        } else {
            this.dest.left = 0;
            this.dest.right = 100;
        }

        // clean up native objects
        bodyDef.delete();
        polygonShape.delete();
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.canvas.drawBitmap(this.bitmap, null, this.dest, null);
        this.canvas.restore();
    }
}