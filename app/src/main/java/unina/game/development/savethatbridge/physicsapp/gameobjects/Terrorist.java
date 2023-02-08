package unina.game.development.savethatbridge.physicsapp.gameobjects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import unina.game.development.savethatbridge.R;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import unina.game.development.savethatbridge.physicsapp.general.AndroidFastRenderView;
import unina.game.development.savethatbridge.physicsapp.general.GameWorld;
import unina.game.development.savethatbridge.physicsapp.general.Level;

public class Terrorist extends GameObject {
    private static float screenSemiWidth, screenSemiHeight;
    private final Canvas canvas;

    private final Rect src;
    private final RectF dest = new RectF();
    private final Bitmap bitmap;
    private int sprite = 0;

    private long updateTime = 0;
    private boolean allBombsPlanted = false;
    private int numberOfBombs = 2;
    private static final List<Integer> timeToPlantBombs = new ArrayList<>();
    private final long now;

    private static final int FRAME_WIDTH = 46;

    public Terrorist(GameWorld gw, float x, float y) {
        super(gw);
        int width = 2;
        int height = 2;

        this.canvas = new Canvas(gw.getBuffer());
        screenSemiHeight = gw.toPixelsYLength(height) / 2;
        screenSemiWidth = gw.toPixelsXLength(width) / 2;
        this.src = new Rect(0, 150, FRAME_WIDTH, 200);
        this.now = System.currentTimeMillis() / 1000;

        // a body definition: position and type
        BodyDef bodyDef = new BodyDef();
        bodyDef.setPosition(x, y);
        bodyDef.setType(BodyType.kinematicBody);
        bodyDef.setLinearVelocity(new Vec2((float) 3, 0));

        // a body
        this.body = gw.getWorld().createBody(bodyDef);
        this.body.setSleepingAllowed(true);
        this.name = "Terrorist";
        this.body.setUserData(this);

        generateRandomTimeBombPlants();

        // Prevents scaling
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        this.bitmap = BitmapFactory.decodeResource(gw.activity.getResources(), R.drawable.terrorist, o);

        // clean up native objects
        bodyDef.delete();
    }

    private void updateAnimation() {
        this.src.left = this.sprite * FRAME_WIDTH;
        this.src.right = this.sprite * FRAME_WIDTH + FRAME_WIDTH;
        if (this.sprite == 7) this.sprite = 0;
        else this.sprite++;
    }

    public void setNumberOfBombs(int numberOfBombs) {
        this.numberOfBombs = numberOfBombs;
    }

    private void generateRandomTimeBombPlants() {
        for (int i = 0; i < numberOfBombs; i++) {
            Random random = new Random();
            timeToPlantBombs.add(random.nextInt(8));
        }
    }

    private void checkToSpawnTheBomb() {
        long currentTime = System.currentTimeMillis() / 1000;
        long timePassed = this.now - currentTime;
        for (int i = 0; i < timeToPlantBombs.size(); i++) {
            int time = timeToPlantBombs.get(i);
            if (time > timePassed && this.body.getPositionX() > (Level.getBomb() != null ? Level.getBomb().body.getPositionX() : 0)) {
                AndroidFastRenderView.setSpawnBomb(true);
                timeToPlantBombs.remove(i);
                i--;
            }
        }
        if (timeToPlantBombs.isEmpty()) this.allBombsPlanted = true;
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        if (this.updateTime == 6) {
            updateAnimation();
            this.updateTime = 0;
        }
        this.updateTime++;

        if (this.body.getPositionX() > this.gw.getPhysicalSize().getxMax() - 1) {
            AndroidFastRenderView.setRemoveTerrorist(true);
        }

        if (!this.allBombsPlanted) {
            checkToSpawnTheBomb();
        }

        this.canvas.save();
        this.canvas.rotate((float) Math.toDegrees(angle), x, y);
        this.dest.left = x - screenSemiWidth;
        this.dest.bottom = y + screenSemiHeight;
        this.dest.right = x + screenSemiWidth;
        this.dest.top = y - screenSemiHeight;
        this.canvas.drawBitmap(this.bitmap, this.src, this.dest, null);
        this.canvas.restore();
    }
}