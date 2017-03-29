package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by Kevin on 3/28/2017.
 */

public class Obstacles {
    protected Rect hitBox;
    protected Bitmap image;
    //Coordinates
    protected int x;
    protected int y;

    public Obstacles(Context context, int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean detectCollision(int playerX, int playerY) {
        return playerX >= hitBox.left && playerX <= hitBox.right && playerY >= hitBox.top && playerY <= hitBox.bottom;
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

    public static class Table extends Obstacles {
        public Table(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.table);
            image = Bitmap.createScaledBitmap(temp, GameView.tileWidth * scaleX, GameView.tileHeight * scaleY, true);
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight() - GameView.tileHeight);
        }
    }

    public static class LoungeChair extends Obstacles {
        public LoungeChair(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.lounge_chair);
            image = Bitmap.createScaledBitmap(temp, GameView.tileWidth * scaleX, GameView.tileHeight * scaleY, true);
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
        }
    }
}
