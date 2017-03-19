package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Kevin on 3/17/2017.
 * Use canvas to hopefully programmatically draw locked and unlocked stages
 */

public class StageView extends View {

    private Bitmap mLockedStage;
    private Bitmap mUnlockedStage;
    private Paint mPaint;
    //private Game mGame;
    //0: locked, 1 unlocked
    private int[] mUnlockedStages;

    public StageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    //Is this necessary?
//    public void setGame(Game game) {
//        mGame = game;
//    }

    public void setUnlockedStages(int[] unlockedStages) {
        mUnlockedStages = unlockedStages;
    }

    public void initialize() {
        mLockedStage = BitmapFactory.decodeResource(getResources(), R.drawable.stage_icon_locked_placeholder);
        mUnlockedStage = BitmapFactory.decodeResource(getResources(), R.drawable.stage_icon_placeholder);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int buttonSize = getWidth() / 5;
        Rect drawingRect = new Rect();
        //Draw on button locations
        //TODO: Actually calculate button locations
        //As it stands now the buttons are not in the right position/scale
        int index = 0;
        for(int row = 0; row < getHeight(); row += buttonSize) { //by column
            for(int column = 0; column < getWidth(); column += buttonSize) { //by row
                drawingRect.left = column;
                drawingRect.top = row;
                drawingRect.right = drawingRect.left + buttonSize;
                drawingRect.bottom = drawingRect.top + buttonSize;
                if(index < mUnlockedStages.length && mUnlockedStages[index] == 0) {
                    canvas.drawBitmap(mLockedStage, null, drawingRect, null);
                }
                else if(index < mUnlockedStages.length && mUnlockedStages[index] == 1) {
                    canvas.drawBitmap(mUnlockedStage, null, drawingRect, null);
                }
                index++;

            }
        }

    }

}
