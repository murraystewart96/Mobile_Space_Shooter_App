package com.example.alastair.starburst;

import android.content.ContentProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.SoundPool;


public class Player extends GameObject {
    private Bitmap spritesheet;
    public int score;


    private boolean touched;
    private boolean playing;
    private Animation animation = new Animation();
    private long startTime;
    private float velo =10;
    private int height;
    private int width;
    private float shootTimer = 1.5f;
    private boolean moveUp = false;
    private boolean moveDown =false;
    private Bitmap spritesheet_bullet;
    private Bitmap spritesheet_explosion;
    private SoundPool pool;

    int shootSound;

    public int health;

    boolean isShooting = false;


    public Player(Bitmap res, Vector2D pos, int w, int h, int numFrames, Bitmap res_bullet, Bitmap res_exp, SoundPool _pool, Context _context)
    {
        pool = _pool;
        shootSound = pool.load(_context,R.raw.pulse,1);


        score = 0;
        this.height = h ;
        this.width = w ;
        health = 150;
        objectType = ObjectType.PLAYER;
        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;
        position = pos;
        this.m_active = true;
        spritesheet_bullet = res_bullet;
        spritesheet_explosion = res_exp;

        for (int i = 0; i < image.length; i++)
        {
            image[i] = Bitmap.createBitmap(spritesheet, 0, i * height, width,height);
            image[i] = Bitmap.createScaledBitmap(image[i], width*2,height*2,true);
        }

       height = height*2;
       width = width*2;

        if (width > height)
        {
            this.radius = width/3 - width/4;
        }
        else
        {
            this.radius = height/3 - width/4;
        }

        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();
    }

    public void setTouched(boolean b){touched = b;}
    public void setMoveUp(boolean b){moveUp = b;}
    public void setMoveDown(boolean b){moveDown = b;}

    public void handleActionDown(int eventX, int eventY)
    {


        if(!playing)
        {
           // playing = true;
        }

        if(eventX >= 0 && eventX <= position.x && eventY >= GamePanel.HEIGHT/2 && eventY <= GamePanel.HEIGHT)
        {
            if(touched = true)
            {
                moveDown = true;
            }
        }
        if(eventX >= 0 && eventX <= position.x && eventY >= 0 && eventY <= GamePanel.HEIGHT/2)
        {
            if(touched = true)
            {

                moveUp = true;
            }
        }

        //System.out.println("PLAYER POSITION = " + position.x + " , "+ position.y);
       //System.out.println("TOUCH POSITION = " + eventX + " , " + eventY);


        if(eventX >= position.x)
        {
            if(touched = true && shootTimer > 1.5)
            {

                shoot(eventX, eventY);
                shootTimer = 0;
                pool.play(shootSound,1.0f,0.0f,1,0,1.0f);


                System.out.println(eventX + "   " + eventY);
            }
        }

    }

    @Override
    public void update()
    {

        //System.out.println("PLAYER POSITION = " + position.x + " , "+ position.y);
        //Every 10th of second score increments by 1
        long elapsed = (System.nanoTime() - startTime) / 1000000;

        if(elapsed>100)
        {
            startTime = System.nanoTime();
        }
        animation.update();

        // move up
        if(moveUp) position.y -= velo;

        // move down
        if(moveDown) position.y += velo;


        //Top Screen Boundary
        if (position.y <= 0)
        {
            position.y = 0;
        }
        //Bottom Screen Boundary
        else if(position.y >= 720- height)
        {
            position.y = 720 - height;
        }

        if (health <= 0)
        {
            playing = false;
            Explode();
            //Deactivate();
        }

        shootTimer +=0.1;

    }

    @Override
    public void processCollision(GameObject other)
    {

        if (other.objectType == ObjectType.BULLET_ENEMY)
        {
            health -= 15;

            if(health <= 0) health = 0;

                other.Deactivate();
                System.out.println("SHIP COLLIDED");
        }

        if (other.objectType == ObjectType.HUNTER_DROID)
        {
            health = 0;

            if(health <= 0) health = 0;

            other.Deactivate();
            System.out.println("SHIP COLLIDED");
        }

        if (other.objectType == ObjectType.BASIC_DROID)
        {
            health = 0;

            if(health <= 0) health = 0;

            other.Deactivate();
            System.out.println("SHIP COLLIDED");
        }


    }

    @Override
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(animation.getImage(), position.x-width/1.5f, position.y, null);
    }

    public void shoot(int eventX, int eventY)
    {
        Vector2D shootVector = new Vector2D(eventX, eventY).subtract(position);
        shootVector.normalize();

        shootVector.scalar(17);

        Bullet bullet = new Bullet(spritesheet_bullet, new Vector2D(position.x, position.y), shootVector, ObjectType.BULLET_PLAYER);
        GamePanel.m_objectmanager.Add(bullet);

        isShooting = true;
    }


    public void Explode()
    {
        Explosion explosion = new Explosion(spritesheet_explosion, position, 100,100);
        GamePanel.m_objectmanager.Add(explosion);
    }



    @Override
    public boolean hasCollided(GameObject other)
    {


        float distance = (((other.position.x - position.x+width/1.5f) * (other.position.x - position.x+width/1.5f)) +
                ((other.position.y - position.y) * (other.position.y - position.y)));

        float sum = (radius + other.radius) * (radius + other.radius);



        if (distance < sum)
        {
            return true;
        }



        return false;
    }




    public int getScore(){return score;}

    public boolean getPlaying(){return playing;}

    public boolean isTouched(){return touched;}

    public void setPlaying(boolean b){playing = b;}

    public void resetScore() {score = 0;}







};