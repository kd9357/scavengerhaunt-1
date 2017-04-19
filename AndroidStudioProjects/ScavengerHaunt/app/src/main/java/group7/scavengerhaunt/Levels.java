package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 4/18/2017.
 */

public class Levels {
    private int tileWidth;
    private int tileHeight;
    private int screenMinX;
    private int screenMinY;
    private int screenMaxX;
    private int screenMaxY;

    private Bitmap background;
    private Player player;
    private Interactables.Door door;
    private Interactables.Key key;
    private List<Obstacles> obstacleList = new ArrayList<>();
    private List<Enemies> enemyList = new ArrayList<>();
    private List<Lights> lightList = new ArrayList<>();

    private Context context;
    private GameActivity gameActivity;

    public Levels(Context context, int screenX, int screenY) {
        tileWidth = screenX / GameActivity.NUM_COLUMNS;
        tileHeight = screenY / GameActivity.NUM_ROWS;
        screenMaxX = screenX;
        screenMaxY = screenY;
        this.context = context;
        gameActivity = (GameActivity)context;
    }

    public void setStage(Context context, int stageNum) {
        Bitmap temp;
        switch(stageNum) {
            case 0:
                screenMinX = tileWidth * 2;
                screenMinY = 0;
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_full);
                background = Bitmap.createScaledBitmap(temp, screenMaxX, screenMaxY, true);
                //Context, startX, startY, screenX min, screenY min, screenX max, screenY max, width of player, width of height
                player = new Player(context, tileWidth * 18, tileHeight * 6, screenMinX, screenMinY, screenMaxX, screenMaxY, tileWidth * 2, tileHeight * 2);
                player.setDirection(-1, 0);
                //Create our interactables
                door = new Interactables.Door(context, 0, tileHeight, tileWidth * 2, tileHeight * 2);
                key = new Interactables.Key(context, (int)(tileWidth * 2.5), tileHeight * 10, tileWidth, tileHeight);
                //Create our obstacles
                obstacleList.add(new Obstacles.Table(context, tileWidth * 2, tileHeight *  4, 7, 7));
                obstacleList.add(new Obstacles.LoungeChair(context, tileWidth * 16, tileHeight * 8, 4, 4));
                obstacleList.add(new Obstacles.Box(context, tileWidth * 3, 0, 3, 2));
                obstacleList.add(new Obstacles.Fireplace(context, tileWidth *15, 0, 6, 5));
                //Create our enemies
                enemyList.add(new Enemies.Ghost(context, tileWidth * 12, tileHeight * 2, 3, 3));
                //Create our lights
                lightList.add(player.getSelfLight());
                for(Obstacles o : obstacleList) {
                    if(o.hasLight())
                        lightList.add(o.getLight());
                }
                break;
            default:
                gameActivity.finish();
                Log.d("LOADING LEVEL ERROR", "LEVEL DOES NOT EXIST");
        }

    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getScreenMinX() {
        return screenMinX;
    }

    public int getScreenMinY() {
        return screenMinY;
    }

    public int getScreenMaxX() {
        return screenMaxX;
    }

    public int getScreenMaxY() {
        return screenMaxY;
    }

    public Bitmap getBackground() {
        return background;
    }

    public Player getPlayer() {
        return player;
    }

    public Interactables.Door getDoor() {
        return door;
    }

    public Interactables.Key getKey() {
        return key;
    }

    public List<Obstacles> getObstacleList() {
        return obstacleList;
    }

    public List<Enemies> getEnemyList() {
        return enemyList;
    }

    public List<Lights> getLightList() {
        return lightList;
    }
}
