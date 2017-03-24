package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import group7.scavengerhaunt.Interactables;

/**
 * Created by Kevin on 3/23/2017.
 */

public class Door extends Interactables {

    public Door(Context context, int x, int y) {
        super(context, x, y);
        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.how_to_play);
        hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
    }

}
