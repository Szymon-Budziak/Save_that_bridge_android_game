package unina.game.development.savethatbridge.physicsapp;

import android.graphics.Bitmap;
import android.graphics.Paint;

import unina.game.development.savethatbridge.liquidfun.BodyDef;
import unina.game.development.savethatbridge.liquidfun.PolygonShape;

// A static box, usually encloses the whole world.
public class EnclosureGO extends GameObject {
    private static final float THICKNESS = 0;

    private Paint paint = new Paint();
    private float xmin, xmax, ymin, ymax;
    private float screen_xmin, screen_xmax, screen_ymin, screen_ymax;

    public EnclosureGO(GameWorld gw, float xmin, float xmax, float ymin, float ymax) {
        super(gw);
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        this.screen_xmax = gw.worldToFrameBufferX(xmax - THICKNESS);
        this.screen_xmin = gw.worldToFrameBufferX(xmin + THICKNESS);
        this.screen_ymax = gw.worldToFrameBufferY(ymax - THICKNESS);
        this.screen_ymin = gw.worldToFrameBufferY(ymin + THICKNESS);

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        // default position is (0,0) and default type is staticBody
        this.body = gw.world.createBody(bodyDef);
        this.name = "Enclosure";
        this.body.setUserData(this);

        PolygonShape box = new PolygonShape();
        // top
        box.setAsBox(xmax - xmin, THICKNESS, xmin + (xmax - xmin) / 2, ymin, 0); // last is rotation angle
        this.body.createFixture(box, 0); // no density needed
        // bottom
        box.setAsBox(xmax - xmin, THICKNESS, xmin + (xmax - xmin) / 2, ymax, 0);
        this.body.createFixture(box, 0);
        // left
        box.setAsBox(THICKNESS, ymax - ymin, xmin, ymin + (ymax - ymin) / 2, 0);
        this.body.createFixture(box, 0);
        // right
        box.setAsBox(THICKNESS, ymax - ymin, xmax, ymin + (ymax - ymin) / 2, 0);
        this.body.createFixture(box, 0);

        // clean up native objects
        bodyDef.delete();
        box.delete();
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
    }
}
