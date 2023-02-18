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
    private final GameWorld gameWorld;
    private final Bitmap framebuffer;
    private final SurfaceHolder surfaceHolder;
    private final Rect dest = new Rect();

    // rendering
    private Thread renderThread = null;
    private volatile boolean running = false;

    // level and hasWon verification
    private static boolean isLevelVerified = false;
    private static boolean isNextLevel = false;
    private static boolean isWinVerified = false;
    private static boolean hasWon = false;

    // player
    private static boolean hasPlayerStarted = false;
    private static boolean hasPlayerFinished = false;

    // timers
    private static int bombTimer = 0;
    private static int totalGameTimer = 0;
    private static boolean isDecreasingTimerValue = false;

    // terrorist and bomb
    private static boolean isRemovingTerrorist = false;
    private static boolean isSpawningBomb = false;

    public AndroidFastRenderView(Context context, GameWorld gw) {
        super(context);
        this.gameWorld = gw;
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

            checkSpawnBombAndRemoveTerrorist();

            checkPlayerStartAndPlanksToPlace();

            checkPlayerFinish(fpsDeltaTime);

            verifyLevelAndWin(fpsDeltaTime);

            checkGameTimeAndLaunchNextLevel(fpsDeltaTime);

            this.gameWorld.update(deltaTime);
            this.gameWorld.render();

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

    private void checkSpawnBombAndRemoveTerrorist() {
        if (isSpawningBomb) {
            this.gameWorld.addGameObject(GameWorld.getBomb());
            isSpawningBomb = false;
        }
        if (isRemovingTerrorist) {
            this.gameWorld.removeGameObject(GameWorld.getTerrorist());
            GameWorld.setPreviousObjectsDestroyed(false);
            isRemovingTerrorist = false;
            hasPlayerStarted = true;
        }
    }

    private void checkPlayerStartAndPlanksToPlace() {
        if (hasPlayerStarted) {
            for (GameObject object : GameWorld.getBridge())
                ((Bridge) object).setHasAnchor(true);
            hasPlayerStarted = false;
        }
        if (GameWorld.getPlanksToPlace() == 0) {
            hasPlayerFinished = true;
            GameWorld.setPlanksToPlace(-1);
        }
    }

    private void checkPlayerFinish(float fpsDeltaTime) {
        if (hasPlayerFinished) {
            if (fpsDeltaTime > 1) {
                GameWorld.bombTimer--;
                if (GameWorld.getBomb() != null && GameWorld.bombTimer == 0) {
                    GameWorld.getBomb().explode();
                    this.gameWorld.removeGameObject(GameWorld.getBomb());
                    GameWorld.setPreviousObjectsDestroyed(false);
                    GameWorld.setBomb(null);
                    hasPlayerFinished = false;
                    isLevelVerified = true;
                    GameWorld.bombTimer = 5;
                }
            }
            for (GameObject b : GameWorld.getBridge()) {
                ((Bridge) b).setHasAnchor(false);
            }
        }
    }

    private void verifyLevelAndWin(float fpsDeltaTime) {
        if (isLevelVerified && fpsDeltaTime > 1) {
            bombTimer++;
            if (bombTimer == 5) {
                isDecreasingTimerValue = true;
                isLevelVerified = false;
                bombTimer = 0;
                this.gameWorld.verifyLevel();
            }
        }
        if (isWinVerified) {
            if (hasWon)
                GameWorld.incrementLevel();
            isDecreasingTimerValue = false;
            totalGameTimer = 0;
            this.gameWorld.removePreviousObjects();
            isWinVerified = false;
            isNextLevel = true;
        }
    }

    private void checkGameTimeAndLaunchNextLevel(float fpsDeltaTime) {
        if (isDecreasingTimerValue && fpsDeltaTime > 1) {
            totalGameTimer++;
            if (totalGameTimer == 10) {
                isDecreasingTimerValue = false;
                isWinVerified = true;
                this.gameWorld.verifyWin(null, null);
            }
        }
        if (isNextLevel && GameWorld.isReadyForNextLevel()) {
            this.gameWorld.setupNextLevel();
            isNextLevel = false;
        }
    }

    // setters
    public static void setIsRemovingTerrorist(boolean isRemovingTerrorist) {
        AndroidFastRenderView.isRemovingTerrorist = isRemovingTerrorist;
    }

    public static void setIsWinVerified(boolean isWinVerified) {
        AndroidFastRenderView.isWinVerified = isWinVerified;
    }

    public static void setHasWon(boolean hasWon) {
        AndroidFastRenderView.hasWon = hasWon;
    }

    public static void setIsSpawningBomb(boolean isSpawningBomb) {
        AndroidFastRenderView.isSpawningBomb = isSpawningBomb;
    }
}