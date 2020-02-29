package com.example.alastair.starburst;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by Alastair on 08/03/2018.
 */

public class TankDroid extends GameObject {


    private int health;
    private int damage;
    private Vector2D velocity;
    private Animation animation = new Animation();
    private long startTime;
    Bitmap spritesheet_bullet;
    private Bitmap spritesheet_explosion;
    float timer;




    boolean reachedPlayer;
    private int _posX;
    private int _posY;
    private int oscilatorX;
    private int oscilatorY;
    private float startY;
    private float xOffset;

    private float YPERIOD = 67; // loop every 8 calls to updateNumber
    private float XPERIOD = 233; // loop every 8 calls to updateNumber
    private float OSC_YSCALE = 133; // go between 0 and this
    private float OSC_XSCALE = 467; // go between 0 and this

    public TankDroid(Vector2D pos, Vector2D vel, int oscRangeY, int oscSpeedY, int oscSpeedX, Bitmap res, int w, int h, int numFrames, Bitmap res_bullet, Bitmap res_exp) {
        this.position = pos;
        this.startY = pos.y;
        this.health = 100;
        this.damage = 10;
        this.velocity = vel;
        this.height = h;
        this.width = w;
        this.timer = 3;
        Bitmap[] image = new Bitmap[numFrames];
        this.spritesheet_bullet = res_bullet;
        this.spritesheet = res;
        this.m_active = true;
        this.objectType = ObjectType.TANK_DROID;
        this.spritesheet_explosion = res_exp;

        Random rand = new Random();
        int randNum = rand.nextInt(200);

        this._posY = randNum;
        this._posX = 0;
        this.oscilatorX = 0;
        this.oscilatorY =0;
        this.reachedPlayer = false;
        this.xOffset = 0;

        this.OSC_YSCALE = oscRangeY;
        this.YPERIOD = oscSpeedY;
        this.XPERIOD = oscSpeedX;


        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, 0, i * height, width, height);
            image[i] = Bitmap.createScaledBitmap(image[i], (width*2)+width/3, (height*2)+height/3, true);
        }

        height = (height*2)+height/3;
        width = (width*2)+width/3;

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
    public void update() {
        //Every 10th of second score increments by 1
        long elapsed = (System.nanoTime() - startTime) / 1000000;
        if (elapsed > 100) {
            startTime = System.nanoTime();
        }
        animation.update();




        timer += 0.05;

        if (timer >= 3 && position.x < 1280) {
            shoot();
            timer = 0;
        }

        if (health <= 0) {
            Deactivate();
            Explode();
            GamePanel.player.score += 50;
        }


        if(position.x - GamePanel.player.position.x < 350)
        {

            xOffset = position.x;

            reachedPlayer = true;

            System.out.println("OFFSET == " + xOffset);

            while(xOffset != oscilatorX)
            {
                _posX++;
                oscilatorX = (int)(Math.sin(_posX*2*Math.PI/XPERIOD)*(OSC_XSCALE /2) + (OSC_XSCALE /2));
                oscilatorX += xOffset;
                System.out.println(oscilatorX);
            }

            System.out.println(position.x);

        }




        _posX++;
        oscilatorX = (int)(Math.sin(_posX*2*Math.PI/XPERIOD)*(OSC_XSCALE /2) + (OSC_XSCALE /2));
        oscilatorX += xOffset;
       // System.out.println(oscilatorX);


        _posY++;
        oscilatorY = (int)(Math.sin(_posY*2*Math.PI/YPERIOD)*(OSC_YSCALE /2) + (OSC_YSCALE /2));
        oscilatorY += startY;
       //System.out.println(oscilatorY);



        if (reachedPlayer == false)
        {
            position.add(velocity);
        }
        else
        {

            position.x = oscilatorX;

        }

        position.y = oscilatorY;

    }


    public void Explode()
    {

        System.out.println("EXPLODE AAAAAAAAAAAHHHHHHHHHHHH");

        Explosion explosion = new Explosion(spritesheet_explosion, position, 100,100);
        GamePanel.m_objectmanager.Add(explosion);
    }


    public void shoot() {

        Vector2D shootVector = GamePanel.player.position.subtract(position);
        shootVector.normalize();
        shootVector.scalar(17);


        Bullet bullet = new Bullet(spritesheet_bullet, new Vector2D(position.x, position.y), shootVector, ObjectType.BULLET_ENEMY);
        GamePanel.m_objectmanager.Add(bullet);


    }


    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(), position.x, position.y, null);

    }


    @Override
    public void processCollision(GameObject other) {
        if (other.objectType == ObjectType.BULLET_PLAYER) {
            health -= 10;
            other.Deactivate();
        }
    }



}