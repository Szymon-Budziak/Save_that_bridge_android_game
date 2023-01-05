package unina.game.development.savethatbridge.physicsapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import unina.game.development.savethatbridge.liquidfun.Body;
import unina.game.development.savethatbridge.liquidfun.BodyDef;
import unina.game.development.savethatbridge.liquidfun.BodyType;
import unina.game.development.savethatbridge.liquidfun.FixtureDef;
import unina.game.development.savethatbridge.liquidfun.Joint;
import unina.game.development.savethatbridge.liquidfun.PolygonShape;
import unina.game.development.savethatbridge.liquidfun.RevoluteJointDef;
import unina.game.development.savethatbridge.liquidfun.Vec2;

/**
 * Created by mfaella on 27/02/16.
 */
public class MyRevoluteJoint {
    Joint joint;

    public MyRevoluteJoint(GameWorld gw, Body a, Body b, float xa, float ya, float xb, float yb) {
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.setBodyA(a);
        jointDef.setBodyB(b);
        jointDef.setLocalAnchorA(xa, ya);
        jointDef.setLocalAnchorB(xb, yb);
        jointDef.setCollideConnected(false);
        jointDef.setEnableMotor(false);
        joint = gw.world.createJoint(jointDef);
        jointDef.delete();
    }
}
