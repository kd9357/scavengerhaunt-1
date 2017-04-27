package group7.scavengerhaunt;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "Game Interface";
    private int SETTINGS_REQUEST = 1;

    private int level;

    public static final int NUM_COLUMNS = 20;
    public static final int NUM_ROWS = 12;

    public static int tileWidth;
    public static int tileHeight;

    //Surface view of game
    private GameView gameView;

    private ImageButton buttonSettingsGear;
    //Is the game over or not?
    private boolean mGameOver;
    public FragmentManager fm;

    //TODO: add variables for gameview, hud, enemy type/locations, etc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Bundle b = getIntent().getExtras();
        level = -1;
        if(b != null)
            level = b.getInt("level");

        //Just to make sure it's unlocked
        StageSelectActivity.mStages[level] = 1;

        //Display object, get screen size
        Display display = getWindowManager().getDefaultDisplay();
        //Get screen resolution
        Point size = new Point();
        display.getSize(size);
        tileWidth = size.x / GameActivity.NUM_COLUMNS;
        tileHeight = size.y / GameActivity.NUM_ROWS;

        //Initialize game objects
        Levels stage = new Levels(this, size.x, size.y);
        stage.setStage(this, level);

        //Initialize View
        gameView = (GameView) findViewById(R.id.game_view);
        //Load gameobjects onto view
        //gameView.initialize(this, size.x, size.y);
        gameView.initialize(this, stage);

        buttonSettingsGear = (ImageButton) findViewById(R.id.buttonSettingsGear);
        buttonSettingsGear.setOnClickListener(this);

        fm = getFragmentManager();

        setInstanceVarsFromSharedPrefs();
    }

    // Dialog fragment for when user wins / loses
    protected void handleEndGame() {
        FragmentManager fm = getFragmentManager();
        EndGameFragment f = new EndGameFragment();
        f.show(fm, "end");
    }

    //Set next stage to be unlocked, and return next stage
    public int unlockStage(boolean won) {
        if(level + 1 < StageSelectActivity.mStages.length && won) {
            StageSelectActivity.mStages[level + 1] = 1;
            return level + 1;
        }
        return -1;
    }

    //Pause activity
    @Override
    protected void onPause() {
        //TODO: Save data
        //Evidently turning the screen off is not the same as pausing
        super.onPause();
        gameView.pause();
    }

    //Resume activity
    @Override
    protected void onResume() {
        //TODO: Restore data
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameView.pause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Called when Settings has been exited
        if(requestCode == SETTINGS_REQUEST) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            MainActivity.mSoundOn = sharedPref.getBoolean("sound", true);
            MainActivity.mSoftShadowsOn = sharedPref.getBoolean("shadows", true);
            MainActivity.mDebugModeOn = sharedPref.getBoolean("debug", false);
        }
    }

    private void setInstanceVarsFromSharedPrefs() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        MainActivity.mSoundOn = sharedPref.getBoolean("sound", true);
        MainActivity.mSoftShadowsOn = sharedPref.getBoolean("shadows", true);
        MainActivity.mDebugModeOn = sharedPref.getBoolean("debug", false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        Player player = gameView.getPlayer();
        ArrayList<Enemies> enemyList = (ArrayList)gameView.getEnemyList();
        //Metadata
        outState.putIntArray("mStages", StageSelectActivity.mStages);

        outState.putBoolean("gameWon", gameView.hasWon());
        outState.putBoolean("gameFinished", gameView.hasFinished());
        //Player information
        outState.putBoolean("hasKey", player.hasKey());
        outState.putDoubleArray("direction", player.getDirection());
        outState.putIntArray("position", new int[]{player.getX(), player.getY()});
        outState.putFloat("battery", player.getCharge());
        //Enemy information
        outState.putParcelableArrayList("enemies", enemyList);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        StageSelectActivity.mStages = savedInstanceState.getIntArray("mStages");
        boolean playerDead = (!savedInstanceState.getBoolean("gameWon") && savedInstanceState.getBoolean("gameFinished"));
        if (!playerDead) {
            boolean hasKey = savedInstanceState.getBoolean("hasKey");
            double[] direction = savedInstanceState.getDoubleArray("direction");
            int[] position = savedInstanceState.getIntArray("position");
            float battery = savedInstanceState.getFloat("battery");
            Player player = gameView.getPlayer();
            if (hasKey)
                player.foundKey();
            player.setDirection(direction[0], direction[1]);
            player.newLocation(position[0], position[1]);
            player.setCharge(battery);
            gameView.setPlayer(player);
        }
    }

    @Override
    public void onClick(View v) {
        //Go to settings screen
        if(v==buttonSettingsGear) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, SETTINGS_REQUEST);
        }
    }

    //Given a direction vector, find the heading in degrees in relation to north
    public static double getAngle(double ux, double uy) {
        return Math.toDegrees(Math.acos(-uy));
    }

    public static double calculateDistance(int startX, int startY, int destX, int destY) {
        return Math.sqrt(Math.pow((destX - startX), 2) + Math.pow((destY - startY), 2));
    }
}