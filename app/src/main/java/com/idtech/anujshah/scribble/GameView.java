package com.idtech.anujshah.scribble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;

public class GameView extends SurfaceView implements SurfaceHolder.Callback
{
    GameThread thread;

    private Paint textColor;

    private boolean buttonDown;

    // line coordinates
    private float x1 = 0;
    private float x2 = 0;
    private float x3 = 0;
    private float x4 = 0;

    // array for storing line coordinates
    private ArrayList<Float> al;

    // buttons
    private Bitmap clearButton;
    private Bitmap undoButton;
//    private Bitmap cameraButton;
    private Bitmap colorButton;
    private Bitmap backgroundButton;

    // variables for pen color
    private int[] colors = {Color.WHITE, Color.BLACK, Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE};
    private int colorNum = 0;
    private int color;
    private ArrayList<Integer> colorsUsed;

    // variables for background color
    private int[] backgrounds = {Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN, Color.BLACK};
    private int backgroundNum = 0;
    private int backgroundColor = Color.BLACK;

    public GameView (Context context)
    {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);
        textColor = new Paint();
        al = new ArrayList<Float>();
        colorsUsed = new ArrayList<Integer>();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (thread.getState() == Thread.State.TERMINATED)
        {
            thread = new GameThread(getHolder(), this);
        }
        thread.setRunning(true);
        thread.start();
//        if (thread.getState() == Thread.State.NEW)
//        {
//            thread.start();
//        }
}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        while(retry)
        {
            try
            {
                thread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            buttonDown = true;

            // clear button collision
            if (event.getX() >= 5 && event.getX() <= 5 + 98 && event.getY() >= 25 && event.getY() <= 25 + 42)
            {
                al.clear();
                colorsUsed.clear();
            }

            // undo button collision
            if (event.getX() >= 120 && event.getX() <= 120 + 60 && event.getY() >= 10 && event.getY() <= 10 + 60)
            {
                undoDrawing();
            }

            // pen color button collision
            if (event.getX() >= 200 && event.getX() <= 200 + 60 && event.getY() >= 10 && event.getY() <= 10 + 60)
            {
                changeColor();
            }

            // background color button collision
            if (event.getX() >= 280 && event.getX() <= 280 + 60 && event.getY() >= 10 && event.getY() <= 10 + 60)
            {
                changeBackground();
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            buttonDown = false;
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            if (buttonDown && event.getY() >= 80)
            {
                al.add(new Float(x1));
                al.add(new Float(x2));
                x3 = event.getX();
                al.add(new Float(x3));
                x4 = event.getY();
                al.add(new Float(x4));
                for (int i = 0; i < 4; i++)
                {
                    colorsUsed.add(new Integer(colors[colorNum]));
                }
                this.invalidate();
            }
        }
        x1 = event.getX();
        x2 = event.getY();
        this.postInvalidate();
        return true;

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (canvas != null)
        {
            thread.setRunning(true);

            canvas.drawColor(backgroundColor);

            // clear
            clearButton = BitmapFactory.decodeResource(getResources(), R.drawable.clear);
            canvas.drawBitmap(clearButton, 5, 25, null);

            // undo
            undoButton = BitmapFactory.decodeResource(getResources(), R.drawable.undo);
            canvas.drawBitmap(undoButton, 120, 10, null);

//            // screenshot
//            cameraButton = BitmapFactory.decodeResource(getResources(), R.drawable.camera);
//            canvas.drawBitmap(cameraButton, 120, 10, null);

            // pen color
            colorButton = BitmapFactory.decodeResource(getResources(), R.drawable.color_wheel);
            canvas.drawBitmap(colorButton, 200, 10, null);

            // background color
            backgroundButton = BitmapFactory.decodeResource(getResources(), R.drawable.background_color);
            canvas.drawBitmap(backgroundButton, 280, 10, null);

            // System.out.println("x1: " + x1 + "\nx2: " + x2 + "\nx3: " + x3 + "\nx4: " + x4);

            try
            {
                for (int i = 0; i < al.size()-3; i += 4)
                {
                    textColor.setColor(colorsUsed.get(i));
                    canvas.drawLine(al.get(i), al.get(i + 1), al.get(i + 2), al.get(i + 3), textColor);
                }
            }
            catch (IndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void clearDrawing()
    {
        al.clear();
        colorsUsed.clear();
    }

    public void undoDrawing()
    {
        if (al.size() >= 4 && colorsUsed.size() >= 4)
        {
            for (int i = 1; i < 5; i++)
            {
                if (al.size() >= 4 && al.size()-i >= 0)
                {
                    al.remove(al.size() - i);
                }
                if (colorsUsed.size() >= 4 && colorsUsed.size()-i >= 0)
                {
                    colorsUsed.remove(colorsUsed.size() - i);
                }
            }
        }
    }

//    public void takeScreenshot()
//    {
//
//    }

    public void changeColor()
    {
        if (colorNum+1 == colors.length)
        {
            colorNum = 0;
        }
        else
        {
            colorNum++;
        }
        color = colors[colorNum];
    }

    public void changeBackground()
    {
        backgroundColor = backgrounds[backgroundNum];
        if (backgroundNum+1 == backgrounds.length)
        {
            backgroundNum = 0;
        }
        else
        {
            backgroundNum++;
        }
    }
}