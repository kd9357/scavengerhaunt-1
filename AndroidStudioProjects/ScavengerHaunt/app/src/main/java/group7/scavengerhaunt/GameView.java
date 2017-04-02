package group7.scavengerhaunt;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Kevin on 3/18/2017.
 */

//SurfaceView implementation to show state of the game
public class GameView extends View implements Runnable {
    //Activity that runs the view
    private GameActivity gameActivity;

    private final int NUM_COLUMNS = 20;
    private final int NUM_ROWS = 12;

    //Determines the minimum size of each object
    public static int tileWidth;
    public static int tileHeight;

    private int screenMaxX;
    private int screenMaxY;

    //This variable is volatile for when the phone pauses or not
    volatile boolean playing;

    //Game thread
    private Thread gameThread = null;

    //Static images
    private Bitmap background;
    private Bitmap wall;
    private Bitmap bear;
    private Bitmap papers;
    private Bitmap bottle;

    private Bitmap escaped;
    private Bitmap captured;

    //Game Objects
    private Player player;
    private Interactables.Door door;
    private Interactables.Key key;
    private List<Obstacles> obstacleList = new ArrayList<>();
    private List<Enemies> enemyList = new ArrayList<>();
    private List<Lights> lightList = new ArrayList<>();

    //For drawing
    private Paint paint;                //Draws images
    private Paint transparentPaint;     //Draws flashlight
    private Paint normalLightsPaint;    //Draws other lights
    private Canvas darkness;
    private Bitmap darknessBitmap;

    //Determines the text of the dialog fragment
    private boolean gameWon = false;
    private boolean gameFinished = false;

    Context context;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void initialize(GameActivity g, int screenX, int screenY) {
        this.gameActivity = g;
        screenMaxX = screenX;
        screenMaxY = screenY;
        tileWidth = screenX / NUM_COLUMNS;
        tileHeight = screenY / NUM_ROWS;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        //Setup lighting
        darknessBitmap = Bitmap.createBitmap(screenX, screenY, Bitmap.Config.ARGB_8888);
        darkness = new Canvas(darknessBitmap);
        transparentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        transparentPaint.setColor(Color.TRANSPARENT);
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        normalLightsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        normalLightsPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        normalLightsPaint.setColor(Color.TRANSPARENT);
        normalLightsPaint.setMaskFilter(new BlurMaskFilter(60, BlurMaskFilter.Blur.NORMAL));

        //Create our background and decorations
        Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.floorboard);
        background = Bitmap.createScaledBitmap(temp, screenX, screenY, true);
        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall);
        wall = Bitmap.createScaledBitmap(temp, tileWidth * 2, screenY, true);
        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bear);
        bear = Bitmap.createScaledBitmap(temp, tileWidth * 5, tileHeight * 5, true);
        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.papers_ground);
        papers = Bitmap.createScaledBitmap(temp, tileWidth * 2, tileHeight * 2, true);
        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.liquor);
        bottle = Bitmap.createScaledBitmap(temp, tileWidth, tileHeight, true);

        //End Game drawables
        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.escaped);
        escaped = Bitmap.createScaledBitmap(temp, screenX - tileWidth * 3, screenY - tileHeight * 3, true);
        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.captured);
        captured = Bitmap.createScaledBitmap(temp, screenX - tileWidth * 3, screenY - tileHeight *3, true);

        //Create player
        player = new Player(context, tileWidth * 18, tileHeight * 6, tileWidth * 2, 0, screenX, screenY, tileWidth * 2, tileHeight * 2);
        player.setDirection(-1, 0);
        //Create our interactables
        door = new Interactables.Door(context, 0, tileHeight, tileWidth * 2, tileHeight * 2);
        key = new Interactables.Key(context, (int)(tileWidth * 2.5), tileHeight * 10, tileWidth, tileHeight);
        //Create our obstacles
        obstacleList.add(new Obstacles.SeatTwo(context, (int)(tileWidth * 7.2), (int)(tileHeight * 5.7), 2, 3));
        obstacleList.add(new Obstacles.SeatThree(context, (int)(tileWidth * 3.8), (int)(tileHeight * 8.7), 2, 2));
        obstacleList.add(new Obstacles.SeatFour(context, tileWidth * 2, (int)(tileHeight * 5.2), 2, 2));
        obstacleList.add(new Obstacles.Table(context, tileWidth * 3, tileHeight *  5, 5, 5));
        obstacleList.add(new Obstacles.LoungeChair(context, tileWidth * 16, tileHeight * 8, 4, 4));
        obstacleList.add(new Obstacles.Box(context, tileWidth * 3, 0, 3, 2));
        obstacleList.add(new Obstacles.Fireplace(context, tileWidth *15, 0, 6, 5));

        //Create our enemies
        enemyList.add(new Enemies.Ghost(context, tileWidth * 12, tileHeight * 2, 3, 3));

        //Create our lights
        for(Obstacles o : obstacleList) {
            if(o.hasLight())
                lightList.add(o.getLight());
        }
        lightList.add(player.getSelfLight());
    }

    @Override
    public void run() {
        while(playing) {
            //Update and draw every frame
            update();
            postInvalidate();
            controlFrameRate();
        }
    }

    public void update() {
        //Update player location
        int[] newCoords = player.update();
        int newX = newCoords[0];
        int newY = newCoords[1];

        //Detect collision between obstacles
        //If collision detected, reset coords
        for(Obstacles o : obstacleList) {
            if(o.detectCollision(newCoords[0], player.getCenterY()))
                newX = player.getCenterX();
            if(o.detectCollision(player.getCenterX(), newCoords[1]))
                newY = player.getCenterY();
        }
        player.setLocation(newX,newY);

        //Update enemy location & detect collision
        for(Enemies e : enemyList) {
            e.update();
            if(e.detectCollision(player.getHitBox())) {
                playing = false;
                gameFinished = true;
                gameWon = false;
                break;
            }
        }

        //Detect collision between interactables
        //if(detectCollision(player.getHitBox(), key.getHitBox())) {
        if(key.detectCollision(player.getCenterX(),player.getCenterY())) {
            player.foundKey();
        }
        if(door.detectCollision(player.getCenterX(),player.getCenterY()) && player.hasKey()) {
            playing = false;
            gameFinished = true;
            gameWon = true;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!gameFinished) {
            //Draw background
            canvas.drawBitmap(background, 0, 0, paint);
            canvas.drawBitmap(wall, 0, 0, paint);

            canvas.drawBitmap(bear, tileWidth * 12, tileHeight * 2, paint);
            canvas.drawBitmap(papers, tileWidth * 15, tileHeight * 9, paint);
            canvas.drawBitmap(bottle, tileWidth * 15, tileHeight * 10, paint);

            //Draw interactables
            if(!player.hasKey())
                key.drawInteractable(canvas, paint);
            door.drawInteractable(canvas, paint);
            //Draw obstacles
            for (Obstacles o : obstacleList)
                o.drawObstacle(canvas, paint);

            //Draw player below lights (for now)
            canvas.save();
            canvas.rotate((float) player.getAngleDegrees(), player.getCenterX(), player.getCenterY());
            canvas.drawBitmap(player.getImage(), player.getX(), player.getY(), paint);
            canvas.restore();

            //Draw enemies
            for(Enemies e : enemyList) {
                canvas.save();
                canvas.rotate((float) e.getAngleDegrees(), e.getCenterX(), e.getCenterY());
                e.drawEnemy(canvas, paint);
                canvas.restore();
            }

            //Draw Lights
            darkness.drawRect(0, 0, screenMaxX, screenMaxY, paint);
            //Draw Flashlight
            darkness.save();
            darkness.rotate((float) player.getAngleDegrees(), player.getCenterX(), player.getCenterY());
            Lights.Flashlight f = player.getFlashLight();
            f.drawLight(darkness, transparentPaint);
            darkness.restore();
            //Draw other lights
            for(Lights l : lightList)
                l.drawLight(darkness, normalLightsPaint);
            darkness.save();
            darkness.rotate((float) player.getAngleDegrees(), player.getCenterX(), player.getCenterY());
            f.drawColorLight(darkness);
            darkness.restore();
            //Draws shadows and lights onto canvas
            canvas.drawBitmap(darknessBitmap, 0, 0, paint);

            //Draw HUD
            if(player.hasKey()) {
                canvas.drawBitmap(key.getImage(), 0, 0, paint);
            }
        }
        //Handle endgame (to be removed and replaced with dialog fragment)
        else {
            if (gameWon)
                canvas.drawBitmap(escaped, (int) (tileWidth * 1.5), (int) (tileHeight * 1.5), paint);
            else
                canvas.drawBitmap(captured, (int) (tileWidth * 1.5), (int) (tileHeight * 1.5), paint);
        }
}

    //This supposedly makes the game run at a steady 60fps
    //TODO: modify the length of sleep to maintain steady fps
    //We can shoot for 30fps instead
    //Need to optimize, 30fps is getting harder to achieve
    private void controlFrameRate() {
        try {
            gameThread.sleep(30);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    private boolean detectCollision(Rect a, Rect b) {
//        return Rect.intersects(a, b);
//        //return detectCollisionX(a,b) && detectCollisionY(a,b);
//    }

//    private boolean detectCollisionX(Rect a, Rect b) {
//        return (a.left >= b.left && a.left <= b.right) || (b.left >= a.left && b.left <= a.right);
//    }
//
//    private boolean detectCollisionY(Rect a, Rect b) {
//        return (a.top >= b.top && a.top <= b.bottom) || (b.top >= a.top && b.top <= a.bottom);
//    }

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
                if (gameFinished) {
                    if (motionEvent.getX() > screenMaxX - tileWidth * 6 && motionEvent.getX() < screenMaxX - tileWidth) {
                        if (motionEvent.getY() > screenMaxY - tileHeight * 5 && motionEvent.getY() < screenMaxY - tileHeight) {
                            gameActivity.finish();
                        }
                    }
                }
                else {
                    player.setDestination((int) motionEvent.getX(), (int) motionEvent.getY());
                    player.startMoving();
                }
                break;
            //Screen is being pressed & moved, player should move
            case MotionEvent.ACTION_MOVE:
                player.setDestination((int)motionEvent.getX(), (int)motionEvent.getY());
                break;
        }
        return true;
    }

}
