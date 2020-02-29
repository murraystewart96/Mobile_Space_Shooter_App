package com.example.alastair.starburst;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.Image;
import android.media.SoundPool;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import static android.content.ContentValues.TAG;

enum GameState{RUNNNING, GAMEOVER, MENU};

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback
{


    GameState currentState;

    public static int WIDTH = 800;
    public static int HEIGHT = 480;
    public static long INIT_TIME;

    public static final int MOVESPEED = -5;
    public static DisplayMetrics display;
    public static ObjectManager m_objectmanager;
    public static MainThread m_thread;

    private Background m_background;
    public static Player player;
    private float waveTimer;
    private int waveNumber;
    private boolean waveInterval;
    private boolean startWave;

    public boolean GamOver;
    public SoundPool soundPool;

    private OrientiationData oriontationData;
    private long frameTime;
    private boolean oriontationToggle;

    // Constructor
    public GamePanel(Context context)
    {
        // surface view
        super(context);

        currentState = GameState.MENU;
       // player.setPlaying(true);


        oriontationToggle = true;
        oriontationData = new OrientiationData(context);
        oriontationData.register();
        frameTime = System.currentTimeMillis();

        // Add the callback to the SurfaceHolder to intercept events
        getHolder().addCallback(this);

        display = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(display);
        WIDTH = display.widthPixels;
        HEIGHT = display.heightPixels;

        soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC,0);
        m_objectmanager = new ObjectManager();

        // Initialise game objects
        m_background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.backdrop));
        m_background.m_image = Bitmap.createScaledBitmap(m_background.m_image, 1280, 720, true);
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.spaceship),new Vector2D(320,360), 64, 29, 4, BitmapFactory.decodeResource(getResources(), R.drawable.shot),
                BitmapFactory.decodeResource(getResources(), R.drawable.explosion), soundPool,getContext());
        m_objectmanager.Add(player);

        waveNumber = 1;
        waveTimer = 0;
        waveInterval = false;
        startWave = false;

        // make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            // Handle actions whilst game is on
            player.handleActionDown((int)event.getX() , (int)event.getY());

            //System.out.println("X = " + event.getX() + "Y = " + event.getY());

            if(currentState == GameState.MENU)
            {
                if(event.getX() >= (1280 - 230) * 1.5f && event.getX() <= (1280 - 140) * 1.5f  && event.getY() >= 600 * 1.5f  && event.getY() <= 670 * 1.5f)
                {
                    oriontationToggle = !oriontationToggle;

                    System.out.println("PRESSED ");
                }

                if(event.getX() >= ((640) * 1.5f - (160)* 1.5f)&& event.getX() <= ((640) * 1.5f + (160)* 1.5f) && event.getY() >= ((450) * 1.5f - (89)* 1.5f)
                    && event.getY() <= ((450) * 1.5f + (89)* 1.5f))  {
                    currentState = GameState.RUNNNING;

                }



//                if(event.getX() >= 640  - 160&& event.getX() <= 640 *  + (160) && event.getY() >= 450 *  - 89
//                        && event.getY() <= 450   + 89) {
//                    currentState = GameState.RUNNNING;
//
//                }

            }

        }
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
            player.setTouched(false);
            player.setMoveUp(false);
            player.setMoveDown(false);
        }


        else if(currentState == GameState.GAMEOVER)
        {
            oriontationData.newGame();
            player.setPlaying(true);
            m_objectmanager.Add(player);

            player.health = 150;
            player.score = 0;
            waveNumber = 1;

           currentState = GameState.RUNNNING;

        }

        return true;
    }




    public void update()
    {
        switch(currentState)
        {
            case MENU:

                break;

            case RUNNNING:

                manageOrientiationData();
                manageWaves();

                m_objectmanager.update();
                m_objectmanager.processCollisions();
                m_objectmanager.RemoveInactive();

                if(!player.getPlaying()) currentState = GameState.GAMEOVER;

                break;
            case GAMEOVER:


                m_objectmanager.RemoveAll();

                break;

        }

        m_background.update();

    }


    @Override
    public void draw(Canvas canvas) {
        //super override
        super.draw(canvas);

        if (canvas != null)
        {
            m_background.draw(canvas);

            switch(currentState)
            {
                case MENU:


                    Paint toggle = new Paint();
                    toggle.setColor(Color.GREEN);
                    toggle.setTextSize(30);
                    toggle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                    if(oriontationToggle) canvas.drawText("Accelmeter On",1280 - 300, 700, toggle);
                    else if(!oriontationToggle) canvas.drawText("Accelmeter off",1280 - 300, 700, toggle);



                    canvas.drawText("Toggle",1280 - 245, 570, toggle);
                    canvas.drawRect(1280 - 230 , 600 ,1280 - 140, 670,toggle);


                    canvas.drawBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.startburst)),1280 - 950,50,null);
                    canvas.drawBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.playbut)),1280 - 800,350,null);



                    break;

                case RUNNNING:

                        m_objectmanager.draw(canvas);

                        Paint HealthPaint = new Paint();
                        Paint ScorePaint = new Paint();
                        HealthPaint.setColor(Color.RED);
                        HealthPaint.setTextSize(30);
                        HealthPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                        ScorePaint.setColor(Color.BLUE);
                        ScorePaint.setTextSize(30);
                        ScorePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                        canvas.drawText("SCORE: " + (player.getScore()),1280 - 350, 30, ScorePaint);
                        canvas.drawText("HEALTH: " + player.health,20 , 30, HealthPaint);



                    break;
                case GAMEOVER:

                        Paint GameOver = new Paint();
                        GameOver.setColor(Color.RED);
                        GameOver.setTextSize(60);
                        GameOver.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        canvas.drawText("GameOver",1280 - 800, 300, GameOver);
                        canvas.drawText("You scored: " +(player.getScore()),1280 - 800, 400, GameOver);

                    break;

            }



        }

    }


    public void startWave()
    {
        if (waveNumber == 1)
        {
            //Adding enemies
            BasicDroid bdroid1 = new BasicDroid(new Vector2D(1333, 120),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot), BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            BasicDroid bdroid2 = new BasicDroid(new Vector2D(2000, 333),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            BasicDroid bdroid3 = new BasicDroid(new Vector2D(1667, 600),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            m_objectmanager.Add(bdroid1);
            m_objectmanager.Add(bdroid2);
            m_objectmanager.Add(bdroid3);

            waveNumber++;

        }
        else if(waveNumber == 2)
        {

            System.out.println("WAVE 2 YA CUNT");

            //Adding enemies
            BasicDroid bdroid1 = new BasicDroid(new Vector2D(1333, 120),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            BasicDroid bdroid2 = new BasicDroid(new Vector2D(2000, 333),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            BasicDroid bdroid3 = new BasicDroid(new Vector2D(1667, 600),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            BasicDroid bdroid4 = new BasicDroid(new Vector2D(2333, 333),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));


            HunterDroid hdroid1 = new HunterDroid(new Vector2D(2733, 333),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_orange40x30), 40, 30, 6
            ,  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            m_objectmanager.Add(bdroid1);
            m_objectmanager.Add(bdroid2);
            m_objectmanager.Add(bdroid3);
            m_objectmanager.Add(bdroid4);
            m_objectmanager.Add(hdroid1);
            waveNumber++;
        }
        else if(waveNumber == 3)
        {
            //Adding enemies


            HunterDroid hdroid1 = new HunterDroid(new Vector2D(1333, 200),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_orange40x30), 40, 30, 6
            ,  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));
            HunterDroid hdroid2 = new HunterDroid(new Vector2D(1567, 467),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_orange40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            BasicDroid bdroid1 = new BasicDroid(new Vector2D(1833, 120),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            BasicDroid bdroid2 = new BasicDroid(new Vector2D(2167, 333),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            BasicDroid bdroid3 = new BasicDroid(new Vector2D(2000, 600),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));


            HunterDroid hdroid3 = new HunterDroid(new Vector2D(2733, 200),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_orange40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.explosion));
            HunterDroid hdroid4 = new HunterDroid(new Vector2D(2733, 466),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_orange40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.explosion));



            m_objectmanager.Add(hdroid1);
            m_objectmanager.Add(hdroid2);


            m_objectmanager.Add(bdroid1);
            m_objectmanager.Add(bdroid2);
            m_objectmanager.Add(bdroid3);


            m_objectmanager.Add(hdroid3);
            m_objectmanager.Add(hdroid4);

            waveNumber++;
        }
        else if(waveNumber ==4)
        {
            //Adding enemies


            TankDroid tdroid1 = new TankDroid(new Vector2D(1333, 133), new Vector2D(-5, 0), 133,67,133,BitmapFactory.decodeResource(getResources(), R.drawable.droid_blue40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),   BitmapFactory.decodeResource(getResources(), R.drawable.explosion));
            TankDroid tdroid2 = new TankDroid(new Vector2D(1333, 400), new Vector2D(-5, 0), 133,67,133,BitmapFactory.decodeResource(getResources(), R.drawable.droid_blue40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),   BitmapFactory.decodeResource(getResources(), R.drawable.explosion));



            m_objectmanager.Add(tdroid1);
            m_objectmanager.Add(tdroid2);






            waveNumber++;
        }
        else if(waveNumber == 5)
        {
            TankDroid tdroid1 = new TankDroid(new Vector2D(1333, 333), new Vector2D(-5, 0), 100,67,133,BitmapFactory.decodeResource(getResources(), R.drawable.droid_blue40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),   BitmapFactory.decodeResource(getResources(), R.drawable.explosion));



            BasicDroid bdroid1 = new BasicDroid(new Vector2D(2000, 120),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            BasicDroid bdroid2 = new BasicDroid(new Vector2D(1667, 600),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            BasicDroid bdroid3 = new BasicDroid(new Vector2D(2600, 120),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            BasicDroid bdroid4 = new BasicDroid(new Vector2D(2600, 600),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));



            HunterDroid hdroid1 = new HunterDroid(new Vector2D(3400, 120),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_orange40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.explosion));
            HunterDroid hdroid2 = new HunterDroid(new Vector2D(3400, 600),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_orange40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            m_objectmanager.Add(tdroid1);
            m_objectmanager.Add(bdroid1);
            m_objectmanager.Add(bdroid2);
            m_objectmanager.Add(bdroid3);
            m_objectmanager.Add(bdroid4);
            m_objectmanager.Add(hdroid1);
            m_objectmanager.Add(hdroid2);

            waveNumber++;
        }
        else if(waveNumber == 6)
        {


            HunterDroid hdroid1 = new HunterDroid(new Vector2D(1800, 333),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_orange40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.explosion));



            BasicDroid bdroid1 = new BasicDroid(new Vector2D(1333, 120),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            BasicDroid bdroid2 = new BasicDroid(new Vector2D(1333, 600),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));




            TankDroid tdroid1 = new TankDroid(new Vector2D(2300, 333), new Vector2D(-5, 0), 100,67,133,BitmapFactory.decodeResource(getResources(), R.drawable.droid_blue40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),   BitmapFactory.decodeResource(getResources(), R.drawable.explosion));


            HunterDroid hdroid2 = new HunterDroid(new Vector2D(2700, 600),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_orange40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.explosion));


            HunterDroid hdroid3 = new HunterDroid(new Vector2D(3000, 120),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_orange40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            m_objectmanager.Add(tdroid1);
            m_objectmanager.Add(bdroid1);
            m_objectmanager.Add(bdroid2);

            m_objectmanager.Add(hdroid1);
            m_objectmanager.Add(hdroid2);
            m_objectmanager.Add(hdroid3);

            waveNumber++;
        }
        else if(waveNumber == 7)
        {


            HunterDroid hdroid1 = new HunterDroid(new Vector2D(1333, 333),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_orange40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.explosion));



            BasicDroid bdroid1 = new BasicDroid(new Vector2D(1460, 120),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            BasicDroid bdroid2 = new BasicDroid(new Vector2D(1460, 333),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            BasicDroid bdroid3 = new BasicDroid(new Vector2D(1460, 600),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_spritesheet_yellow40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),  BitmapFactory.decodeResource(getResources(), R.drawable.explosion));




            TankDroid tdroid1 = new TankDroid(new Vector2D(1900, 150), new Vector2D(-5, 0), 80,67,133,BitmapFactory.decodeResource(getResources(), R.drawable.droid_blue40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),   BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            TankDroid tdroid2 = new TankDroid(new Vector2D(1900, 550), new Vector2D(-5, 0), 80,67,133,BitmapFactory.decodeResource(getResources(), R.drawable.droid_blue40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.shot),   BitmapFactory.decodeResource(getResources(), R.drawable.explosion));


            HunterDroid hdroid2 = new HunterDroid(new Vector2D(2200, 333),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_orange40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.explosion));


            HunterDroid hdroid3 = new HunterDroid(new Vector2D(2600, 333),new Vector2D(-4, 0), BitmapFactory.decodeResource(getResources(), R.drawable.droid_orange40x30), 40, 30, 6,
                    BitmapFactory.decodeResource(getResources(), R.drawable.explosion));

            m_objectmanager.Add(tdroid1);
            m_objectmanager.Add(tdroid2);
            m_objectmanager.Add(bdroid1);
            m_objectmanager.Add(bdroid2);
            m_objectmanager.Add(bdroid3);

            m_objectmanager.Add(hdroid1);
            m_objectmanager.Add(hdroid2);
            m_objectmanager.Add(hdroid3);
        }
    }

    public void manageWaves()
    {
        if(m_objectmanager.NoEnemies())
        {
            waveInterval = true;
            startWave = false;
            //System.out.println("NO ENEMIES");
        }
        if (waveInterval)
        {
            waveTimer += 0.05;
        }
        if(waveTimer >= 4)
        {
            waveInterval = false;
            startWave = true;
            waveTimer = 0;
            //System.out.println("TIMER OVER");
        }
        if (startWave == true)
        {
            startWave();
            startWave = false;
            //System.out.println("WAVE STARTED");
        }
    }

    public void manageOrientiationData()
    {
        if(oriontationToggle)
        {
            // System.out.println("RATIO = " + WIDTH + " x " + HEIGHT);
            if(frameTime <  INIT_TIME)
            {
                frameTime = INIT_TIME;
            }
            int elapsedTime = (int)(System.currentTimeMillis() - frameTime);
            frameTime = System.currentTimeMillis();

            if(oriontationData.getOrientation() != null && oriontationData.getStartOrientation() != null)
            {
                // float pitch = oriontationData.getOrientation()[1] - oriontationData.getStartOrientation()[1];
                float roll = oriontationData.getOrientation()[2] - oriontationData.getStartOrientation()[2] ;

                float ySpeed = roll * HEIGHT/1000;
                player.position.y -= Math.abs(ySpeed*elapsedTime) > 5 ? ySpeed*elapsedTime : 0;
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        ThreadPause();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {

        INIT_TIME = System.currentTimeMillis();
        ThreadResume();

    }

    public void ThreadResume()
    {
        // Make thread
        m_thread = new MainThread(getHolder(), this);
        // Start the game loop once the surface has been created
        m_thread.setRunning(true);
        m_thread.start();



    }

    public void ThreadPause() {
        boolean l_retry = true;
        while (l_retry) {
            try {
                m_thread.setRunning(false);
                m_thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            l_retry = false;
        }
    }
}

