package com.example.laris.first2dgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rushd on 7/5/2017.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread thread;
    private CharacterSprite characterSprite;
    public PipeSprite pipe1, pipe2, pipe3;
    public TrashSprite paper1, paper2;
    public static int gapHeight = 500;
    public static int velocity = 10;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int charHeight = 200;
    private int charWidth = 350;

    public GameView(Context context) {
        super(context);

        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        setFocusable(true);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        characterSprite.y = characterSprite.y - (characterSprite.yVelocity * 20);
        return super.onTouchEvent(event);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        makeLevel();
        thread.setRunning(true);
        thread.start();

    }

    private void makeLevel() {
        characterSprite = new CharacterSprite
                (getResizedBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.bird), charWidth, charHeight));
        Bitmap bmp;
        Bitmap bmp2;
        Bitmap bmp3;
        Bitmap bmp4;
        int y;
        int x;
        bmp = getResizedBitmap(BitmapFactory.decodeResource
                (getResources(), R.drawable.pipe_down), 200, Resources.getSystem().getDisplayMetrics().heightPixels/2);
        bmp2 = getResizedBitmap
                (BitmapFactory.decodeResource(getResources(), R.drawable.pipe_up), 200, Resources.getSystem().getDisplayMetrics().heightPixels/2);

        pipe1 = new PipeSprite(bmp, bmp2, 2000, 100);
        pipe2 = new PipeSprite(bmp, bmp2, 4500, 100);
        pipe3 = new PipeSprite(bmp, bmp2, 3200, 100);

        bmp3 = getResizedBitmap
                (BitmapFactory.decodeResource(getResources(), R.drawable.paper), 200, 200);
        paper1 = new TrashSprite(bmp3, 2500, 500);
        bmp4 = getResizedBitmap
                (BitmapFactory.decodeResource(getResources(), R.drawable.paper), 200, 200);
        paper2 = new TrashSprite(bmp4, 4500, 500);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();

            } catch(InterruptedException e){
                e.printStackTrace();
            }
            retry = false;
        }
    }

    public void update() {
        logic();
        characterSprite.update();
        pipe1.update();
        pipe2.update();
        pipe3.update();
        paper1.update();
        paper2.update();
    }

    @Override
    public void draw(Canvas canvas)
    {

        super.draw(canvas);
        if(canvas!=null) {
            canvas.drawRGB(0, 100, 205);
            characterSprite.draw(canvas);
            pipe1.draw(canvas);
            pipe2.draw(canvas);
            pipe3.draw(canvas);
            paper1.draw(canvas);
            paper2.draw(canvas);

        }
    }

    public void logic() {

        List<PipeSprite> pipes = new ArrayList<>();
        pipes.add(pipe1);
        pipes.add(pipe2);
        pipes.add(pipe3);

        for (int i = 0; i < pipes.size(); i++) {
            //Detect if the character is touching one of the pipes
            if (characterSprite.y < pipes.get(i).yY + (screenHeight / 2) - (gapHeight / 2) && characterSprite.x + charWidth > pipes.get(i).xX && characterSprite.x < pipes.get(i).xX + gapHeight) {
                resetLevel();
            } else if (characterSprite.y + charHeight > (screenHeight / 2) + (gapHeight / 2) + pipes.get(i).yY && characterSprite.x + charWidth > pipes.get(i).xX && characterSprite.x < pipes.get(i).xX + gapHeight) {
                resetLevel();
            }

            //Detect if the pipe has gone off the left of the screen and regenerate further ahead
            if (pipes.get(i).xX + gapHeight < 0) {
                Random r = new Random();
                int value1 = r.nextInt(gapHeight);
                int value2 = r.nextInt(gapHeight);
                pipes.get(i).xX = screenWidth + value1 + 1000;
                pipes.get(i).yY = value2 - 250;
            }
        }

        //Detect if the character has gone off the bottom or top of the screen
        if (characterSprite.y + charHeight < 0) {
            resetLevel(); }
        if (characterSprite.y > screenHeight) {
            resetLevel(); }

        //Check for paper
        List<TrashSprite> papers = new ArrayList<>();
        papers.add(paper1);
        papers.add(paper2);

        for (int i = 0; i < papers.size(); i++) {
            //Detect if the character is touching one of the papers
           if (characterSprite.y < papers.get(i).posY + 200 &&characterSprite.y + charHeight > papers.get(i).posY && characterSprite.x + charWidth > papers.get(i).posX && characterSprite.x < papers.get(i).posX + 200) {
                reGenerate(papers.get(i));
            }
               //Detect if the paper has gone off the left of the screen and regenerate further ahead
            if (papers.get(i).posX + 200 < 0) {
                reGenerate(papers.get(i));
            }
        }
    }
    public void reGenerate(TrashSprite n)
    {
        Random r = new Random();
        int value1 = r.nextInt(screenWidth);
        int value2 = r.nextInt(screenHeight);
        n.posX = screenWidth + value1;
        n.posY = value2;
    }
    public void resetLevel() {
        characterSprite.y = 100;
        pipe1.xX = 2000;
        pipe1.yY = 0;
        pipe2.xX = 4500;
        pipe2.yY = 200;
        pipe3.xX = 3200;
        pipe3.yY = 250;/*
        paper1.posX = 3000;
        paper1.posY = 200;
        paper2.posX = 4500;
        paper2.posY = 400;*/
    }

}
