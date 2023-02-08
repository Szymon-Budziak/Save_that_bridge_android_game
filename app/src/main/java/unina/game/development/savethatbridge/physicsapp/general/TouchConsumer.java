package unina.game.development.savethatbridge.physicsapp.general;

import android.util.Log;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.MouseJoint;
import com.google.fpl.liquidfun.MouseJointDef;
import com.google.fpl.liquidfun.QueryCallback;

import unina.game.development.savethatbridge.logic.Game;
import unina.game.development.savethatbridge.logic.Input;
import unina.game.development.savethatbridge.physicsapp.gameobjects.DynamicBoxGO;
import unina.game.development.savethatbridge.physicsapp.gameobjects.GameObject;
import unina.game.development.savethatbridge.physicsapp.gameobjects.Anchor;
import unina.game.development.savethatbridge.physicsapp.gameobjects.Bridge;

// Takes care of user interaction: pulls objects using a Mouse Joint.
public class TouchConsumer {
    // keep track of what we are dragging
    private MouseJoint mouseJoint;
    private int activePointerID;
    private Fixture touchedFixture;
    private GameObject oldObject;

    private final GameWorld gw;
    private final QueryCallback touchQueryCallback = new TouchQueryCallback();

    // physical units, semi-side of a square around the touch point
    private final static float POINTER_SIZE = 0.5f;

    // scale{X,Y} are the scale factors from pixels to physics simulation coordinates
    public TouchConsumer(GameWorld gw) {
        this.gw = gw;
        this.oldObject = null;
    }

    private class TouchQueryCallback extends QueryCallback {
        public boolean reportFixture(Fixture fixture) {
            touchedFixture = fixture;
            return true;
        }
    }

    public void consumeTouchEvent(Input.TouchEvent event) {
        switch (event.type) {
            case Input.TouchEvent.TOUCH_DOWN:
                consumeTouchDown(event);
                break;
            case Input.TouchEvent.TOUCH_UP:
                consumeTouchUp(event);
                break;
            case Input.TouchEvent.TOUCH_DRAGGED:
                consumeTouchMove(event);
                break;
        }
    }

    private void consumeTouchDown(Input.TouchEvent event) {
        int pointerId = event.pointer;

        if (this.mouseJoint != null) return;

        float x = this.gw.screenToWorldX(event.x);
        float y = this.gw.screenToWorldY(event.y);

        Log.d("MultiTouchHandler", "touch down at " + x + ", " + y);

        this.touchedFixture = null;
        this.gw.getWorld().queryAABB(this.touchQueryCallback, x - POINTER_SIZE, y - POINTER_SIZE, x + POINTER_SIZE, y + POINTER_SIZE);
        if (this.touchedFixture != null) {
            // From fixture to GO
            Body touchedBody = this.touchedFixture.getBody();
            Object userData = touchedBody.getUserData();
            if (userData != null) {
                GameObject touchedGO = (GameObject) userData;
                this.activePointerID = pointerId;
                Log.d("MultiTouchHandler", "touched game object " + touchedGO.name);
                if (touchedGO instanceof Anchor || touchedGO instanceof Bridge) {
                    if (touchedGO instanceof Bridge && ((Bridge) touchedGO).getHasAnchor()) {
                        objectIsBridge(touchedGO);
                    } else if (touchedGO instanceof Anchor) {
                        objectIsAnchor(touchedGO);
                    }
                } else {
                    if (this.oldObject != null) {
                        if (this.oldObject instanceof Anchor)
                            ((Anchor) this.oldObject).setColor(false);
                        else if (this.oldObject instanceof Bridge)
                            ((Bridge) this.oldObject).setColor(false);
                        this.oldObject = null;
                    }
                    if (touchedGO instanceof DynamicBoxGO) {
                        setupMouseJoint(x, y, touchedBody);
                    }
                }
            }
        }
    }

    private void objectIsBridge(GameObject touchedGO) {
        if (this.oldObject != null && this.oldObject instanceof Anchor) {
            this.gw.addReinforcement(this.oldObject, touchedGO);
            ((Anchor) this.oldObject).setColor(false);
            this.oldObject = null;
        } else {
            if (this.oldObject != null && this.oldObject instanceof Bridge && ((Bridge) this.oldObject).getHasAnchor())
                ((Bridge) this.oldObject).setColor(false);
            this.oldObject = touchedGO;
            ((Bridge) touchedGO).setColor(true);
        }
    }

    private void objectIsAnchor(GameObject touchedGO) {
        if (this.oldObject != null && this.oldObject instanceof Bridge && ((Bridge) this.oldObject).getHasAnchor()) {
            this.gw.addReinforcement(touchedGO, this.oldObject);
            ((Bridge) this.oldObject).setColor(false);
            this.oldObject = null;
        } else {
            if (this.oldObject != null && this.oldObject instanceof Anchor)
                ((Anchor) this.oldObject).setColor(false);
            this.oldObject = touchedGO;
            ((Anchor) touchedGO).setColor(true);
        }
    }

    // Set up a mouse joint between the touched GameObject and the touch coordinates (x,y)
    private void setupMouseJoint(float x, float y, Body touchedBody) {
        MouseJointDef mouseJointDef = new MouseJointDef();
        mouseJointDef.setBodyA(touchedBody);
        mouseJointDef.setBodyB(touchedBody);
        mouseJointDef.setMaxForce(500 * touchedBody.getMass());
        mouseJointDef.setTarget(x, y);
        this.mouseJoint = this.gw.getWorld().createMouseJoint(mouseJointDef);
    }

    private void consumeTouchUp(Input.TouchEvent event) {
        if (this.mouseJoint != null && event.pointer == this.activePointerID) {
            Log.d("MultiTouchHandler", "Releasing joint");
            this.gw.getWorld().destroyJoint(this.mouseJoint);
            this.mouseJoint = null;
            this.activePointerID = 0;
        }
    }

    private void consumeTouchMove(Input.TouchEvent event) {
        float x = this.gw.screenToWorldX(event.x);
        float y = this.gw.screenToWorldY(event.y);
        if (this.mouseJoint != null && event.pointer == this.activePointerID) {
            Log.d("MultiTouchHandler", "active pointer moved to " + x + ", " + y);
            this.mouseJoint.setTarget(x, y);
        }
    }
}