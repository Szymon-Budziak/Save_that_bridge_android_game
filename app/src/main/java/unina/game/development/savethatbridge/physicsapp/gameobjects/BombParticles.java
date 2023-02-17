package unina.game.development.savethatbridge.physicsapp.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.nio.ByteBuffer;

import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.ParticleFlag;
import com.google.fpl.liquidfun.ParticleGroup;
import com.google.fpl.liquidfun.ParticleGroupDef;
import com.google.fpl.liquidfun.ParticleGroupFlag;
import com.google.fpl.liquidfun.ParticleSystem;

import unina.game.development.savethatbridge.physicsapp.general.GameWorld;

public class BombParticles extends GameObject {
    private static final int PARTICLE_BYTES = 8;
    private static final int BUFFER_OFFSET = 4;

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

        // clean up native objects
        circleShape.delete();
        particleGroupDef.delete();
    }

    // draw particles
    @Override
    public void draw(Bitmap buf, float _x, float _y, float _angle) {
        this.particleSystem.copyPositionBuffer(0, this.particleGroup.getParticleCount(), this.particlePositionsBuffer);

        createBombParticles(0, this.particleGroup.getParticleCount() / 4, 255);

        createBombParticles(this.particleGroup.getParticleCount() / 4, 2 * this.particleGroup.getParticleCount() / 4, 125);

        createBombParticles(2 * this.particleGroup.getParticleCount() / 4, 3 * this.particleGroup.getParticleCount() / 4, 0);
    }

    private void createBombParticles(int start, int end, int green) {
        this.paint.setARGB(255, 255, green, 0);
        for (int i = start; i < end; i++) {
            float x = Float.intBitsToFloat(calculateX(i));
            float y = Float.intBitsToFloat(calculateY(i));
            this.canvas.drawCircle(this.gw.setWorldToFrameX(x), this.gw.setWorldToFrameY(y), 4, this.paint);
        }
    }

    private int getParticlePositionValue(int offset) {
        return this.particlePositions[offset] & 0xFF | (this.particlePositions[offset + 1] & 0xFF) << 8 | (this.particlePositions[offset + 2] & 0xFF) << 16 | (this.particlePositions[offset + 3] & 0xFF) << 24;
    }

    private int calculateX(int idx) {
        int offset = idx * 8 + BUFFER_OFFSET;
        return getParticlePositionValue(offset);
    }

    private int calculateY(int idx) {
        int offset = idx * 8 + BUFFER_OFFSET;
        return getParticlePositionValue(offset + 4);
    }
}