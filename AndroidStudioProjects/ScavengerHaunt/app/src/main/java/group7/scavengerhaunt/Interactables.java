package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by Kevin on 3/23/2017.
 */

public class Interactables {
    protected Rect hitBox;
    protected Rect imageBox;
    protected Bitmap image;
    //Coordinates
    protected int x;
    protected int y;

    protected boolean illuminated = false;

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

    public void setIlluminated(boolean b) {
        illuminated = b;
    }

    public boolean isIlluminated() {
        return illuminated;
    }

    public Bitmap getImage(){
        return image;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public Rect getImageBox() {
        return imageBox;
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

        public Door(Context context, int x, int y, int tileWidth, int tileHeight, boolean vertical) {
            super(context, x, y);
            Bitmap temp;
            if(vertical) {
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.door_2);
                image = Bitmap.createScaledBitmap(temp, (int)(tileWidth * 1.2), tileHeight, true);
                imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
                hitBox = new Rect(x, y + image.getHeight(), x  + image.getWidth(), y + (int)(image.getHeight() * 1.6));
            }
            else {
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.door);
                image = Bitmap.createScaledBitmap(temp, tileWidth, tileHeight * 2, true);
                imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
                hitBox = new Rect(x + 2 * image.getWidth() / 3, y + image.getHeight()/3, x  + (int)(1.5 * image.getWidth()), y + image.getHeight());
            }
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
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x + image.getWidth()/4, y, x + 3 *image.getWidth()/4, y + image.getHeight());
        }
    }

    public static class Battery extends Interactables {
        private Bitmap full;
        private Bitmap eighty;
        private Bitmap sixty;
        private Bitmap forty;
        private Bitmap twenty;
        private Bitmap empty;
        private float percentage;

        public Battery(Context context, float charge, int x, int y, int tileWidth, int tileHeight) {
            super(context, x, y);
            percentage = charge;
            Log.d("In battery", x + ", " + y + ", " + tileWidth + ", " + tileHeight);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.battery_full);
            full = Bitmap.createScaledBitmap(temp, tileWidth, tileHeight, true);
            temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.battery_eighty);
            eighty = Bitmap.createScaledBitmap(temp, tileWidth, tileHeight, true);
            temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.battery_sixty_green);
            sixty = Bitmap.createScaledBitmap(temp, tileWidth, tileHeight, true);
            temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.battery_forty);
            forty = Bitmap.createScaledBitmap(temp, tileWidth, tileHeight, true);
            temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.battery_twenty);
            twenty = Bitmap.createScaledBitmap(temp, tileWidth, tileHeight, true);
            temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.battery_empty);
            empty = Bitmap.createScaledBitmap(temp, tileWidth, tileHeight, true);
        }

        public void drawInteractable(Canvas canvas, Paint paint) {
            if(percentage > 0.8f)
                canvas.drawBitmap(full, getX(), getY(), paint);
            else if(percentage <= 0.8f && percentage > 0.65f)
                canvas.drawBitmap(eighty, getX(), getY(), paint);
            else if(percentage <= 0.65f && percentage > 0.5f)
                canvas.drawBitmap(sixty, getX(), getY(), paint);
            else if(percentage <= 0.5f && percentage > 0.3f)
                canvas.drawBitmap(forty, getX(), getY(), paint);
            else if(percentage <= 0.3f && percentage > 0.05f)
                canvas.drawBitmap(twenty, getX(), getY(), paint);
            else
                canvas.drawBitmap(empty, getX(), getY(), paint);
        }

        public void setPercentage(float percentage) {
            this.percentage = percentage;
        }
    }
}
