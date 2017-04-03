package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Kevin on 3/23/2017.
 */

public class Interactables {
    protected Rect hitBox;
    protected Bitmap image;
    //Coordinates
    protected int x;
    protected int y;

    public Interactables(Context context, int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void drawInteractable(Canvas canvas, Paint paint) {
        canvas.drawBitmap(getImage(), getX(), getY(), paint);
    }

    public void drawHitBox(Canvas canvas, Paint paint) {
        canvas.drawRect(hitBox, paint);
    }

    public Bitmap getImage(){
        return image;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean detectCollision(int playerX, int playerY) {
        return playerX >= hitBox.left && playerX <= hitBox.right && playerY >= hitBox.top && playerY <= hitBox.bottom;
    }

    /**
     * Created by Kevin on 3/23/2017.
     */

    public static class Door extends Interactables {

        public Door(Context context, int x, int y, int tileWidth, int tileHeight) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.door);
            image = Bitmap.createScaledBitmap(temp, tileWidth, tileHeight * 2, true);
            hitBox = new Rect(x + 2 * image.getWidth() / 3, y + image.getHeight()/3, x  + (int)(1.5 * image.getWidth()), y + image.getHeight());
        }

    }

    /**
     * Created by Kevin on 3/23/2017.
     */

    public static class Key extends Interactables {

        public Key(Context context, int x, int y, int tileWidth, int tileHeight) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.key);
            image = Bitmap.createScaledBitmap(temp, tileWidth, tileHeight, true);
            hitBox = new Rect(x + image.getWidth()/4, y, x + 3 *image.getWidth()/4, y + image.getHeight());
        }
    }
}
