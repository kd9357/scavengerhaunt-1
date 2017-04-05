package group7.scavengerhaunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static SoundPool mSounds;
    public static int mButtonPressID;

    private ImageButton buttonStageSelect;
    private ImageButton buttonHowToPlay;
    private ImageButton buttonSettings;

    private int SETTINGS_REQUEST = 1;

    public static boolean mSoundOn = true;
    public static boolean mDebugModeOn = false;
    public static boolean mSoftShadowsOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting the orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //Getting the button
        buttonStageSelect = (ImageButton) findViewById(R.id.buttonStage);
        buttonHowToPlay = (ImageButton) findViewById(R.id.buttonHowToPlay);
        buttonSettings = (ImageButton) findViewById(R.id.buttonSettings);

        //adding a click listener
        buttonStageSelect.setOnClickListener(this);
        buttonHowToPlay.setOnClickListener(this);
        buttonSettings.setOnClickListener(this);

        //TODO:Call function to grab sharedPref and restore info
        setInstanceVarsFromSharedPrefs();
    }

    @Override
    public void onClick(View v) {
        //Go to stage select screen
        if(v==buttonStageSelect) {
            if(mSoundOn) {
                //MainActivity.mSounds.play(MainActivity.mButtonPressID, 1, 1, 1, 0, 1);
            }
            startActivity(new Intent(this, StageSelectActivity.class));
        }//Go to how to play screen
        else if(v==buttonHowToPlay) {
            startActivity(new Intent(this, HowToPlayActivity.class));
        }//Go to settings screen
        else if(v==buttonSettings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, SETTINGS_REQUEST);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSounds = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        mButtonPressID = mSounds.load(this, R.raw.sms_alert_1_daniel_simon, 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSounds != null) {
            mSounds.release();
            mSounds = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.put<type>(key, value)
        //...
        //super.onSaveInstanceState(outState);
        //Save game information, maybe put this into GameActivity?
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //Reload data from savedInstanceState.get<type>(key)
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //put data into editor, then editor.apply
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Called when Settings has been exited
        if(requestCode == SETTINGS_REQUEST) {
            SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
            mSoundOn = sharedPref.getBoolean("sound", true);
            mSoftShadowsOn = sharedPref.getBoolean("shadows", true);
            mDebugModeOn = sharedPref.getBoolean("debug", false);
        }
    }

    private void setInstanceVarsFromSharedPrefs() {
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        mSoundOn = sharedPref.getBoolean("sound", true);
        mSoftShadowsOn = sharedPref.getBoolean("shadows", true);
        mDebugModeOn = sharedPref.getBoolean("debug", false);
    }


}
