package unina.game.development.savethatbridge.logic;

import unina.game.development.savethatbridge.logic.Graphics.PixmapFormat;

public interface Pixmap {
    public int getWidth();

    public int getHeight();

    public PixmapFormat getFormat();

    public void dispose();
}