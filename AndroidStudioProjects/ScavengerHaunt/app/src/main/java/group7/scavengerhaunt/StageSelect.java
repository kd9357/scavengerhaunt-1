package group7.scavengerhaunt;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class StageSelect extends AppCompatActivity implements View.OnClickListener{

    private ImageButton buttonStage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_select);

        //Getting the button
        buttonStage = (ImageButton) findViewById(R.id.stage_select_button);

        //adding a click listener
        buttonStage.setOnClickListener(this);

        //TODO:Refactor and allow code to programmatically add locked and unlocked stages
        //Maybe make this a BoardView instead?
    }

    @Override
    public void onClick(View v) {
        //Start game activity
        if(v==buttonStage) {
            startActivity(new Intent(this, GameActivity.class));
        }
    }
}
