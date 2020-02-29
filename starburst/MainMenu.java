package com.example.alastair.starburst;

import android.app.Activity;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.constraint.solver.widgets.WidgetContainer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static com.example.alastair.starburst.GamePanel.player;


/**
 * Created by Alastair on 08/03/2018.
 */

public class MainMenu extends Activity
{

    GamePanel gamePanel;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set to full screen
       getWindow().setFlags(FLAG_FULLSCREEN,
                FLAG_FULLSCREEN);
       setVolumeControlStream(AudioManager.STREAM_MUSIC);


        gamePanel = new GamePanel(this);



        setContentView(gamePanel);
        player.setPlaying(true);
    }

    public void startGame(View v)
    {
        setContentView(gamePanel);

    }


}
