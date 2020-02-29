package com.example.alastair.starburst;

import android.graphics.Bitmap;
import android.content.ContentProvider;
import android.content.Context;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.SoundPool;

/**
 * Created by Alastair on 13/04/2018.
 */

public class Explosion extends GameObject {

    private Animation animation = new Animation();
    private long startTime;

    public Explosion(Bitmap res, Vector2D pos, int w, int h) {
        this.height = h;
        this.width = w;
        Bitmap[] image = new Bitmap[25];
        spritesheet = res;
        position = pos;
        this.m_active = true;





        for (int i = 0; i < 5; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, width * i, height, width, height);
            image[i+5] = Bitmap.createBitmap(spritesheet, width * i, height *1, width, height);
            image[i+10] = Bitmap.createBitmap(spritesheet, width * i, height *2, width, height);
            image[i+15] = Bitmap.createBitmap(spritesheet, width * i, height*3, width, height);
            image[i+20] = Bitmap.createBitmap(spritesheet, width * i, height*4, width, height);

        }




//
//        for (int i = 5; i < 10; i++) {
//            image[i] = Bitmap.createBitmap(spritesheet, width * i, height, width, height);
//
//        }
//
//        for (int i = 10; i < 15; i++) {
//            image[i] = Bitmap.createBitmap(spritesheet, width * i, height, width, height);
//
//        }
//
//        for (int i = 15; i < 20; i++) {
//            image[i] = Bitmap.createBitmap(spritesheet, width * i, height, width, height);
//
//        }
//
//        for (int i = 20; i < 25; i++) {
//            image[i] = Bitmap.createBitmap(spritesheet, width * i, height, width, height);
//
//        }


        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();



    }

    @Override
    public void update() {
        //Every 10th of second score increments by 1
        long elapsed = (System.nanoTime() - startTime) / 1000000;

        if(elapsed>100)
        {
            startTime = System.nanoTime();
        }





        animation.update();

        System.out.println("current frame = " + animation.currentFrame);

        if (animation.currentFrame == 24)
        {
            System.out.println("current frame = " + animation.currentFrame);
            Deactivate();
        }

    }


    @Override
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(animation.getImage(), position.x, position.y, null);
    }

}


