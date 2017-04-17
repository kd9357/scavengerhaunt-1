package group7.scavengerhaunt;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class StageSelectActivity extends AppCompatActivity implements View.OnTouchListener{

    //private ImageButton buttonStage;
    private StageView mStageView;

    //Must persist on application close
    private int[] mStages = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

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
        if(xCoord > 0 && xCoord < buttonSize && yCoord > 0 && yCoord < buttonSize) {
            Intent intent = new Intent(this, GameActivity.class);
            //intent.putExtras("level", 1); //Will determine which level gameActivity should load
            startActivity(intent);
        }

        //so we aren't notified of continued events when finger is moved
        return false;
    }

    //Used to calculate screen location
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int spToPx(int sp)
    {
        return (int) (sp * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }
}
