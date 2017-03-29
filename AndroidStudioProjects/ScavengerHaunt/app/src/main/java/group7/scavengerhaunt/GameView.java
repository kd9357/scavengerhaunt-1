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

    private boolean gameFinished = false;

    private Context context;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        this.context = context;
        tileWidth = screenX / mColumns;
        tileHeight = screenY / mRows;
        player = new Player(context, screenX, screenY, tileWidth * 2, tileHeight * 2);
        door = new Interactables.Door(context, 0, 0, tileWidth * 2, tileHeight * 2);
        key = new Interactables.Key(context, screenX - screenX/3, screenY - screenY/3, tileWidth, tileHeight);
        table = new Obstacles.Table(context, tileWidth * 4, tileHeight *  4, 6, 6);
        loungeChair = new Obstacles.LoungeChair(context, tileWidth * 16, tileHeight * 8, 4, 4);
        surfaceHolder = getHolder();
        paint = new Paint();
        Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.floorboard);
        background = Bitmap.createScaledBitmap(temp, screenX, screenY, true);
        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall);
        wall = Bitmap.createScaledBitmap(temp, tileWidth * 2, screenY, true);
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
        int[] newCoords = player.update();
//        if(!detectCollision(playerHitBox, table.getHitBox()))
//            player.setLocation(playerHitBox.left, playerHitBox.top);
        int newX = player.getCenterX();
        int newY = player.getCenterY();
        if(!table.detectCollision(newCoords[0], player.getCenterY()) &&
                !loungeChair.detectCollision(newCoords[0], player.getCenterY()))
            newX = newCoords[0];
        if(!table.detectCollision(player.getCenterX(), newCoords[1]) &&
                !loungeChair.detectCollision(player.getCenterX(), newCoords[1]))
            newY = newCoords[1];

        player.setLocation(newX,newY);
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
                paint.setColor(Color.BLACK);
                paint.setTextSize(200);
                canvas.drawText("You won", 0, 7, 0, (float)canvas.getHeight()/2, paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
            else {
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
        //return Rect.intersects(a, b);
        return detectCollisionX(a,b) && detectCollisionY(a,b);
    }

    private boolean detectCollisionX(Rect a, Rect b) {
        return (a.left >= b.left && a.left <= b.right) || (b.left >= a.left && b.left <= a.right);
    }

    private boolean detectCollisionY(Rect a, Rect b) {
        return (a.top >= b.top && a.top <= b.bottom) || (b.top >= a.top && b.top <= a.bottom);
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
