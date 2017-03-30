package group7.scavengerhaunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "Game Interface";
    private int SETTINGS_REQUEST = 1;

    //Surface view of game
    private GameView gameView;
    private int tileWidth;
    private int tileHeight;
    private int screenMaxX;
    private int screenMaxY;
    //Is the game over or not?
    private boolean mGameOver;

    //TODO: add variables for gameview, hud, enemy type/locations, etc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Display object
        Display display = getWindowManager().getDefaultDisplay();
        //Get screen resolution
        Point size = new Point();
        display.getSize(size);


        gameView = new GameView(this, size.x, size.y);
        setContentView(gameView);

        tileWidth = size.x / 20;
        tileHeight = size.y / 12;
        screenMaxX = size.x;
        screenMaxY = size.y;
        //setContentView(R.layout.activity_game);
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

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
//        if(motionEvent.getX() <= 1.5 * tileWidth && motionEvent.getY() >= screenMaxY - 1.5 * tileHeight) {
//            Intent intent = new Intent(this, SettingsActivity.class);
//            startActivityForResult(intent, SETTINGS_REQUEST);
//        }
        Log.d("Hello", "Pressed at " + motionEvent.getX() + ", " + motionEvent.getY());
        return true;
    }

    private void setInstanceVarsFromSharedPrefs() {
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        MainActivity.mSoundOn = sharedPref.getBoolean("sound", true);
    }

    //Given a direction vector, find the heading in degrees in relation to north
    public static double getAngle(double ux, double uy) {
        return Math.toDegrees(Math.acos(-uy));
    }
}