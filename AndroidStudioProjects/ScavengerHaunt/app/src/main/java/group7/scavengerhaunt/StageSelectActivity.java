package group7.scavengerhaunt;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class StageSelectActivity extends AppCompatActivity implements View.OnTouchListener{

    //private ImageButton buttonStage;
    private StageView mStageView;

    //Must persist on application close
    public static int[] mStages = new int[10];

    private int buttonSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_select);

        restoreStages();
        //Using Canvas to display buttons
        mStageView = (StageView) findViewById(R.id.stageView);
        //Grab saved data here
        mStageView.setStages(mStages);
        mStageView.setOnTouchListener(this);
    }


    public boolean onTouch(View v, MotionEvent event) {
        buttonSize = mStageView.getWidth() / 5;
        int xCoord = (int)event.getX();
        int yCoord = (int)event.getY();
        if(xCoord > 0 && xCoord < mStageView.getWidth() && yCoord > 0 && yCoord < mStageView.getHeight()) {
            int stageNum = xCoord / buttonSize;
            if(yCoord / buttonSize == 1)
                stageNum+=5;
            if(mStages[stageNum] == 1) {
                if(MainActivity.mSoundOn) {
                    MediaPlayer player = MediaPlayer.create(this, R.raw.button_pressed);
                    player.start();
                }
                Intent intent = new Intent(this, GameActivity.class);
                intent.putExtra("level", stageNum);
                startActivity(intent);
            }
        }

        //so we aren't notified of continued events when finger is moved
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putIntArray("mStages", StageSelectActivity.mStages);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        for (int i = 1; i < mStages.length; i++) {
            editor.putInt("stage" + i, mStages[i]);
        }
        editor.apply();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreStages();
        //StageSelectActivity.mStages = savedInstanceState.getIntArray("mStages");
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        for (int i = 1; i < mStages.length; i++) {
            editor.putInt("stage" + i, mStages[i]);
        }
        editor.apply();
    }


    private void restoreStages() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mStages[0] = 1;
        for(int i = 1; i < mStages.length; i++) {
            mStages[i] = sharedPref.getInt("stage" + i, 0);
        }
    }
}
