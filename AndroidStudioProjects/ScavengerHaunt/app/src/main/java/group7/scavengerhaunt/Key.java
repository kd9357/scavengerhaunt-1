package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by Kevin on 3/23/2017.
 */

public class Key extends Interactables {

    public Key(Context context, int x, int y) {
        super(context, x, y);
        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.stage_icon_locked_placeholder);
        hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
    }
}
