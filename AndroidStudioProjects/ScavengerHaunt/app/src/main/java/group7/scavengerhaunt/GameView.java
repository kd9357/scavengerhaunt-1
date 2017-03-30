package group7.scavengerhaunt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Kevin on 3/18/2017.
 */

//SurfaceView implementation to show state of the game
public class GameView extends SurfaceView implements Runnable {
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

    //Game Objects
    private Player player;
    private Interactables.Door door;
    private Interactables.Key key;
    private List<Obstacles> obstacleList = new ArrayList<>();

    //For drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    //Determines the text of the dialog fragment
    private boolean gameWon = false;
    private boolean gameFinished = false;

    Context context;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void initialize(int screenX, int screenY) {
        screenMaxX = screenX;
        screenMaxY = screenY;
        tileWidth = screenX / NUM_COLUMNS;
        tileHeight = screenY / NUM_ROWS;
        surfaceHolder = getHolder();
        paint = new Paint();
        //Create our background and decorations
        Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.floorboard);
        background = Bitmap.createScaledBitmap(temp, screenX, screenY, true);
        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall);
        wall = Bitmap.createScaledBitmap(temp, tileWidth * 2, screenY, true);
        //Create player
        player = new Player(context, tileWidth * 2, 0, screenX, screenY, tileWidth * 2, tileHeight * 2);
        //Create our interactables
        door = new Interactables.Door(context, 0, tileHeight, tileWidth * 2, tileHeight * 2);
        key = new Interactables.Key(context, screenX - screenX/3, screenY - screenY/3, tileWidth, tileHeight);
        //Create our obstacles
        obstacleList.add(new Obstacles.Table(context, tileWidth * 4, tileHeight *  4, 6, 6));
        obstacleList.add(new Obstacles.LoungeChair(context, tileWidth * 16, tileHeight * 8, 4, 4));
        obstacleList.add(new Obstacles.Fireplace(context, tileWidth *15, 0, 6, 5));
    }

    @Override
    public void run() {
        while(playing) {
            //Update and draw every frame
            update();
            draw();
            controlFrameRate();
        }
        if(gameFinished)
            handleEndGame();
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

        //Detect collision between interactables
        if(detectCollision(player.getHitBox(), key.getHitBox())) {
            player.foundKey();
        }
        if(detectCollision(player.getHitBox(), door.getHitBox()) && player.hasKey()) {
            playing = false;
            gameFinished = true;
            gameWon = true;
        }
    }

    public void draw() {
        if(surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            //Draw background
            canvas.drawBitmap(background, 0, 0, paint);
            canvas.drawBitmap(wall, 0, 0, paint);
            //Draw interactables
            if(!player.hasKey())
                canvas.drawBitmap(key.getImage(), key.getX(), key.getY(), paint);
            canvas.drawBitmap(door.getImage(), door.getX(), door.getY(), paint);
            //Draw obstacles
            for(Obstacles o : obstacleList) {
                canvas.drawBitmap(o.getImage(), o.getX(), o.getY(), paint);
            }
            //Draw player
            canvas.save();
            canvas.rotate((float) player.getAngleDegrees(), player.getCenterX(), player.getCenterY());
            canvas.drawBitmap(player.getImage(), player.getX(), player.getY(), paint);
            canvas.restore();
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

    private boolean detectCollision(Rect a, Rect b) {
        return Rect.intersects(a, b);
        //return detectCollisionX(a,b) && detectCollisionY(a,b);
    }

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
                player.setDestination((int) motionEvent.getX(), (int) motionEvent.getY());
                player.startMoving();
                break;
            //Screen is being pressed & moved, player should move
            case MotionEvent.ACTION_MOVE:
                player.setDestination((int)motionEvent.getX(), (int)motionEvent.getY());
                break;
        }
        return true;
    }

    public void handleEndGame() {
        if(surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            paint.setColor(Color.BLACK);
            paint.setTextSize(200);
            if(gameWon)
                canvas.drawText("You won!", 0, 8, 0, (float)canvas.getHeight()/2, paint);
            else
                canvas.drawText("You died!", 0, 9, 0, (float)canvas.getHeight()/2, paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

}
