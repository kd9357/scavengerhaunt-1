package group7.scavengerhaunt;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.shapes.Shape;
import android.util.Log;
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

    private Bitmap background;
    private Bitmap scaledBitmap;

    private Player player;
    private Door door;
    private Key key;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private boolean gameFinished = false;
    private boolean gameWon = false;

    private Context context;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        this.context = context;
        player = new Player(context, screenX, screenY);
        door = new Door(context, 0, 0);
        key = new Key(context, screenX - screenX/3, screenY - screenY/3);
        surfaceHolder = getHolder();
        paint = new Paint();
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        scaledBitmap = Bitmap.createScaledBitmap(background,
                screenX,
                screenY,
                true);
    }

    @Override
    public void run() {
        while(playing) {
            //Draw and Update the game
            update();
            draw();
            controlFrameRate();
            //Check if won, if yes set stage as cleared in unlockedStages
        }
    }

    public void update() {
        player.update();
        if(detectCollision(player.getHitBox(), key.getHitBox())) {
            player.foundKey();
        }
        if(detectCollision(player.getHitBox(), door.getHitBox()) && player.hasKey()) {
            handleEndGame();
        }
    }

    public void draw() {
        if(surfaceHolder.getSurface().isValid()) {
            if(gameFinished) {
                canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.WHITE);
                //canvas.drawRect(0, 0, (float)canvas.getWidth(), (float)canvas.getHeight(), paint);
//                paint.setColor(Color.BLACK);
//                paint.setStyle(Paint.Style.FILL);
//                canvas.drawPaint(paint);

                paint.setColor(Color.BLACK);
                paint.setTextSize(200);
//                canvas.drawText("Some Text", 10, 25, paint);
//                paint.setColor(0);
                canvas.drawText("You won", 0, 7, 0, (float)canvas.getHeight()/2, paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
            else {
                canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.BLACK);
                canvas.drawBitmap(scaledBitmap, 0, 0, paint);
                canvas.save();
                canvas.rotate((float) player.getAngleDegrees(), player.getCenterX(), player.getCenterY());
                canvas.drawBitmap(player.getImage(), player.getX(), player.getY(), paint);
                canvas.restore();
                if(!player.hasKey())
                    canvas.drawBitmap(key.getImage(), key.getX(), key.getY(), paint);
                canvas.drawBitmap(door.getImage(), door.getX(), door.getY(), paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
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

    private boolean detectCollision(Rect a, Rect b) {
        if (Rect.intersects(a, b)) {
            return true;
        }
        return false;
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

    public void handleEndGame() {
        gameFinished = true;
        //paint.setColor(0);
        //canvas.drawText("You won punk", 0, 12, (float)canvas.getWidth()/2, (float)canvas.getHeight()/2, paint);
    }
}
