package com.example.alastair.starburst;

/**
 * Created by Alastair on 07/03/2018.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by Murray on 27/02/2018.
 */
public class BasicDroid extends GameObject{


    private int health;
    private int damage;
    private Vector2D velocity;
    private Animation animation = new Animation();
    private long startTime;
    Bitmap spritesheet_bullet;
    private Bitmap spritesheet_explosion;
    float timer;




    public BasicDroid(Vector2D pos, Vector2D vel, Bitmap res, int w, int h, int numFrames, Bitmap res_bullet, Bitmap res_exp)
    {
        position = pos;

        this.health = 100;
        this.damage = 10;
        this.velocity = vel;
        this.height = h ;
        this.width = w ;
        this.timer = 3;
        Bitmap[] image = new Bitmap[numFrames];
        this.spritesheet_bullet = res_bullet;
        this.spritesheet = res;
        this.spritesheet_explosion = res_exp;
        this.m_active = true;
        this.objectType = ObjectType.BASIC_DROID;


        for (int i = 0; i < image.length; i++)
        {
            image[i] = Bitmap.createBitmap(spritesheet, 0, i * height, width,height);
            image[i] = Bitmap.createScaledBitmap(image[i], width*2,height*2,true);
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


        timer += 0.05;

        if (timer >= 3 && position.x < 1280)
        {
            shoot();
            timer = 0;
        }

        if (position.x <  -80)
        {
            this.Deactivate();
        }

        if (health <= 0)
        {

            Deactivate();
            Explode();

            GamePanel.player.score += 10;
        }

        position = position.add(velocity);

    }



    public void Explode()
    {

        System.out.println("EXPLODE AAAAAAAAAAAHHHHHHHHHHHH");

        Explosion explosion = new Explosion(spritesheet_explosion, position, 100,100);
        GamePanel.m_objectmanager.Add(explosion);
    }


    public void shoot()
    {



        Bullet bullet = new Bullet(spritesheet_bullet, new Vector2D(position.x ,position.y), new Vector2D(-17, 0), ObjectType.BULLET_ENEMY);
        //SCALING

        GamePanel.m_objectmanager.Add(bullet);


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
            health -= 24;
            other.Deactivate();
        }
    }



}