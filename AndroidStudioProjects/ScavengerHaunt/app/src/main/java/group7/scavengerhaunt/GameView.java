package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * Created by Kevin on 3/18/2017.
 */

//SurfaceView implementation to show gamestate
public class GameView extends SurfaceView implements Runnable {
    //This variable is volatile for when the phone pauses or not
    volatile boolean playing;
    //Game thread
    private Thread gameThread = null;

    private Player player;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        player = new Player(context, screenX, screenY);

        surfaceHolder = getHolder();
        paint = new Paint();
    }

    @Override
    public void run() {
        while(playing) {
            //Draw and Update the game
            player.update();
            draw();
            controlFrameRate();
        }
    }

    public void draw() {
        if(surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(player.getImage(), player.getX(), player.getY(), paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
    //This supposedly makes the game run at a steady 60fps
    private void controlFrameRate() {
        try {
            gameThread.sleep(17);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        //When the game is paused
        playing = false;
        //Stop the thread
        try {
            gameThread.join();
        }
        catch(InterruptedException e) {
        }
    }

    public void resume() {
        //When the game is resumed
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            //Screen is released
            case MotionEvent.ACTION_UP:
                player.stopMoving();
                break;
            //Screen is touched, face in destination direction
            //If held down, move in that direction as well
            case MotionEvent.ACTION_DOWN:
                player.setDestination((int)motionEvent.getX(), (int)motionEvent.getY());
                player.startMoving();
                break;
            //Screen is being pressed & moved, player should move
            case MotionEvent.ACTION_MOVE:
                player.setDestination((int)motionEvent.getX(), (int)motionEvent.getY());
                //player.startMoving();
                break;
        }
        return true;
    }
}
