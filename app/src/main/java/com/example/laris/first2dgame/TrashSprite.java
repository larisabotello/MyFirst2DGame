package com.example.laris.first2dgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by laris on 4/21/2018.
 */

public class TrashSprite {
    private Bitmap image;
    public int posX, posY;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    public TrashSprite (Bitmap bmp, int x, int y) {
        image = bmp;
        posX = x;
        posY = y;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, posX, posY, null);
    }
    public void update() {
        posX -= GameView.velocity;
    }
    public void destroy()
    {
        image.recycle();
    }
    public boolean gone()
    {
        return image.isRecycled();
    }
}
