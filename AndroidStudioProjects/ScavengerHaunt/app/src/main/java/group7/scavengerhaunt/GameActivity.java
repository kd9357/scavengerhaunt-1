package group7.scavengerhaunt;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "Game Interface";
    private int SETTINGS_REQUEST = 1;

    private int level;

    //Surface view of game
    private GameView gameView;

    private ImageButton buttonSettingsGear;
    //Is the game over or not?
    private boolean mGameOver;

    //TODO: add variables for gameview, hud, enemy type/locations, etc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Bundle b = getIntent().getExtras();
        level = -1;
        if(b != null)
            level = b.getInt("level");

        //Display object
        Display display = getWindowManager().getDefaultDisplay();
        //Get screen resolution
        Point size = new Point();
        display.getSize(size);
        //SurfaceView
        gameView = (GameView) findViewById(R.id.game_view);
        //Eventually, initialize will have more parameters for spawn locations etc
        gameView.initialize(this, size.x, size.y);

        buttonSettingsGear = (ImageButton) findViewById(R.id.buttonSettingsGear);
        buttonSettingsGear.setOnClickListener(this);

        setInstanceVarsFromSharedPrefs();
    }

    // Dialog fragment for when user wins / loses
    protected void handleEndGame() {
        FragmentManager fm = getFragmentManager();
        EndGameFragment f = new EndGameFragment();
        f.show(fm, "end");
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