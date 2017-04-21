package group7.scavengerhaunt;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class StageSelectActivity extends AppCompatActivity implements View.OnTouchListener{

    //private ImageButton buttonStage;
    private StageView mStageView;

    //Must persist on application close
    private int[] mStages = {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};

    private int buttonSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_select);

        //Using Canvas to display buttons
        mStageView = (StageView) findViewById(R.id.stageView);
        //Grab saved data here
        mStageView.setStages(mStages);
        mStageView.setOnTouchListener(this);
    }

    public boolean onTouch(View v, MotionEvent event) {
        //TODO: Calculate which item touched given xy coordinates
        //Right now just touching the screen starts a default game
        buttonSize = mStageView.getWidth() / 5;
        int xCoord = (int)event.getX();
        int yCoord = (int)event.getY();
        if(xCoord > 0 && xCoord < mStageView.getWidth() && yCoord > 0 && yCoord < mStageView.getHeight()) {
            int stageNum = xCoord / buttonSize + yCoord / buttonSize;
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
}
