package unina.game.development.savethatbridge.physicsapp;

import unina.game.development.savethatbridge.liquidfun.Body;
import unina.game.development.savethatbridge.liquidfun.Joint;
import unina.game.development.savethatbridge.liquidfun.PrismaticJointDef;
import unina.game.development.savethatbridge.liquidfun.RevoluteJointDef;

/**
 * Created by mfaella on 27/02/16.
 */
public class MyPrismaticJoint {
    Joint joint;

    public MyPrismaticJoint(GameWorld gw, Body a, Body b) {
        PrismaticJointDef jointDef = new PrismaticJointDef();
        jointDef.setBodyA(a);
        jointDef.setBodyB(b);
        jointDef.setLocalAnchorA(0, 0);
        jointDef.setLocalAnchorB(0, 0);
        jointDef.setLocalAxisA(1f, 1f);
        // add friction
        jointDef.setEnableMotor(true);
        jointDef.setMotorSpeed(0f);
        jointDef.setMaxMotorForce(10f);
        this.joint = gw.world.createJoint(jointDef);

        jointDef.delete();
    }
}
