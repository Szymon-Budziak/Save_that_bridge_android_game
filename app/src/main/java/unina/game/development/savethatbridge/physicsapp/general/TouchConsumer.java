package unina.game.development.savethatbridge.physicsapp.general;

import android.util.Log;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.MouseJoint;
import com.google.fpl.liquidfun.QueryCallback;

import unina.game.development.savethatbridge.logic.Input;
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
    private final GameWorld gameWorld;
    private final QueryCallback touchQueryCallback = new TouchQueryCallback();

    // physical units, semi-side of a square around the touch point
    private final static float POINTER_SIZE = 0.5f;

    // scale{X,Y} are the scale factors from pixels to physics simulation coordinates
    public TouchConsumer(GameWorld gw) {
        this.gameWorld = gw;
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

        float x = this.gameWorld.setScreenToWorldX(event.x);
        float y = this.gameWorld.setScreenToWorldY(event.y);

        Log.d("MultiTouchHandler", "touch down at " + x + ", " + y);

        this.touchedFixture = null;
        this.gameWorld.getWorld().queryAABB(this.touchQueryCallback, x - POINTER_SIZE, y - POINTER_SIZE, x + POINTER_SIZE, y + POINTER_SIZE);
        if (this.touchedFixture == null) return;

        Body touchedBody = this.touchedFixture.getBody();
        Object userData = touchedBody.getUserData();
        if (userData == null) return;

        GameObject touchedGameObject = (GameObject) userData;
        this.activePointerID = pointerId;
        Log.d("MultiTouchHandler", "touched game object " + touchedGameObject.name);

        if (touchedGameObject instanceof Anchor || touchedGameObject instanceof Bridge) {
            handleAnchorOrBridgeTouch(touchedGameObject);
        } else {
            handleOtherGameObjectTouch();
        }
    }

    private void handleAnchorOrBridgeTouch(GameObject touchedGameObject) {
        if (touchedGameObject instanceof Bridge && ((Bridge) touchedGameObject).getHasAnchor()) {
            handleBridgeWithAnchorTouch(touchedGameObject);
        } else if (touchedGameObject instanceof Anchor) {
            handleAnchorTouch(touchedGameObject);
        }
    }

    private void handleBridgeWithAnchorTouch(GameObject touchedGameObject) {
        if (this.oldObject != null && this.oldObject instanceof Anchor) {
            this.gameWorld.addBridgeReinforcement(this.oldObject, touchedGameObject);
            ((Anchor) this.oldObject).setAnchorColor(false);
            this.oldObject = null;
        } else {
            if (this.oldObject != null && this.oldObject instanceof Bridge && ((Bridge) this.oldObject).getHasAnchor()) {
                ((Bridge) this.oldObject).setBridgeAnchorColor(false);
            }
            this.oldObject = touchedGameObject;
            ((Bridge) touchedGameObject).setBridgeAnchorColor(true);
        }
    }

    private void handleAnchorTouch(GameObject touchedGameObject) {
        if (this.oldObject != null && this.oldObject instanceof Bridge && ((Bridge) this.oldObject).getHasAnchor()) {
            this.gameWorld.addBridgeReinforcement(touchedGameObject, this.oldObject);
            ((Bridge) this.oldObject).setBridgeAnchorColor(false);
            this.oldObject = null;
        } else {
            if (this.oldObject != null && this.oldObject instanceof Anchor) {
                ((Anchor) this.oldObject).setAnchorColor(false);
            }
            this.oldObject = touchedGameObject;
            ((Anchor) touchedGameObject).setAnchorColor(true);
        }
    }

    private void handleOtherGameObjectTouch() {
        if (this.oldObject != null) {
            if (this.oldObject instanceof Anchor) ((Anchor) this.oldObject).setAnchorColor(false);
            else if (this.oldObject instanceof Bridge)
                ((Bridge) this.oldObject).setBridgeAnchorColor(false);
            this.oldObject = null;
        }
    }

    private void consumeTouchUp(Input.TouchEvent event) {
        if (this.mouseJoint != null && event.pointer == this.activePointerID) {
            Log.d("MultiTouchHandler", "Releasing joint");
            this.gameWorld.getWorld().destroyJoint(this.mouseJoint);
            this.mouseJoint = null;
            this.activePointerID = 0;
        }
    }

    private void consumeTouchMove(Input.TouchEvent event) {
        float x = this.gameWorld.setScreenToWorldX(event.x);
        float y = this.gameWorld.setScreenToWorldY(event.y);
        if (this.mouseJoint != null && event.pointer == this.activePointerID) {
            Log.d("MultiTouchHandler", "active pointer moved to " + x + ", " + y);
            this.mouseJoint.setTarget(x, y);
        }
    }
}