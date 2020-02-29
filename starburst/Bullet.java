package com.example.alastair.starburst;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Alastair on 15/02/2018.
 */

public class Bullet extends GameObject
{
    private Bitmap image;
    Vector2D velocity;
    int timer = 0;

    public Bullet(Bitmap res, Vector2D pos, Vector2D vel, ObjectType type)
    {
        image = res;
        position = pos;
        velocity = vel;
        objectType = type;
        height = res.getHeight();
        width = res.getWidth();
        m_active = true;


        if (width > height)
        {
            this.radius = width/2;
        }
        else
        {
            this.radius = height/2;
        }




    }

    @Override
    public void update()
        {
        position.add(velocity);

       if (position.x > GamePanel.WIDTH + 20 || position.x < - 20 || position.y < -10
               ||position.y > GamePanel.HEIGHT + 10)
       {
           this.Deactivate();
       }
    }



    @Override
    public void processCollision(GameObject other)
    {


    }




    @Override
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(this.image,position.x, position.y,null);
    }


}
