package group7.scavengerhaunt;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class StageSelectActivity extends AppCompatActivity implements View.OnTouchListener{

    //private ImageButton buttonStage;
    private StageView mStageView;

    //Must persist on application close
    private int[] mStages = {1, 1, 0, 0, 0, 0, 0, 0};

    private int xStart;
    private int yStart;
    private int buttonSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_select);

        //Using Canvas to display buttons
        mStageView = (StageView) findViewById(R.id.stageView);
        //Shouldn't hard code this!
        buttonSize = mStageView.getWidth() / 5;
        xStart = dpToPx(16);
        yStart = dpToPx(16 * 2) + spToPx(50);
        //Grab saved data here
        mStageView.setStages(mStages);
        mStageView.setOnTouchListener(this);
    }

    public boolean onTouch(View v, MotionEvent event) {
        //TODO: Calculate which item touched given xy coordinates
        //Right now just touching the screen starts a default game
        startActivity(new Intent(this, GameActivity.class));
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
