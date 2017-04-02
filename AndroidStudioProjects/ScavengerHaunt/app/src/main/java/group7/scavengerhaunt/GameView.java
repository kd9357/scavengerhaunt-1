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
    private Bitmap escaped;
    private Bitmap captured;

    //Game Objects
    private Player player;
    private Interactables.Door door;
    private Interactables.Key key;
    private List<Obstacles> obstacleList = new ArrayList<>();
    private List<Lights> lightList = new ArrayList<>();

    //For drawing
    private Paint paint;
    private Paint transparentPaint;
    private Paint normalLightsPaint;
    private Paint radialGradientPaint;
    private Canvas darkness;
    private Bitmap darknessBitmap;

    //Determines the text of the dialog fragment
    private boolean gameWon = false;
    private boolean gameFinished = false;

    private GameActivity g;

    Context context;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void initialize(GameActivity g, int screenX, int screenY) {
        this.g = g;

        screenMaxX = screenX;
        screenMaxY = screenY;
        tileWidth = screenX / NUM_COLUMNS;
        tileHeight = screenY / NUM_ROWS;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //paint.setColor(getResources().getColor(R.color.colorPrimary));
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

        radialGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //Create our background and decorations
        Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.floorboard);
        background = Bitmap.createScaledBitmap(temp, screenX, screenY, true);
        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.escaped);
        escaped = Bitmap.createScaledBitmap(temp, screenX - tileWidth * 3, screenY - tileHeight * 3, true);
        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.captured);
        captured = Bitmap.createScaledBitmap(temp, screenX - tileWidth * 3, screenY - tileHeight *3, true);



        temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall);
        wall = Bitmap.createScaledBitmap(temp, tileWidth * 2, screenY, true);
        //Create player
        player = new Player(context, tileWidth * 2, 0, screenX, screenY, tileWidth * 2, tileHeight * 2);
        //Create our interactables
        door = new Interactables.Door(context, 0, tileHeight, tileWidth * 2, tileHeight * 2);
        key = new Interactables.Key(context, screenX - screenX/3, screenY - screenY/3, tileWidth, tileHeight);
        //Create our obstacles
        obstacleList.add(new Obstacles.Table(context, tileWidth * 4, tileHeight *  4, 5, 5));
        obstacleList.add(new Obstacles.LoungeChair(context, tileWidth * 16, tileHeight * 8, 4, 4));
        obstacleList.add(new Obstacles.Fireplace(context, tileWidth *15, 0, 6, 5));
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
        // handleEndGame
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
            //canvas.drawColor(Color.BLACK);
            //Draw background
            canvas.drawBitmap(background, 0, 0, paint);
            canvas.drawBitmap(wall, 0, 0, paint);
            //Draw interactables
            if (!player.hasKey())
                canvas.drawBitmap(key.getImage(), key.getX(), key.getY(), paint);
            canvas.drawBitmap(door.getImage(), door.getX(), door.getY(), paint);
            //Draw obstacles
            for (Obstacles o : obstacleList)
                canvas.drawBitmap(o.getImage(), o.getX(), o.getY(), paint);

            //Draw Lights
            darkness.drawRect(0, 0, screenMaxX, screenMaxY, paint);
            //Draw Flashlight
            darkness.save();
            darkness.rotate((float) player.getAngleDegrees(), player.getCenterX(), player.getCenterY());
            Lights.Flashlight f = player.getFlashLight();
            darkness.drawArc(f.getCircle(), f.getStartingAngle(), f.getSweepingAngle(), true, transparentPaint);
            RadialGradient gradient = new RadialGradient(f.getX(), f.getY(), f.getRadius(),
                    new int[] {0x00000000, 0xFF000000}, null, android.graphics.Shader.TileMode.CLAMP);
            radialGradientPaint.setShader(gradient);
            darkness.drawArc(f.getLargerCircle(), f.getStartingAngle() - 30, f.getSweepingAngle() + 30, true, radialGradientPaint);
            darkness.restore();
            //Draw other lights
            for(Lights l : lightList) {
                darkness.drawCircle(l.getX(), l.getY(), l.getRadius(), normalLightsPaint);
            }
            //Draws shadows and lights onto canvas
            canvas.drawBitmap(darknessBitmap, 0, 0, paint);
            
            //Draw player over lights (for now)
            canvas.save();
            canvas.rotate((float) player.getAngleDegrees(), player.getCenterX(), player.getCenterY());
            canvas.drawBitmap(player.getImage(), player.getX(), player.getY(), paint);
            canvas.restore();

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
                            g.finish();
                        }
                    }
                }
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

}
