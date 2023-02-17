package unina.game.development.savethatbridge.physicsapp.general;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import unina.game.development.savethatbridge.R;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.ParticleSystemDef;
import com.google.fpl.liquidfun.World;

import unina.game.development.savethatbridge.logic.Input;
import unina.game.development.savethatbridge.logic.Music;
import unina.game.development.savethatbridge.logic.impl.TouchHandler;
import unina.game.development.savethatbridge.physicsapp.gameobjects.Ball;
import unina.game.development.savethatbridge.physicsapp.gameobjects.Bomb;
import unina.game.development.savethatbridge.physicsapp.gameobjects.Anchor;
import unina.game.development.savethatbridge.physicsapp.gameobjects.Bridge;
import unina.game.development.savethatbridge.physicsapp.gameobjects.BridgeReinforcement;
import unina.game.development.savethatbridge.physicsapp.gameobjects.GameObject;
import unina.game.development.savethatbridge.physicsapp.gameobjects.Terrorist;
import unina.game.development.savethatbridge.physicsapp.activities.StartingActivity;
import unina.game.development.savethatbridge.physicsapp.sounds.BallSound;

public class GameWorld {
    private final Canvas canvas;
    private final World world;
    private final RectF dest;
    private Bitmap bitmap;
    private final Bitmap bitmapBuffer;
    private final int bufferWidth, bufferHeight;
    private static GameObject worldBorder;
    private final StartingActivity activity;

    private final Box physicalSize, screenSize;
    private final MyContactListener contactListener;
    private final TouchConsumer touchConsumer;
    private TouchHandler touchHandler;

    // particles
    private final ParticleSystem particleSystem;

    // GameObjects
    private final ArrayList<GameObject> gameObjects = new ArrayList<>();
    private static GameObject ball;
    private final Music ballSound = BallSound.getSound();

    // bridge
    public static ArrayList<GameObject> road;
    public static ArrayList<GameObject> bridge = new ArrayList<>();
    public static ArrayList<GameObject> gameBridgeAnchors = new ArrayList<>();
    public static ArrayList<GameObject> bridgeConstructions = new ArrayList<>();
    private static float deckHeight;
    private static float bridgeLength;

    // bomb and terrorist
    private static Bomb bomb;
    private static Terrorist terrorist;

    // joints
    public static ArrayList<MyRevoluteJoint> gameJoints = new ArrayList<>();
    private static final ArrayList<Joint> jointsToDestroy = new ArrayList<>();
    private static final ArrayList<Body> objectsToDestroy = new ArrayList<>();

    // flags and values
    private static boolean previousObjectsDestroyed = true;
    private static boolean readyForNextLevel = true;
    private static boolean verified = false;
    private static boolean canPlace = true;
    public static int bombTimer = 5;
    private static int planksToPlace = -1;
    private static int level = 1;

    public GameWorld(Box physicalSize, Box screenSize, StartingActivity theActivity) {
        this.physicalSize = physicalSize;
        this.screenSize = screenSize;
        this.activity = theActivity;

        Resources res = this.activity.getResources();
        this.bufferWidth = res.getInteger(R.integer.frameBufferWidth);
        this.bufferHeight = res.getInteger(R.integer.frameBufferHeight);

        this.bitmapBuffer = Bitmap.createBitmap(this.bufferWidth, this.bufferHeight, Bitmap.Config.ARGB_8888);
        this.world = new World(res.getInteger(R.integer.gravityX), res.getInteger(R.integer.gravityY));

        ParticleSystemDef particleSystemDef = new ParticleSystemDef();
        particleSystemDef.setDestroyByAge(true);
        this.particleSystem = this.world.createParticleSystem(particleSystemDef);
        this.particleSystem.setRadius(0.3f);
        this.particleSystem.setMaxParticleCount(1000);
        particleSystemDef.delete();

        this.contactListener = new MyContactListener();
        this.world.setContactListener(this.contactListener);
        this.touchConsumer = new TouchConsumer(this);

        this.canvas = new Canvas(this.bitmapBuffer);
        this.dest = new RectF();
        this.dest.top = 0;
        this.dest.bottom = 400;
        this.dest.right = 600;
        this.dest.left = 0;

        bridgeLength = 2 * res.getInteger(R.integer.worldXMax) - 3;
        deckHeight = this.physicalSize.getyMax() / 30;
    }

    // getters
    public Bitmap getBitmapBuffer() {
        return this.bitmapBuffer;
    }

    public World getWorld() {
        return this.world;
    }

    public Box getPhysicalSize() {
        return this.physicalSize;
    }

    public static Bomb getBomb() {
        return bomb;
    }

    public ParticleSystem getParticleSystem() {
        return this.particleSystem;
    }

    public static Terrorist getTerrorist() {
        return terrorist;
    }

    public static ArrayList<GameObject> getBridge() {
        return bridge;
    }

    public StartingActivity getActivity() {
        return this.activity;
    }

    public static int getPlanksToPlace() {
        return planksToPlace;
    }

    public static boolean isReadyForNextLevel() {
        return readyForNextLevel;
    }

    public static ArrayList<MyRevoluteJoint> getGameJoints() {
        return gameJoints;
    }

    public static ArrayList<Joint> getJointsToDestroy() {
        return jointsToDestroy;
    }

    public static float getBridgeLength() {
        return bridgeLength;
    }

    public static float getDeckHeight() {
        return deckHeight;
    }

    // setters
    public static void setBomb(Bomb bomb) {
        GameWorld.bomb = bomb;
    }

    public static void setPlanksToPlace(int planksToPlace) {
        GameWorld.planksToPlace = planksToPlace;
    }

    public static void setCanPlace(boolean canPlace) {
        GameWorld.canPlace = canPlace;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static void setWorldBorder(GameObject worldBorder) {
        GameWorld.worldBorder = worldBorder;
    }

    public static void setTerrorist(Terrorist terrorist) {
        GameWorld.terrorist = terrorist;
    }

    // conversions between screen and physical coordinates
    public float toPixelsXLength(float x) {
        return x / this.physicalSize.getWidth() * this.bufferWidth;
    }

    public float toPixelsYLength(float y) {
        return y / this.physicalSize.getHeight() * this.bufferHeight;
    }

    public float setScreenToWorldX(float x) {
        return this.physicalSize.getxMin() + x * (this.physicalSize.getWidth() / this.screenSize.getWidth());
    }

    public float setScreenToWorldY(float y) {
        return this.physicalSize.getyMin() + y * (this.physicalSize.getHeight() / this.screenSize.getHeight());
    }

    public float setWorldToFrameX(float x) {
        return (x - this.physicalSize.getxMin()) / this.physicalSize.getWidth() * this.bufferWidth;
    }

    public float setWorldToFrameY(float y) {
        return (y - this.physicalSize.getyMin()) / this.physicalSize.getHeight() * this.bufferHeight;
    }

    public void setTouchHandler(TouchHandler touchHandler) {
        this.touchHandler = touchHandler;
    }

    public static synchronized void setPreviousObjectsDestroyed(boolean b) {
        previousObjectsDestroyed = b;
    }

    // specific GameWorld functions
    public synchronized GameObject addGameObject(GameObject obj) {
        this.gameObjects.add(obj);
        return obj;
    }

    public synchronized void removeGameObject(GameObject obj) {
        if (obj != null) {
            objectsToDestroy.add(obj.body);
            gameObjects.remove(obj);
        }
    }

    public synchronized void removePreviousObjects() {
        removeObjects(road);
        removeObjects(bridge);
        removeObjects(gameBridgeAnchors);
        removeObjects(bridgeConstructions);
        addJointsToDestroy(gameJoints);
        removeGameObject(worldBorder);
        setPreviousObjectsDestroyed(false);
        readyForNextLevel = false;
    }

    private void removeObjects(List<GameObject> objects) {
        for (GameObject object : objects) {
            this.removeGameObject(object);
        }
    }

    private void addJointsToDestroy(List<MyRevoluteJoint> joints) {
        for (MyRevoluteJoint joint : joints) {
            jointsToDestroy.add(joint.getJoint());
        }
    }

    public synchronized void update(float elapsedTime) {
        this.world.step(elapsedTime, 8, 3, 3);
        handleJointsToDestroy();
        handleCollisions(this.contactListener.getCollisions());
        handleTouchEvents();
    }

    private void handleJointsToDestroy() {
        if (!getPreviousObjectsDestroyed()) {
            destroyJoints(jointsToDestroy);
            destroyBodies(objectsToDestroy);
            setPreviousObjectsDestroyed(true);
            if (!readyForNextLevel) {
                readyForNextLevel = true;
            }
        }
    }

    private void destroyJoints(List<Joint> joints) {
        for (Joint joint : joints) {
            this.world.destroyJoint(joint);
        }
        joints.clear();
    }

    private void destroyBodies(List<Body> bodies) {
        for (Body body : bodies) {
            this.world.destroyBody(body);
        }
        bodies.clear();
    }

    private void handleCollisions(Collection<Collision> collisions) {
        for (Collision event : collisions) {
            if (isEnclosureCollision(event)) {
                if (isBallCollision(event) && !verified) {
                    this.verifyWin(event.getA(), event.getB());
                }
            }
        }
    }

    private boolean isEnclosureCollision(Collision event) {
        return event.getA() instanceof EnclosureGO || event.getB() instanceof EnclosureGO;
    }

    private boolean isBallCollision(Collision event) {
        return event.getA() instanceof Ball || event.getB() instanceof Ball;
    }

    private void handleTouchEvents() {
        for (Input.TouchEvent event : this.touchHandler.getTouchEvents()) {
            this.touchConsumer.consumeTouchEvent(event);
        }
    }

    public synchronized void render() {
        this.canvas.save();
        this.canvas.drawBitmap(this.bitmap, null, dest, null);
        this.canvas.restore();
        for (GameObject obj : this.gameObjects) {
            obj.draw(this.bitmapBuffer);
        }
    }

    public synchronized void setupNextLevel() {
        Level gameLevel = new Level(this);
        if (level == 1) gameLevel.level1(this);
        else if (level == 2) gameLevel.level2(this);
        else gameLevel.endLevel(this);
    }

    public static synchronized void incrementLevel() {
        level++;
    }

    public static synchronized boolean getPreviousObjectsDestroyed() {
        return previousObjectsDestroyed;
    }

    public synchronized void addReinforcement(GameObject objectA, GameObject objectB) {
        Anchor anchor = ((Anchor) ((objectA instanceof Anchor) ? objectA : objectB));
        Bridge bridge = ((Bridge) ((objectA instanceof Bridge) ? objectA : objectB));
        if (planksToPlace > 0) {
            if (canPlace) {
                float anchorWidth = Anchor.getWidth();
                float hb = deckHeight;
                float dist_ab_x = Math.abs(anchor.body.getPositionX() - bridge.body.getPositionX());
                float dist_ab_y = Math.abs(anchor.body.getPositionY() - bridge.body.getPositionY());
                float width = (float) Math.sqrt(Math.pow(anchor.body.getPositionX() - bridge.body.getPositionX(), 2) + Math.pow(anchor.body.getPositionY() - bridge.body.getPositionY(), 2)) - anchorWidth / 2;
                if (width < 12) {
                    float x = Math.min(anchor.body.getPositionX(), bridge.body.getPositionX()) + dist_ab_x / 2;
                    float y = Math.min(anchor.body.getPositionY(), bridge.body.getPositionY()) + dist_ab_y / 2;
                    float angle = (float) ((anchor.body.getPositionX() < bridge.body.getPositionX()) ? 3.14 / 2 + Math.atan(dist_ab_x / dist_ab_y) : 3.14 / 2 - Math.atan(dist_ab_x / dist_ab_y));
                    BridgeReinforcement reinforcement = new BridgeReinforcement(this, x, y, width, deckHeight, angle);
                    this.addGameObject(reinforcement);
                    bridgeConstructions.add(reinforcement);
                    if (anchor.body.getPositionX() < reinforcement.body.getPositionX() && anchor.body.getPositionY() > reinforcement.body.getPositionY()) { // left road anchor
                        gameJoints.add(new MyRevoluteJoint(this, anchor.body, reinforcement.body, dist_ab_x / 2 - anchorWidth / 2, -dist_ab_y / 2 + anchorWidth, anchorWidth / 2, 0));
                        gameJoints.add(new MyRevoluteJoint(this, bridge.body, reinforcement.body, -dist_ab_x / 2, -dist_ab_y / 2 + hb * 3 / 2, 0, hb));
                    } else if (anchor.body.getPositionX() > reinforcement.body.getPositionX() && anchor.body.getPositionY() > reinforcement.body.getPositionY()) { // right road anchor
                        gameJoints.add(new MyRevoluteJoint(this, anchor.body, reinforcement.body, width / 2, 0, 0, 0));
                        gameJoints.add(new MyRevoluteJoint(this, bridge.body, reinforcement.body, -width / 2, 0, 0, 0));

                    }
                    bridge.setHasAnchor(false);
                    planksToPlace--;
                }
            }
        }
    }

    public synchronized void verifyLevel() {
        verified = false;
        canPlace = false;
        ball = this.addGameObject(new Ball(this, this.physicalSize.getxMin() + 2, 0));
        ballSound.play();
    }

    synchronized void verifyWin(GameObject a, GameObject b) {
        verified = true;
        AndroidFastRenderView.setHasWon((a instanceof EnclosureGO) ? b.body.getPositionY() < 0 : a != null && a.body.getPositionY() < 0);
        this.removeGameObject(ball);
        ballSound.stop();
        AndroidFastRenderView.setIsWinVerified(true);
        setPreviousObjectsDestroyed(false);
    }

    @Override
    protected void finalize() {
        this.world.delete();
    }
}