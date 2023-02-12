package unina.game.development.savethatbridge.physicsapp.general;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import unina.game.development.savethatbridge.physicsapp.gameobjects.Bridge;
import unina.game.development.savethatbridge.physicsapp.gameobjects.GameObject;

public class AndroidFastRenderView extends SurfaceView implements Runnable {
    private final GameWorld gameworld;
    private final Bitmap framebuffer;
    private final SurfaceHolder surfaceHolder;
    private final Rect dest = new Rect();

    // rendering
    private Thread renderThread = null;
    private volatile boolean running = false;

    // level and win verification
    private static boolean verifyLevel = false;
    private static boolean nextLevel = false;
    private static boolean verifyWin = false;
    private static boolean win = false;

    // player
    private static boolean hasPlayerStarted = false;
    private static boolean hasPlayerFinished = false;

    // bombTimer
    private static int ballTimer = 0;
    private static int timerVal = 10;
    private static boolean decrTimerVal = false;

    // terrorist and bomb
    private static boolean removeTerrorist = false;
    private static boolean spawnBomb = false;

    public AndroidFastRenderView(Context context, GameWorld gw) {
        super(context);
        this.gameworld = gw;
        this.framebuffer = gw.getBitmapBuffer();
        this.surfaceHolder = getHolder();
    }

    public void resume() {
        this.running = true;
        this.renderThread = new Thread(this);
        this.renderThread.start();
    }

    public void pause() {
        this.running = false;
        while (true) {
            try {
                this.renderThread.join();
                break;
            } catch (InterruptedException e) {
                // just retry
            }
        }
    }

    public void run() {
        long startTime = System.nanoTime();
        long fpsTime = startTime;
        long frameCounter = 0;

        while (this.running) {
            if (!this.surfaceHolder.getSurface().isValid()) {
                continue;
            }

            long currentTime = System.nanoTime();
            // deltaTime is in seconds
            float deltaTime = (currentTime - startTime) / 1000000000f;
            float fpsDeltaTime = (currentTime - fpsTime) / 1000000000f;
            startTime = currentTime;


            checkSpawnBomb();

            checkRemoveTerrorist();

            checkPlayerStart();

            if (GameWorld.getPlanksToPlace() == 0) {
                hasPlayerFinished = true;
                GameWorld.setPlanksToPlace(-1);
            }

            checkPlayerFinish(fpsDeltaTime);

            verifyLevelAndWin(fpsDeltaTime);


            if (decrTimerVal && fpsDeltaTime > 1) {
                timerVal--;
                if (timerVal == 0) {
                    decrTimerVal = false;
                    verifyWin = true;
                    this.gameworld.verifyWin(null, null);
                }
            }

            checkLaunchNextLevel();

            this.gameworld.update(deltaTime);
            this.gameworld.render();

            // Draw framebuffer on screen
            Canvas canvas = this.surfaceHolder.lockCanvas();
            canvas.getClipBounds(this.dest);

            // Scales to actual screen resolution
            canvas.drawBitmap(this.framebuffer, null, this.dest, null);
            this.surfaceHolder.unlockCanvasAndPost(canvas);

            // measure FPS
            frameCounter++;
            if (fpsDeltaTime > 1) {
                Log.d("FastRenderView", "Current FPS = " + frameCounter);
                frameCounter = 0;
                fpsTime = currentTime;
            }
        }
    }

    private void checkSpawnBomb() {
        if (spawnBomb) {
            this.gameworld.addGameObject(GameWorld.getBomb());
            spawnBomb = false;
        }
    }

    private void checkRemoveTerrorist() {
        if (removeTerrorist) {
            this.gameworld.removeGameObject(GameWorld.getTerrorist());
            GameWorld.setPreviousObjectsDestroyed(false);
            removeTerrorist = false;
            hasPlayerStarted = true;
        }
    }

    private void checkPlayerStart() {
        if (hasPlayerStarted) {
            for (GameObject object : GameWorld.getBridge()) {
                ((Bridge) object).setHasAnchor(true);
            }
            hasPlayerStarted = false;
        }
    }

    private void checkPlayerFinish(float fpsDeltaTime) {
        if (hasPlayerFinished) {
            if (fpsDeltaTime > 1) {
                GameWorld.bombTimer--;
                if (GameWorld.getBomb() != null && GameWorld.bombTimer == 0) {
                    GameWorld.getBomb().explode();
                    this.gameworld.removeGameObject(GameWorld.getBomb());
                    GameWorld.setPreviousObjectsDestroyed(false);
                    GameWorld.setBomb(null);
                    hasPlayerFinished = false;
                    verifyLevel = true;
                    GameWorld.bombTimer = 5;
                }
            }
            for (GameObject b : GameWorld.getBridge()) {
                ((Bridge) b).setHasAnchor(false);
            }
        }
    }

    private void verifyLevelAndWin(float fpsDeltaTime) {
        if (verifyLevel) {
            if (fpsDeltaTime > 1) {
                ballTimer++;
                if (ballTimer == 5) {
                    this.gameworld.verifyLevel();
                    verifyLevel = false;
                    ballTimer = 0;
                    decrTimerVal = true;
                }
            }
        }
        if (verifyWin) {
            decrTimerVal = false;
            timerVal = 10;
            this.gameworld.removePreviousObjects();
            if (win) {
                GameWorld.incrementLevel();
            }
            verifyWin = false;
            nextLevel = true;
        }
    }

    private void checkLaunchNextLevel() {
        if (nextLevel && GameWorld.isReadyForNextLevel()) {
            this.gameworld.setupNextLevel();
            nextLevel = false;
        }
    }

    // setters
    public static void setRemoveTerrorist(boolean removeTerrorist) {
        AndroidFastRenderView.removeTerrorist = removeTerrorist;
    }

    public static void setVerifyWin(boolean verifyWin) {
        AndroidFastRenderView.verifyWin = verifyWin;
    }

    public static void setWin(boolean win) {
        AndroidFastRenderView.win = win;
    }

    public static void setSpawnBomb(boolean spawnBomb) {
        AndroidFastRenderView.spawnBomb = spawnBomb;
    }
}