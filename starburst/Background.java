package com.example.alastair.starburst;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Alastair on 13/02/2018.
 */

public class Background {

    public Bitmap m_image;
    private int x,y,dx;


    public Background(Bitmap res)
    {
        //m_image = Bitmap.createScaledBitmap(res, GamePanel.m_thread.m_canvas.getWidth(), GamePanel.m_thread.m_canvas.getHeight(), true  );
        m_image = res;

        dx = GamePanel.MOVESPEED;

    }

    public void update()
    {
        // Scrolls background by value dx
        x += dx;

        // If the image is off the the screen reset it
        if(x<-1280)
        {
            x = 0;
        }

    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(m_image,x,y,null);

        if(x<0)
        {
            canvas.drawBitmap(m_image,x + 1280,y,null);
        }
    }




}
