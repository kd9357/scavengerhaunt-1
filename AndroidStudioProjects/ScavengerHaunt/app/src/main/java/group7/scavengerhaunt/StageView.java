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

    //0: locked, 1 unlocked
    private int[] mStages;

    public StageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public void setStages(int[] stages) {
        mStages = stages;
    }

    public void initialize() {
        mLockedStage = BitmapFactory.decodeResource(getResources(), R.drawable.stage_icon_locked_placeholder);
        mUnlockedStage = BitmapFactory.decodeResource(getResources(), R.drawable.stage_icon_placeholder);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public int getStageWidth() {
        return getWidth();
    }

    public int getStageHeight() {
        return getHeight();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int buttonSize = getWidth() / 5;
        Rect drawingRect = new Rect();
        //Draw on button locations
        //TODO: Actually calculate button locations
        //As it stands now the buttons are not in the right position/scale
        int r = 0;
        int c = 0;
        for(int i = 0; i < mStages.length; i++) {
            drawingRect.left = c;
            drawingRect.top = r;
            drawingRect.right = c + buttonSize;
            drawingRect.bottom = r + buttonSize;
            //Draw Locked Stage
            if(mStages[i] == 0) {
                canvas.drawBitmap(mLockedStage, null, drawingRect, mPaint);
            }
            //Draw Unlocked Stage
            else {
                //Should also draw i + 1 over the unlocked stage bitmap
                canvas.drawBitmap(mUnlockedStage, null, drawingRect, mPaint);
            }
            c += buttonSize;
            if(c > getWidth()) {
                c = 0;
                r += buttonSize;
                //Unlikely to happen, but possibly add pagination for more stages
            }
        }
    }

}
