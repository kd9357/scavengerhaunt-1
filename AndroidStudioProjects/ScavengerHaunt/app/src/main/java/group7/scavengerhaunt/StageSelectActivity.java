package group7.scavengerhaunt;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class StageSelectActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton buttonStage;
    private StageView mStageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_select);

        //Getting the button
        buttonStage = (ImageButton) findViewById(R.id.stage_select_button);

        //Use View/canvas instead?
        mStageView = (StageView) findViewById(R.id.stageView);
        //Grab save data
        int[] temp = {1, 1, 0, 0, 0};
        mStageView.setUnlockedStages(temp);
        mStageView.setOnTouchListener(mTouchListener);

        //adding a click listener
        buttonStage.setOnClickListener(this);

        //TODO:Refactor and allow code to programmatically add locked and unlocked stages
        //Maybe make this a BoardView (from tutorial) instead?
    }

    // Listen for touches on the board. Only apply move if game not over.
    //TODO: Calculate which item touched given xy coordinates
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            //Set location, startActivity if unlocked

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    //If using imageButtons
    @Override
    public void onClick(View v) {
        //Start game activity
        if(v==buttonStage) {
            //Should tell game activity character spawn point, obstacles, other info
            //startActivity(new Intent(this, GameActivity.class));
        }
    }
}
