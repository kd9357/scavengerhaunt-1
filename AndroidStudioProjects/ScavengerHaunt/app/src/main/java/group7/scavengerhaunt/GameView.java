package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * Created by Kevin on 3/18/2017.
 */

//SurfaceView implementation to show gamestate
public class GameView extends SurfaceView implements Runnable {
    private final int mColumns = 20;
    private final int mRows = 12;
    //Determines the minimum size of each object
    public static int tileWidth;
    public static int tileHeight;

    //This variable is volatile for when the phone pauses or not
    volatile boolean playing;

    //Game thread
    private Thread gameThread = null;

    private Bitmap background;
    private Bitmap wall;

    private Player player;
    private Interactables.Door door;
    private Interactables.Key key;
    private Obstacles.Table table;
    private Obstacles.LoungeChair loungeChair;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private boolean gameWon = false;

    private Context context;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        this.context = context;
        tileWidth = screenX / mColumns;
        tileHeight = screenY / mRows;
        surfaceHolder = getHolder();
        paint = new Paint();
        //Create our objects
        Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.floorboard);
        background = Bitmap.createScaledBitmap(temp, screenX, screenY, true);
        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall);
        wall = Bitmap.createScaledBitmap(temp, tileWidth * 2, screenY, true);
        player = new Player(context, tileWidth * 2, 0, screenX, screenY, tileWidth * 2, tileHeight * 2);
        door = new Interactables.Door(context, 0, 0, tileWidth * 2, tileHeight * 2);
        key = new Interactables.Key(context, screenX - screenX/3, screenY - screenY/3, tileWidth, tileHeight);
        table = new Obstacles.Table(context, tileWidth * 4, tileHeight *  4, 6, 6);
        loungeChair = new Obstacles.LoungeChair(context, tileWidth * 16, tileHeight * 8, 4, 4);
    }

    @Override
    public void run() {
        while(playing) {
            //Update and draw every frame
            update();
            draw();
            controlFrameRate();
            //Check if won, if yes set stage as cleared in unlockedStages
        }
        handleEndGame();
    }

    public void update() {
        //Update player location
        int[] newCoords = player.update();
        int newX = player.getCenterX();
        int newY = player.getCenterY();

        //Detect collision between obstacles
        if(!table.detectCollision(newCoords[0], player.getCenterY()) &&
                !loungeChair.detectCollision(newCoords[0], player.getCenterY()))
            newX = newCoords[0];
        if(!table.detectCollision(player.getCenterX(), newCoords[1]) &&
                !loungeChair.detectCollision(player.getCenterX(), newCoords[1]))
            newY = newCoords[1];
        player.setLocation(newX,newY);

        //Detect collision between interactables
        if(detectCollision(player.getHitBox(), key.getHitBox())) {
            player.foundKey();
        }
        if(detectCollision(player.getHitBox(), door.getHitBox()) && player.hasKey()) {
            playing = false;
            gameWon = true;
        }
    }

    public void draw() {
        if(surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(background, 0, 0, paint);
            canvas.drawBitmap(wall, 0, 0, paint);
            if(!player.hasKey())
                canvas.drawBitmap(key.getImage(), key.getX(), key.getY(), paint);
            canvas.drawBitmap(door.getImage(), door.getX(), door.getY(), paint);
            canvas.drawBitmap(table.getImage(), table.getX(), table.getY(), paint);
            canvas.drawBitmap(loungeChair.getImage(), loungeChair.getX(), loungeChair.getY(), paint);
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
