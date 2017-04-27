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
        Enemies.Mummy m;
        Enemies.Ghost g;
        Enemies.Zombie z;
        Lights l;
        switch(stageNum) {
            case 0:
                screenMinX = tileWidth;
                screenMinY = tileHeight * 2;
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_full_0);
                background = Bitmap.createScaledBitmap(temp, screenMaxX, screenMaxY, true);
                player = new Player(context, tileWidth * 18, tileHeight * 3, screenMinX, screenMinY, screenMaxX, screenMaxY, (int)(tileWidth * 2.5), (int)(tileHeight * 2.5));
                player.setDirection(-1, 0);
                door = new Interactables.Door(context, tileWidth * 4, 0, tileWidth * 2, tileHeight * 2, true);
                key = new Interactables.Key(context, tileWidth * 18, tileHeight * 10, tileWidth, tileHeight);
                obstacleList.add(new Obstacles.WoodTable(context, tileWidth * 13, tileHeight * 7, 6, 3, true));
                obstacleList.add(new Obstacles.LoungeChair(context, tileWidth * 4, tileHeight * 7, 4, 4));
                lightList.add(player.getSelfLight());
                //Wall sconce
                l = new Lights(tileWidth * 7, tileHeight, GameActivity.tileWidth * 2);
                lightList.add(l);
                l = new Lights(tileWidth * 3, tileHeight * 10, GameActivity.tileWidth * 3);
                lightList.add(l);
                break;
            case 1:
                screenMinX = tileWidth * 2;
                screenMinY = 0;
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_full);
                background = Bitmap.createScaledBitmap(temp, screenMaxX, screenMaxY, true);
                //Context, startX, startY, screenX min, screenY min, screenX max, screenY max, width of player, width of height
                player = new Player(context, tileWidth * 18, tileHeight * 6, screenMinX, screenMinY, screenMaxX, screenMaxY, (int)(tileWidth * 2.5), (int)(tileHeight * 2.5));
                player.setDirection(-1, 0);
                //Create our interactables
                door = new Interactables.Door(context, 0, tileHeight, tileWidth * 2, tileHeight * 2, false);
                key = new Interactables.Key(context, (int)(tileWidth * 2.5), tileHeight * 10, tileWidth, tileHeight);
                //Create our obstacles
                obstacleList.add(new Obstacles.Table(context, tileWidth * 2, tileHeight *  4, 7, 7));
                obstacleList.add(new Obstacles.LoungeChair(context, tileWidth * 16, tileHeight * 8, 4, 4));
                obstacleList.add(new Obstacles.Box(context, tileWidth * 3, 0, 3, 2));
                obstacleList.add(new Obstacles.Fireplace(context, tileWidth *15, 0, 6, 5));
                //Create our enemies
                m =  new Enemies.Mummy(context, tileWidth * 12, tileHeight * 2, 3, 3, -1, 0);
                m.setPath(GameActivity.tileWidth * 7);
                //enemyList.add(new Enemies.Mummy(context, tileWidth * 12, tileHeight * 2, 3, 3));
                enemyList.add(m);
                //Create our lights
                lightList.add(player.getSelfLight());
                for(Obstacles o : obstacleList) {
                    if(o.hasLight())
                        lightList.add(o.getLight());
                }
                break;
            case 2:
                screenMinX = tileWidth;
                screenMinY = tileHeight * 2;
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_full_0);
                background = Bitmap.createScaledBitmap(temp, screenMaxX, screenMaxY, true);
                player = new Player(context, tileWidth * 18, tileHeight * 9, screenMinX, screenMinY, screenMaxX, screenMaxY, (int)(tileWidth * 2.5), (int)(tileHeight * 2.5));
                player.setDirection(-1, 0);
                door = new Interactables.Door(context, tileWidth * 16, 0, tileWidth * 2, tileHeight * 2, true);
                key = new Interactables.Key(context, (int)(tileWidth * 2.5), tileHeight * 3, tileWidth, tileHeight);
                obstacleList.add(new Obstacles.ClothTable(context, tileWidth * 5, tileHeight * 2, 3, 7));
                obstacleList.add(new Obstacles.WoodTable(context, tileWidth * 12, tileHeight * 7, 6, 3, false));
                m =  new Enemies.Mummy(context, tileWidth * 2, tileHeight * 9, 3, 3, 1, 0);
                m.setPath(GameActivity.tileWidth * 9);
                //m.setDirection(1, 0);
                enemyList.add(m);
                lightList.add(player.getSelfLight());
                for(Obstacles o : obstacleList) {
                    if(o.hasLight())
                        lightList.add(o.getLight());
                }
                break;
            case 3:
                screenMinX = tileWidth;
                screenMinY = tileHeight * 2;
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_full_2);
                background = Bitmap.createScaledBitmap(temp, screenMaxX, screenMaxY, true);
                player = new Player(context, tileWidth * 2, tileHeight * 9, screenMinX, screenMinY, screenMaxX, screenMaxY, (int)(tileWidth * 2.5), (int)(tileHeight * 2.5));
                player.setDirection(1, 0);
                door = new Interactables.Door(context, tileWidth * 5, 0, tileWidth * 2, tileHeight * 2, true);
                key = new Interactables.Key(context, tileWidth * 18, tileHeight * 2, tileWidth, tileHeight);
                obstacleList.add(new Obstacles.WoodTable(context, tileWidth * 4, tileHeight * 5, 9, 4, false));
                obstacleList.add(new Obstacles.Box(context, tileWidth * 16, tileHeight * 4, 3, 2));
                m =  new Enemies.Mummy(context, tileWidth * 2, tileHeight * 2, 3, 3, 1, 0);
                m.setPath(GameActivity.tileWidth * 9);
                enemyList.add(m);
                m =  new Enemies.Mummy(context, tileWidth * 13, tileHeight * 2, 3, 3, 0, 1);
                m.setPath(GameActivity.tileHeight * 7);
                enemyList.add(m);
                lightList.add(player.getSelfLight());
                for(Obstacles o : obstacleList) {
                    if(o.hasLight())
                        lightList.add(o.getLight());
                }
                l = new Lights((int)(tileWidth * 12.5), (int)(tileHeight * 5.5), GameActivity.tileWidth * 2);
                lightList.add(l);
                break;
            case 4:
                screenMinX = tileWidth * 2;
                screenMinY = 0;
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_full);
                background = Bitmap.createScaledBitmap(temp, screenMaxX, screenMaxY, true);
                //Context, startX, startY, screenX min, screenY min, screenX max, screenY max, width of player, width of height
                player = new Player(context, tileWidth * 18, tileHeight * 8, screenMinX, screenMinY, screenMaxX, screenMaxY, (int)(tileWidth * 2.5), (int)(tileHeight * 2.5));
                player.setDirection(-1, 0);
                //Create our interactables
                door = new Interactables.Door(context, 0, tileHeight * 8, tileWidth * 2, tileHeight * 2, false);
                key = new Interactables.Key(context, tileWidth * 9, 0, tileWidth, tileHeight);
                //Create our obstacles
                obstacleList.add(new Obstacles.Box(context, tileWidth * 3, tileHeight, 3, 2));
                obstacleList.add(new Obstacles.WoodTable(context, tileWidth * 4, 0, 5, 5, false));
                obstacleList.add(new Obstacles.WoodTable(context, tileWidth * 11, 0, 3, 5, true));
                obstacleList.add(new Obstacles.ClothTable(context, tileWidth * 7, tileHeight * 8, 3, 3));
                obstacleList.add(new Obstacles.LoungeChair(context, tileWidth * 16, tileHeight * 4, 4, 4));
                //Create our enemies
                g = new Enemies.Ghost(context, tileWidth * 11, tileHeight * 4, 3, 3, 1, 0, tileWidth * 4);
                enemyList.add(g);
                //Create our lights
                lightList.add(player.getSelfLight());
                for(Obstacles o : obstacleList) {
                    if(o.hasLight())
                        lightList.add(o.getLight());
                }
                l = new Lights((int)(tileWidth * 8.5), (int)(tileHeight * 0.5), GameActivity.tileWidth * 2);
                lightList.add(l);
                break;
            case 5:
                screenMinX = tileWidth;
                screenMinY = tileHeight * 2;
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_full_2);
                background = Bitmap.createScaledBitmap(temp, screenMaxX, screenMaxY, true);
                player = new Player(context, tileWidth * 17, tileHeight * 9, screenMinX, screenMinY, screenMaxX, screenMaxY, (int)(tileWidth * 2.5), (int)(tileHeight * 2.5));
                player.setDirection(-0.3, -0.7);
                door = new Interactables.Door(context, tileWidth * 2, 0, tileWidth * 2, tileHeight * 2, true);
                key = new Interactables.Key(context, tileWidth * 2, tileHeight * 10, tileWidth, tileHeight);
                obstacleList.add(new Obstacles.WoodTable(context, tileWidth * 8, tileHeight * 2, 3, 3, false));
                obstacleList.add(new Obstacles.WoodTable(context, tileWidth * 2, tileHeight * 8, 3, 2, false));
                obstacleList.add(new Obstacles.ClothTable(context, tileWidth * 12, tileHeight * 5, 3, 7));
                g = new Enemies.Ghost(context, tileWidth * 5, tileHeight * 4, 3, 3, 1, 0, tileWidth * 2);
                enemyList.add(g);
                g = new Enemies.Ghost(context, tileWidth * 15, tileHeight * 5, 3, 3, -1, 0, tileWidth * 3);
                enemyList.add(g);
                lightList.add(player.getSelfLight());
                for(Obstacles o : obstacleList) {
                    if(o.hasLight())
                        lightList.add(o.getLight());
                }
                break;
            case 6:
                screenMinX = tileWidth * 2;
                screenMinY = tileWidth * 2;
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_full);
                background = Bitmap.createScaledBitmap(temp, screenMaxX, screenMaxY, true);
                player = new Player(context, tileWidth * 2, tileHeight * 9, screenMinX, screenMinY, screenMaxX, screenMaxY, (int)(tileWidth * 2.5), (int)(tileHeight * 2.5));
                player.setDirection(1, 0);
                door = new Interactables.Door(context, 0, tileHeight * 3, tileWidth * 2, tileHeight * 2, false);
                key = new Interactables.Key(context, tileWidth * 18, tileHeight * 4, tileWidth, tileHeight);
                obstacleList.add(new Obstacles.WoodTable(context, tileWidth * 5, tileHeight * 2, 3, 6, false));
                obstacleList.add(new Obstacles.WoodTable(context, tileWidth * 10, tileHeight * 7, 3, 3, false));
                obstacleList.add(new Obstacles.WoodTable(context, tileWidth * 16, tileHeight * 9, 4, 3, false));
                m =  new Enemies.Mummy(context, tileWidth * 13, (int)(tileHeight * 9.5), 3, 3, 0, -1);
                m.setPath(GameActivity.tileWidth * 4);
                enemyList.add(m);
                g = new Enemies.Ghost(context, tileWidth * 14, tileHeight * 3, 3, 3, -1, 0, tileWidth * 3);
                enemyList.add(g);
                g = new Enemies.Ghost(context, tileWidth * 11, tileHeight * 7, 3, 3, 1, 0, tileWidth * 3);
                enemyList.add(g);
                lightList.add(player.getSelfLight());
                break;
            case 7:
                screenMinX = tileWidth * 2;
                screenMinY = tileWidth * 2;
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_full);
                background = Bitmap.createScaledBitmap(temp, screenMaxX, screenMaxY, true);
                door = new Interactables.Door(context, tileWidth * 4, 0, tileWidth * 2, tileHeight * 2, true);
                key = new Interactables.Key(context, tileWidth * 3, tileHeight * 10, tileWidth, tileHeight);
                player = new Player(context, tileWidth * 17, tileHeight * 9, screenMinX, screenMinY, screenMaxX, screenMaxY, (int)(tileWidth * 2.5), (int)(tileHeight * 2.5));
                player.setDirection(-1, 0);
                obstacleList.add(new Obstacles.WoodTable(context, tileWidth * 6, tileHeight * 5, 10, 5, false));
                z = new Enemies.Zombie(context, tileWidth * 5, tileHeight * 9, 3, 3, 1, 0);
                enemyList.add(z);
                lightList.add(player.getSelfLight());
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
