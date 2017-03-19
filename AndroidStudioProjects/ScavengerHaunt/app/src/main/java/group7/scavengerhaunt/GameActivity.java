package group7.scavengerhaunt;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "Game Interface";
    //Represents the internal state of the game
    private Game mGame;

    //Surface view of game
    private GameView gameView;

    //Is the game over or not?
    private boolean mGameOver;

    //TODO: add variables for gameview, hud, etc
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
        //setContentView(R.layout.activity_game);
    }

    //Pause activity
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    //Resume activity
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}
