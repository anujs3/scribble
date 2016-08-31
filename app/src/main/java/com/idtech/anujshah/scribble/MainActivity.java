package com.idtech.anujshah.scribble;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity
{
    private GameView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gv = new GameView(this);
        setContentView(gv);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        gv.thread.setRunning(false);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        gv.thread.setRunning(true);
    }
}
