package unina.game.development.savethatbridge.physicsapp.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.ParticleFlag;
import com.google.fpl.liquidfun.ParticleGroup;
import com.google.fpl.liquidfun.ParticleGroupDef;
import com.google.fpl.liquidfun.ParticleGroupFlag;
import com.google.fpl.liquidfun.ParticleSystem;

import unina.game.development.savethatbridge.physicsapp.general.GameWorld;

public class BombParticles extends GameObject {

    private static final int BYTESPERPARTICLE = 8;

    private static int bufferOffset;
    private static boolean isLittleEndian;

    private final byte[] particlePositions;
    private final ByteBuffer particlePositionsBuffer;

    private final Canvas canvas;
    private final Paint paint;
    private final ParticleSystem particleSystem;
    private final ParticleGroup group;

    static {
        discoverEndianness();
    }

    public BombParticles(GameWorld gw, float x, float y) {
        super(gw);

        this.canvas = new Canvas(gw.buffer);
        this.particleSystem = gw.getParticleSystem();
        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(2);
        circleShape.setPosition(x, y);

        ParticleGroupDef particleGroupDef = new ParticleGroupDef();
        particleGroupDef.setShape(circleShape);
        particleGroupDef.setPosition(x, y);
        particleGroupDef.setGroupFlags(ParticleGroupFlag.solidParticleGroup);
        particleGroupDef.setFlags(ParticleFlag.powderParticle);
        particleGroupDef.setLifetime(3);

        this.group = this.particleSystem.createParticleGroup(particleGroupDef);

        this.particlePositionsBuffer = ByteBuffer.allocateDirect(this.group.getParticleCount() * BYTESPERPARTICLE);
        this.particlePositions = this.particlePositionsBuffer.array();

        this.body = null;
        // clean up native objects
        circleShape.delete();
        particleGroupDef.delete();
    }

    @Override
    public void draw(Bitmap buf, float _x, float _y, float _angle) {
        this.particleSystem.copyPositionBuffer(0, this.group.getParticleCount(), this.particlePositionsBuffer);

        createBombParticles(0, this.group.getParticleCount() / 2, 150, 150, 150, 3);

        createBombParticles(this.group.getParticleCount() / 2, 4 * this.group.getParticleCount() / 6, 200, 200, 50, 4);

        createBombParticles(4 * this.group.getParticleCount() / 6, 5 * this.group.getParticleCount() / 6, 200, 50, 50, 4);

        createBombParticles(5 * this.group.getParticleCount() / 6, this.group.getParticleCount(), 200, 150, 50, 4);
    }

    private void createBombParticles(int start, int end, int red, int green, int blue, int radius) {
        float x, y;
        this.paint.setARGB(255, red, green, blue);
        for (int i = start; i < end; i++) {
            if (isLittleEndian) {
                x = Float.intBitsToFloat(calculateXLittleEndian(i));
                y = Float.intBitsToFloat(calculateYLittleEndian(i));
            } else {
                x = Float.intBitsToFloat(calculateXBigEndian(i));
                y = Float.intBitsToFloat(calculateYBigEndian(i));
            }
            this.canvas.drawCircle(this.gw.worldToFrameBufferX(x), this.gw.worldToFrameBufferY(y), radius, this.paint);
        }
    }

    private int calculateXLittleEndian(int idx) {
        int firstPart = this.particlePositions[idx * 8 + bufferOffset] & 0xFF;
        int secondPart = (this.particlePositions[idx * 8 + bufferOffset + 1] & 0xFF) << 8;
        int thirdPart = (this.particlePositions[idx * 8 + bufferOffset + 2] & 0xFF) << 16;
        int fourthPart = (this.particlePositions[idx * 8 + bufferOffset + 3] & 0xFF) << 24;
        return (firstPart | secondPart | thirdPart | fourthPart);
    }

    private int calculateXBigEndian(int idx) {
        int firstPart = (this.particlePositions[idx * 8] & 0xFF) << 24;
        int secondPart = (this.particlePositions[idx * 8 + 1] & 0xFF) << 16;
        int thirdPart = (this.particlePositions[idx * 8 + 2] & 0xFF) << 8;
        int fourthPart = this.particlePositions[idx * 8 + 3] & 0xFF;
        return (firstPart | secondPart | thirdPart | fourthPart);
    }

    private int calculateYLittleEndian(int idx) {
        int firstPart = this.particlePositions[idx * 8 + bufferOffset + 4] & 0xFF;
        int secondPart = (this.particlePositions[idx * 8 + bufferOffset + 5] & 0xFF) << 8;
        int thirdPart = (this.particlePositions[idx * 8 + bufferOffset + 6] & 0xFF) << 16;
        int fourthPart = (this.particlePositions[idx * 8 + bufferOffset + 7] & 0xFF) << 24;
        return (firstPart | secondPart | thirdPart | fourthPart);
    }

    private int calculateYBigEndian(int idx) {
        int firstPart = (this.particlePositions[idx * 8 + 4] & 0xFF) << 24;
        int secondPart = (this.particlePositions[idx * 8 + 5] & 0xFF) << 16;
        int thirdPart = (this.particlePositions[idx * 8 + 6] & 0xFF) << 8;
        int fourthPart = this.particlePositions[idx * 8 + 7] & 0xFF;
        return (firstPart | secondPart | thirdPart | fourthPart);
    }

    public static void discoverEndianness() {
        isLittleEndian = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);
        Log.d("DEBUG", "Build.FINGERPRINT=" + Build.FINGERPRINT);
        Log.d("DEBUG", "Build.PRODUCT=" + Build.PRODUCT);
        bufferOffset = 4;
    }
}