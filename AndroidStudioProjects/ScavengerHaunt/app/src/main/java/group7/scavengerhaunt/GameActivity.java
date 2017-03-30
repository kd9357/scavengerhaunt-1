package group7.scavengerhaunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "Game Interface";
    private int SETTINGS_REQUEST = 1;

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

        //Display object
        Display display = getWindowManager().getDefaultDisplay();
        //Get screen resolution
        Point size = new Point();
        display.getSize(size);
        //SurfaceView
        gameView = (GameView) findViewById(R.id.game_view);
        //Eventually, initialize will have more parameters for spawn locations etc
        gameView.initialize(size.x, size.y);

        buttonSettingsGear = (ImageButton) findViewById(R.id.buttonSettingsGear);
        buttonSettingsGear.setOnClickListener(this);

        setInstanceVarsFromSharedPrefs();
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
            SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
            MainActivity.mSoundOn = sharedPref.getBoolean("sound", true);
        }
    }

    private void setInstanceVarsFromSharedPrefs() {
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        MainActivity.mSoundOn = sharedPref.getBoolean("sound", true);
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
}