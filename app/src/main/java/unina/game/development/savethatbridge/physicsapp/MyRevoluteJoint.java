package unina.game.development.savethatbridge.physicsapp;

import unina.game.development.savethatbridge.liquidfun.Body;
import unina.game.development.savethatbridge.liquidfun.Joint;
import unina.game.development.savethatbridge.liquidfun.RevoluteJointDef;

public class MyRevoluteJoint {
    Joint joint;

    public MyRevoluteJoint(GameWorld gw, Body a, Body b, float xb, float yb, float xa, float ya) {
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.setBodyA(a);
        jointDef.setBodyB(b);
        jointDef.setLocalAnchorA(xa, ya);
        jointDef.setLocalAnchorB(xb, yb);
        jointDef.setCollideConnected(false);
        jointDef.setEnableMotor(false);
        this.joint = gw.world.createJoint(jointDef);
        jointDef.delete();
    }
}