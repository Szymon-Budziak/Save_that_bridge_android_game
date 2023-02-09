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
    private final RectF dest = new RectF();
    private final Bitmap bitmap;

    private final float width;
    private final float height;
    private final float xMinRoad;

    public Road(GameWorld gw, float xMin, float xMax, float yMin, float yMax) {
        super(gw);

        instances++;
        this.canvas = new Canvas(gw.getBitmapBuffer());
        this.width = Math.abs(xMax - xMin);
        this.height = Math.abs(yMax - yMin);
        this.xMinRoad = xMin;

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(xMin + this.width / 2, yMin + this.height / 2);

        // a body
        this.body = gw.getWorld().createBody(bodyDef);
        this.name = "Road" + instances;
        this.body.setUserData(this);

        // road's shape
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(this.width / 2, this.height / 2);
        this.body.createFixture(polygonShape, 0);

        // prevents scaling and sets road to a picture
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.road, o);

        // clean up native objects
        bodyDef.delete();
        polygonShape.delete();
    }

    // getters
    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    // draw road
    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        this.canvas.save();
        this.dest.top = 200;
        this.dest.bottom = 400;
        if (this.xMinRoad > 0) {
            this.dest.right = 600;
            this.dest.left = 500;
        } else {
            this.dest.right = 100;
            this.dest.left = 0;
        }
        this.canvas.drawBitmap(this.bitmap, null, this.dest, null);
        this.canvas.restore();
    }
}