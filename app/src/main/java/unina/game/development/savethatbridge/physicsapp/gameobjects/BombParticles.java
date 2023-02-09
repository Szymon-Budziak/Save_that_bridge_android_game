package unina.game.development.savethatbridge.physicsapp.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

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
    private static final int PARTICLE_BYTES = 8;
    private static final int bufferOffset = 4;
    private static final boolean isLittleEndian = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);

    private final Canvas canvas;
    private final Paint paint;

    // particles
    private final ParticleSystem particleSystem;
    private final ParticleGroup particleGroup;
    private final byte[] particlePositions;
    private final ByteBuffer particlePositionsBuffer;

    public BombParticles(GameWorld gw, float x, float y) {
        super(gw);

        this.canvas = new Canvas(gw.getBitmapBuffer());
        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.particleSystem = gw.getParticleSystem();

        // shape of particles
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(2);
        circleShape.setPosition(x, y);

        ParticleGroupDef particleGroupDef = new ParticleGroupDef();
        particleGroupDef.setShape(circleShape);
        particleGroupDef.setPosition(x, y);
        particleGroupDef.setGroupFlags(ParticleGroupFlag.solidParticleGroup);
        particleGroupDef.setFlags(ParticleFlag.powderParticle);
        particleGroupDef.setLifetime(3);

        this.particleGroup = this.particleSystem.createParticleGroup(particleGroupDef);

        this.particlePositionsBuffer = ByteBuffer.allocateDirect(this.particleGroup.getParticleCount() * PARTICLE_BYTES);
        this.particlePositions = this.particlePositionsBuffer.array();

        this.body = null;

        // clean up native objects
        circleShape.delete();
        particleGroupDef.delete();
    }

    @Override
    public void draw(Bitmap buf, float _x, float _y, float _angle) {
        this.particleSystem.copyPositionBuffer(0, this.particleGroup.getParticleCount(), this.particlePositionsBuffer);

        createBombParticles(0, this.particleGroup.getParticleCount() / 4, 255);

        createBombParticles(this.particleGroup.getParticleCount() / 4, 2 * this.particleGroup.getParticleCount() / 4, 125);

        createBombParticles(2 * this.particleGroup.getParticleCount() / 4, 3 * this.particleGroup.getParticleCount() / 4, 0);
    }

    private void createBombParticles(int start, int end, int green) {
        float x, y;
        this.paint.setARGB(255, 255, green, 0);
        for (int i = start; i < end; i++) {
            if (isLittleEndian) {
                x = Float.intBitsToFloat(calculateXLittleEndian(i));
                y = Float.intBitsToFloat(calculateYLittleEndian(i));
            } else {
                x = Float.intBitsToFloat(calculateXBigEndian(i));
                y = Float.intBitsToFloat(calculateYBigEndian(i));
            }
            this.canvas.drawCircle(this.gw.setWorldToFrameX(x), this.gw.setWorldToFrameY(y), 4, this.paint);
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
}