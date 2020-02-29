package com.example.alastair.starburst;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Alastair on 08/03/2018.
 */

public class HunterDroid extends GameObject {
    private int health;
    private int damage;
    private Vector2D velocity;
    private Animation animation = new Animation();
    private long startTime;
    float timer;
    boolean lockingOn;
    boolean charging;
    private Bitmap spritesheet_explosion;




    public HunterDroid(Vector2D pos, Vector2D vel, Bitmap res, int w, int h, int numFrames, Bitmap res_exp)
    {
        position = pos;

        this.health = 100;
        this.damage = 10;
        this.velocity = vel;
        this.height = h ;
        this.width = w ;
        this.timer = 0;
        Bitmap[] image = new Bitmap[numFrames];
        this.spritesheet = res;
        this.lockingOn = false;
        this.charging = false;
        this.spritesheet_explosion = res_exp;

        this.m_active = true;
        this.objectType = ObjectType.HUNTER_DROID;


        for (int i = 0; i < image.length; i++)
        {
            image[i] = Bitmap.createBitmap(spritesheet, 0, i * height, width,height);
            image[i] = Bitmap.createScaledBitmap(image[i], width*2, height*2,true);
        }

        height = height*2;
        width = width*2;

        if (width > height)
        {
            this.radius = (width/2) - width/3;
        }
        else
        {
            this.radius = (height/2) - height/3;
        }




        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();



    }



    @Override
    public void update()
    {
        //Every 10th of second score increments by 1
        long elapsed = (System.nanoTime() - startTime) / 1000000;
        if (elapsed > 100) {
            startTime = System.nanoTime();
        }
        animation.update();


//        timer += 0.05;
//
//        if (timer >= 3)
//        {
//            shoot();
//            timer = 0;
//        }

        if (health <= 0)
        {
            Deactivate();
            Explode();
            GamePanel.player.score += 25;
        }

        if (position.x < 0 - width*2  ||  position.y <  0 - width*2
                ||position.y > 720 + width* 2)
        {

            this.Deactivate();
        }


        if (position.x - GamePanel.player.position.x < 500 && charging == false)
        {
            if(!lockingOn)
            {
                velocity.zero();
            }

            this.lockingOn = true;
        }

        if(position.x < 1280 && lockingOn == false
                && charging == false)
        {
            velocity.x = -7;
        }

        System.out.println("HUNTER POSITION = " + position.x);


        if (lockingOn)
        {
            timer += 0.05;

            if (timer >= 3)
            {
                charge();
                lockingOn = false;
                timer = 0;
            }
        }


        position.add(velocity);

    }


    public void Explode()
    {

        System.out.println("EXPLODE AAAAAAAAAAAHHHHHHHHHHHH");

        Explosion explosion = new Explosion(spritesheet_explosion, position, 100,100);
        GamePanel.m_objectmanager.Add(explosion);
    }


    public void charge()
    {
        velocity  = GamePanel.player.position.subtract(position);
        velocity.normalize();
        velocity.scalar(20);
        charging = true;
    }


    @Override
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(animation.getImage(), position.x, position.y, null);
    }





    @Override
    public void processCollision(GameObject other)
    {
        if (other.objectType == ObjectType.BULLET_PLAYER)
        {
            if(charging == false && lockingOn == false) {
                health -= 24;
                other.Deactivate();

            }
            else if(lockingOn && charging == false)
            {
                health -= 10;
                other.Deactivate();


            }
            else if(charging)
            {
                health -= 30;
                other.Deactivate();

            }
        }


        if (other.objectType == ObjectType.PLAYER)
        {

            health = 0;
        }

    }




}
