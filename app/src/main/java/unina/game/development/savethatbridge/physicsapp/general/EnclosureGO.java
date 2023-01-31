package unina.game.development.savethatbridge.physicsapp.general;

import android.graphics.Bitmap;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.PolygonShape;

import unina.game.development.savethatbridge.physicsapp.gameobjects.GameObject;

public class EnclosureGO extends GameObject {
    private static final float THICKNESS = 0;

    public EnclosureGO(GameWorld gw, float xMin, float xMax, float yMin, float yMax) {
        super(gw);

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();

        // default position is (0,0) and default type is staticBody
        this.body = gw.world.createBody(bodyDef);
        this.name = "Enclosure";
        this.body.setUserData(this);

        PolygonShape polygonShape = new PolygonShape();
        // top
        polygonShape.setAsBox(xMax - xMin, THICKNESS, xMin + (xMax - xMin) / 2, yMin, 0);
        this.body.createFixture(polygonShape, 0); // no density needed
        // bottom
        polygonShape.setAsBox(xMax - xMin, THICKNESS, xMin + (xMax - xMin) / 2, yMax, 0);
        this.body.createFixture(polygonShape, 0);
        // left
        polygonShape.setAsBox(THICKNESS, yMax - yMin, xMin, yMin + (yMax - yMin) / 2, 0);
        this.body.createFixture(polygonShape, 0);
        // right
        polygonShape.setAsBox(THICKNESS, yMax - yMin, xMax, yMin + (yMax - yMin) / 2, 0);
        this.body.createFixture(polygonShape, 0);

        // clean up native objects
        bodyDef.delete();
        polygonShape.delete();
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
    }
}