package group7.scavengerhaunt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "Game Interface";
    //Represents the internal state of the game
    private Game mGame;

    //Is the game over or not?
    private boolean mGameOver;

    //TODO: add variables for gameview, hud, etc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }
}
