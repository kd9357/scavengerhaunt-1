package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;


/**
 * Created by Kevin on 3/18/2017.
 */

//SurfaceView implementation to show state of the game
public class GameView extends View implements Runnable {
    //Activity that runs the view
    private GameActivity gameActivity;
    private SensorManager sensorMgr;

    //Determines the minimum size of each object
    public static int tileWidth;
    public static int tileHeight;

    private int screenMinX;
    private int screenMinY;
    private int screenMaxX;
    private int screenMaxY;

    //This variable is volatile for when the phone pauses or not
    volatile boolean playing;

    // Used for recharge mechanic
    private long lastChargeTime;
    private long lastSpeedTime;
    private boolean timeSet;
    private float last_x;
    private float last_y;
    private float last_z;

    //Game thread
    private Thread gameThread = null;

    //Static images
    private Bitmap background;

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
    private Paint blurredLightsPaint;    //Draws other lights
    private Paint hitBoxPaint;
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

public void initialize(GameActivity g, Levels gameObjects) {
    this.gameActivity = g;
    sensorMgr = (SensorManager) context.getSystemService(SENSOR_SERVICE);
    Sensor accelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorMgr.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);

    tileWidth = gameObjects.getTileWidth();
    tileHeight = gameObjects.getTileHeight();
    screenMinX = gameObjects.getScreenMinX();
    screenMinY = gameObjects.getScreenMinY();
    screenMaxX = gameObjects.getScreenMaxX();
    screenMaxY = gameObjects.getScreenMaxY();

    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(Color.BLACK);

    //Setup lighting
    darknessBitmap = Bitmap.createBitmap(screenMaxX, screenMaxY, Bitmap.Config.ARGB_8888);
    darkness = new Canvas(darknessBitmap);
    transparentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    transparentPaint.setColor(Color.TRANSPARENT);
    transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

    blurredLightsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    blurredLightsPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
    blurredLightsPaint.setColor(Color.TRANSPARENT);
    blurredLightsPaint.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.NORMAL));

    hitBoxPaint = new Paint();
    hitBoxPaint.setStyle(Paint.Style.STROKE);
    hitBoxPaint.setColor(Color.GREEN);

    background = gameObjects.getBackground();
    player = gameObjects.getPlayer();
    door = gameObjects.getDoor();
    key = gameObjects.getKey();
    obstacleList = gameObjects.getObstacleList();
    enemyList = gameObjects.getEnemyList();
    lightList = gameObjects.getLightList();

    //Create our background and decorations
    Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.escaped);
    escaped = Bitmap.createScaledBitmap(temp, screenMaxX - tileWidth * 3, screenMaxY - tileHeight * 3, true);
    temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.captured);
    captured = Bitmap.createScaledBitmap(temp, screenMaxX - tileWidth * 3, screenMaxY - tileHeight *3, true);


    // Set battery recharge time
    lastChargeTime = System.currentTimeMillis();
    lastSpeedTime = lastChargeTime;
    timeSet = false;
}

    @Override
    public void run() {
        while(playing) {
            //Update and draw every frame
            update();
            postInvalidate();
            controlFrameRate();
        }
        //TODO: if using dialog to end game handle here
    }

    public void update() {
        //Update player location
        int[] newCoords = player.update();
        Lights.Flashlight f = player.getFlashLight();

        //Detect hitbox intersections
        obstacleCollision(newCoords[0], newCoords[1], f);
        enemyCollision(f);
        interactablesCollision(f);
    }

    private void obstacleCollision(int newX, int newY, Lights.Flashlight f) {
        //If collision detected, reset coords
        for(Obstacles o : obstacleList) {
            Rect box = o.getImageBox();
            //Illuminate obstacle if collides with flashlight || light attached to it
            o.setIlluminated(f.detectCollision(box) || o.hasLight());
            if(!o.isIlluminated()) {
                for (Lights l : lightList) {
                    if (l.detectCollision(box)) {
                        o.setIlluminated(true);
                        break;
                    }
                }
            }
            if(o.detectCollision(newX, player.getCenterY()))
                newX = player.getCenterX();
            if(o.detectCollision(player.getCenterX(), newY))
                newY = player.getCenterY();
        }
        if(newX != player.getCenterX() || newY != player.getCenterY())
            player.setLocation(newX,newY);
    }

    private void enemyCollision(Lights.Flashlight f) {
        for(Enemies e : enemyList) {
            e.update();
            Rect box = e.getImageBox();
            e.setIlluminated(f.detectCollision(box));
            if(!e.isIlluminated()) {
                for (Lights l : lightList) {
                    if (l.detectCollision(box)) {
                        e.setIlluminated(true);
                        break;
                    }
                }
            }
            if(e.detectCollision(player.getHitBox())) {
            //if(e.detectCollision(player.getCenterX(), player.getCenterY())) {
                playing = false;
                gameFinished = true;
                gameWon = false;
                break;
            }
        }
    }

    private void interactablesCollision(Lights.Flashlight f) {
        boolean keyHit = Rect.intersects(key.getHitBox(), player.getHitBox());
        //if(key.detectCollision(player.getCenterX(),player.getCenterY()) && !player.hasKey()) {
        if(keyHit && !player.hasKey()) {
            if(MainActivity.mSoundOn) {
                MediaPlayer player = MediaPlayer.create(context, R.raw.key_found);
                player.setVolume(0.5f, 0.5f);
                player.start();
            }
            player.foundKey();
        }
        Rect box = key.getImageBox();
        key.setIlluminated(f.detectCollision(box));
        if(!key.isIlluminated()) {
            for (Lights l : lightList) {
                if (l.detectCollision(box)) {
                    key.setIlluminated(true);
                    break;
                }
            }
        }
        box = door.getImageBox();
        door.setIlluminated(f.detectCollision(box));
        if(!door.isIlluminated()) {
            for (Lights l : lightList) {
                if (l.detectCollision(box)) {
                    door.setIlluminated(true);
                    break;
                }
            }
        }
        if(door.detectCollision(player.getCenterX(),player.getCenterY()) && player.hasKey()) {
            if(MainActivity.mSoundOn) {
                MediaPlayer player = MediaPlayer.create(context, R.raw.door_opened);
                player.start();
            }
            playing = false;
            gameFinished = true;
            gameWon = true;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!gameFinished) {
            //Draw background + shadowmap
            canvas.drawBitmap(background, 0, 0, paint);
            drawInteractables(canvas);
            drawObstacles(canvas);
            //Draw player below lights (for now)
            //Drawing underneath lights disguises some lag of the flashlight
            drawPlayer(canvas);
            drawEnemies(canvas);
            drawLights(canvas);
            drawHUD(canvas);
        }
        //Handle endgame (to be removed and replaced with dialog fragment)
        else {
            if (gameWon)
                canvas.drawBitmap(escaped, (int) (tileWidth * 1.5), (int) (tileHeight * 1.5), paint);
            else
                canvas.drawBitmap(captured, (int) (tileWidth * 1.5), (int) (tileHeight * 1.5), paint);
        }
    }

    private void drawInteractables(Canvas canvas) {
        if((!player.hasKey() && key.isIlluminated()) || (MainActivity.mDebugModeOn && !player.hasKey())) {
            key.drawInteractable(canvas, paint);
            if(MainActivity.mDebugModeOn && !player.hasKey())
                key.drawHitBox(canvas, hitBoxPaint);
        }
        if(door.isIlluminated() || MainActivity.mDebugModeOn)
            door.drawInteractable(canvas, paint);
        if(MainActivity.mDebugModeOn) {
            door.drawHitBox(canvas, hitBoxPaint);
            canvas.drawRect(screenMinX, screenMinY, screenMaxX, screenMaxY, hitBoxPaint);
        }
    }

    private void drawObstacles(Canvas canvas) {
        for (Obstacles o : obstacleList) {
            if(o.illuminated || MainActivity.mDebugModeOn)
                o.drawObstacle(canvas, paint);
            if(MainActivity.mDebugModeOn)
                o.drawHitBox(canvas, hitBoxPaint);
        }
    }

    private void drawPlayer(Canvas canvas) {
        canvas.save();
        canvas.rotate((float) player.getAngleDegrees(), player.getCenterX(), player.getCenterY());
        canvas.drawBitmap(player.getImage(), player.getX(), player.getY(), paint);
        canvas.restore();
        if(MainActivity.mDebugModeOn)
            canvas.drawRect(player.getHitBox(), hitBoxPaint);
    }

    private void drawEnemies(Canvas canvas) {
        for(Enemies e : enemyList) {
            if(e.illuminated || MainActivity.mDebugModeOn) {
                canvas.save();
                canvas.rotate((float) e.getAngleDegrees(), e.getCenterX(), e.getCenterY());
                e.drawEnemy(canvas, paint);
                canvas.restore();
            }
            if(MainActivity.mDebugModeOn)
                e.drawHitBox(canvas, hitBoxPaint);
        }
    }

    private void drawLights(Canvas canvas) {
        darkness.drawRect(0, 0, screenMaxX, screenMaxY, paint);
        //Draw Flashlight
        darkness.save();
        darkness.rotate((float) player.getAngleDegrees(), player.getCenterX(), player.getCenterY());
        Lights.Flashlight f = player.getFlashLight();
        f.drawLight(darkness, transparentPaint);
        darkness.restore();
        //Draw other lights
        for(Lights l : lightList) {
            if(MainActivity.mSoftShadowsOn)
                l.drawLight(darkness, blurredLightsPaint);
            else
                l.drawLight(darkness, transparentPaint);
            if(MainActivity.mDebugModeOn)
                l.drawLight(canvas, hitBoxPaint);
        }
        //Draw flashlight color over other lights
        darkness.save();
        darkness.rotate((float) player.getAngleDegrees(), player.getCenterX(), player.getCenterY());
        f.drawColorLight(darkness);
        darkness.restore();
        //Draws shadows and lights onto canvas
        if(!MainActivity.mDebugModeOn) {
            canvas.drawBitmap(darknessBitmap, 0, 0, paint);
        }
    }

    private void drawHUD(Canvas canvas) {
        player.battery.drawInteractable(canvas, paint);
        if(player.hasKey()) {
            canvas.drawBitmap(key.getImage(), 3 * tileWidth / 2, 0, paint);
        }
    }

    //This supposedly makes the game run at a steady 60fps
    //TODO: modify the length of sleep to maintain steady fps
    //We can shoot for 30fps instead
    //Need to optimize, 30fps is getting harder to achieve
    private void controlFrameRate() {
        try {
            gameThread.sleep(33);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        //When the game is paused
        playing = false;
        sensorMgr.unregisterListener(sensorEventListener);
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
        Sensor accelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMgr.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        gameThread = new Thread(this);
        gameThread.start();
        if(MainActivity.mDebugModeOn) {
            player.updateCharge(1);
        }
    }

    private SensorEventListener sensorEventListener =
            new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    long curTime = System.currentTimeMillis();
                    long diffTime = curTime - lastSpeedTime;

                    if (diffTime > 100) {
                        lastSpeedTime = curTime;
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];
                        float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 1000;

                        if (speed > 200) {
                            if (!timeSet) {
                                timeSet = true;
                                lastChargeTime = curTime;
                            }
                            else if (curTime - lastChargeTime > 500) {
                                player.updateCharge(0.20f);
                                timeSet = false;
                                Log.d("In GameView:", "RECHARGE!!!!!");
                            }
                        }

                        last_x = x;
                        last_y = y;
                        last_z = z;
                    }
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // Do nothing
                }
            };


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
                //TODO: If using dialog remove this
                if (gameFinished) {
                    if (motionEvent.getX() > screenMaxX - tileWidth * 6 && motionEvent.getX() < screenMaxX - tileWidth) {
                        if (motionEvent.getY() > screenMaxY - tileHeight * 5 && motionEvent.getY() < screenMaxY - tileHeight) {
                            gameActivity.finish();
                        }
                    }
                    //if gamelost, do gameActivity.recreate() to reset
                    //otherwise, get a new intent for next stage, gameActivity.finish(), then startActivity(intent)
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
