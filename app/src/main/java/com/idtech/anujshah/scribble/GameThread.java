package com.idtech.anujshah.scribble;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread
{
    public Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;

    public GameThread(SurfaceHolder surfaceHolder, GameView gameView)
    {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    public SurfaceHolder getSurfaceHolder()
    {
        return surfaceHolder;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    @Override
    @SuppressLint("WrongCall")
    public void run()
    {
        while (running)
        {
            canvas = null;
            try
            {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder)
                {
                    this.gameView.onDraw(canvas);
                }
            }
            finally
            {
                if (canvas != null)
                {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
