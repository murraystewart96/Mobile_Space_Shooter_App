package com.example.alastair.starburst;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Alastair on 12/02/2018.
 */

public class MainThread extends Thread
{
    private int m_framesPerSecond = 30;
    private double m_averageFramesPerSecond;
    private SurfaceHolder m_surfaceHolder;
    private GamePanel m_gamePanel;
    private boolean m_running;
    private static Canvas m_canvas;


    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel)
    {
        super();
        this.m_surfaceHolder = surfaceHolder;
        this.m_gamePanel = gamePanel;

    }

    @Override
    public void run()
    {
        long l_startTime;
        long l_timeMillis;
        long l_waitTime;
        long l_totalTime = 0;
        int l_frameCount = 0;
        long l_targetTime = 1000/m_framesPerSecond;

        while(m_running)
        {
           // System.out.println("RUNNING");
            l_startTime = System.nanoTime();
            m_canvas = null;

            // Try locking the canvas for pixel editing
            try
            {
                m_canvas = this.m_surfaceHolder.lockCanvas();
                synchronized(m_surfaceHolder)
                {
                   // m_canvas.save();


                    // Larger screen
                    if(m_gamePanel.WIDTH >= 1280)
                   {

                    m_canvas.scale(1.5f,1.5f);
                   }



                   // m_canvas.translate(-300,0);
                   // m_canvas.scale(1.5f,1.5f);



                    this.m_gamePanel.update();
                    this.m_gamePanel.draw(m_canvas);

                    //m_canvas.restore();
                }
            }catch(Exception e){}
            // Always execute regardless of the catch
            finally {
                if(m_canvas != null)
                {
                    try
                    {
                        m_surfaceHolder.unlockCanvasAndPost(m_canvas);
                    }catch(Exception e){e.printStackTrace();};
                }
            }

            // How long it took to update and draw in milliSeconds
            l_timeMillis = (System.nanoTime() - l_startTime)/ 1000000;
            // How long is left to wait
            l_waitTime = l_targetTime - l_timeMillis;

            try
            {
                // Make thread wait!
                this.sleep(l_waitTime);

            }catch(Exception e){}


            l_totalTime += System.nanoTime() - l_startTime;
            l_frameCount++;

            if(l_frameCount == m_framesPerSecond)
            {
                m_averageFramesPerSecond = 1000/((l_totalTime/l_frameCount)/1000000);
                l_frameCount = 0;
                l_totalTime = 0;
               // System.out.println(m_averageFramesPerSecond);
            }

        }//While
    }


    public void setRunning(boolean state)
    {
        m_running = state;
    }



}
