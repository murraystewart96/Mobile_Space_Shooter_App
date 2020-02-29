package com.example.alastair.starburst;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by Alastair on 13/02/2018.
 */


enum ObjectType {PLAYER, BULLET_ENEMY, BULLET_PLAYER, BASIC_DROID, HUNTER_DROID, TANK_DROID, BOSS}


public abstract class GameObject
{

    protected int width;
    protected int height;



    protected Vector2D position;
    protected Bitmap spritesheet;


    protected int radius;
    protected boolean m_active;

    protected ObjectType objectType;




    public int getHeight()
    {
        return height;
    }

    public void Deactivate()
    {
        this.m_active = false;
    }

    public int getWidth()
    {
        return width;
    }

    public void update(){};
    public void draw(Canvas c){};

    public boolean hasCollided(GameObject other)
    {


        float distance = (((other.position.x - position.x) * (other.position.x - position.x)) +
                ((other.position.y - position.y) * (other.position.y - position.y)));

        float sum = (radius + other.radius) * (radius + other.radius);



        if (distance < sum)
        {
            return true;
        }



        return false;
    }


    public void processCollision(GameObject other){};


}

