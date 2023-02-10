package unina.game.development.savethatbridge.physicsapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import java.nio.ByteOrder;

import unina.game.development.savethatbridge.R;
import unina.game.development.savethatbridge.logic.Audio;
import unina.game.development.savethatbridge.logic.Music;
import unina.game.development.savethatbridge.logic.impl.AndroidAudio;
import unina.game.development.savethatbridge.logic.impl.MultiTouchHandler;
import unina.game.development.savethatbridge.physicsapp.general.AndroidFastRenderView;
import unina.game.development.savethatbridge.physicsapp.general.Box;
import unina.game.development.savethatbridge.physicsapp.general.GameWorld;
import unina.game.development.savethatbridge.physicsapp.general.MyThread;
import unina.game.development.savethatbridge.physicsapp.sounds.BallSound;
import unina.game.development.savethatbridge.physicsapp.sounds.CollisionSounds;
import unina.game.development.savethatbridge.physicsapp.sounds.ExplosionSound;

public class StartingActivity extends Activity {
    private MyThread t; // just for fun, unrelated to the rest
    private AndroidFastRenderView renderView;
    private Music backgroundMusic;
    private int xMax, xMin, yMax, yMin;
    private GameWorld gw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load physics library
        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");

        // load constants
        Resources resources = this.getResources();
        this.xMax = resources.getInteger(R.integer.worldXMax);
        this.xMin = resources.getInteger(R.integer.worldXMin);
        this.yMax = resources.getInteger(R.integer.worldYMax);
        this.yMin = resources.getInteger(R.integer.worldYMin);
        // the tag used for logging
        String TAG = getString(R.string.app_name);
        // set fullscreen and no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // setup sound
        setupSound();

        // setup Game World
        setupGameWorld();

        // just for info
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        float refreshRate = display.getRefreshRate();
        Log.i(getString(R.string.app_name), "Refresh rate =" + refreshRate);

        // view
        this.renderView = new AndroidFastRenderView(this, this.gw);
        setContentView(this.renderView);

        // touch
        MultiTouchHandler touch = new MultiTouchHandler(this.renderView, 1, 1);

        // setter needed due to cyclic dependency
        this.gw.setTouchHandler(touch);

        // unrelated to the rest, just to show interaction with another thread
        this.t = new MyThread(this.gw);
        this.t.start();

        Log.i(getString(R.string.app_name), "onCreate complete, Endianness = " + (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? "Big Endian" : "Little Endian"));
    }

    private void setupSound() {
        Audio audio = new AndroidAudio(this);
        CollisionSounds.init(audio);
        ExplosionSound.init(audio);
        BallSound.init(audio);
        this.backgroundMusic = audio.newMusic("soundtrack.mp3");
        this.backgroundMusic.setVolume(0.5f);
        this.backgroundMusic.setLooping(true);
        this.backgroundMusic.play();
    }

    private void setupGameWorld() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Box worldSize = new Box(this.xMin, this.yMin, this.xMax, this.yMax);
        Box screenSize = new Box(0, 0, metrics.widthPixels, metrics.heightPixels);
        this.gw = new GameWorld(worldSize, screenSize, this);
        this.gw.setupNextLevel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Main thread", "pause");
        this.renderView.pause(); // stops the main loop
        this.backgroundMusic.pause();

        // persistence example
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(getString(R.string.important_info), this.t.getCounter());
        editor.apply();
        Log.i("Main thread", "saved counter " + this.t.getCounter());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Main thread", "stop");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Main thread", "resume");

        this.renderView.resume(); // starts game loop in a separate thread
        this.backgroundMusic.play();

        // persistence example
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        int counter = pref.getInt(getString(R.string.important_info), -1); // default value
        Log.i("Main thread", "read counter " + counter);
        this.t.setCounter(counter);
    }
}