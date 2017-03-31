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

    //Lights attached to obstacle, if any
    protected boolean hasLight = false;
    protected Lights light;

    public Obstacles(Context context, int x, int y) {
        this.x = x;
        this.y = y;
        //hasLight = false;
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

    public boolean hasLight() {
        return hasLight;
    }

    public Lights getLight() {
        return light;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static class Table extends Obstacles {
        //Hardcoded to be one tile shorter (-y) than normal
        public Table(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.table);
            image = Bitmap.createScaledBitmap(temp, GameView.tileWidth * scaleX, GameView.tileHeight * scaleY, true);
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight() - GameView.tileHeight);
            hasLight = true;
            //Messy, hardcoded values
            light = new Lights(x + image.getWidth() * 44 / 100, y + image.getHeight() * 3 /10, GameView.tileWidth * 2);
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

    //The fireplace is a triangle, not a rectangle hitbox
    public static class Fireplace extends Obstacles {
        private float[] p1 = new float[2];
        private float[] p2 = new float[2];
        private float[] p3 = new float[2];

        //Right now we're hardcoding the hitbox to be tile wider (to the left) and lower than normal
        public Fireplace(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.fireplace_complete);
            image = Bitmap.createScaledBitmap(temp, GameView.tileWidth * scaleX, GameView.tileHeight * scaleY, true);
            p1[0] = (float)x - GameView.tileWidth;
            p1[1] = (float)y;
            p2[0] = (float)(x + image.getWidth());
            p2[1] = (float)y;
            p3[0] = (float)(x + image.getWidth());
            p3[1] = (float)(y + image.getHeight() + GameView.tileHeight);
            hasLight = true;
            light = new Lights(x + image.getWidth() * 3 / 5, y + image.getHeight() / 3, GameView.tileWidth * (scaleX * 2) / 3);
        }

        //Calculating Barycentric coordinates (flashbacks to computer graphics)
        public boolean detectCollision(int playerX, int playerY) {
            float pX = (float)playerX;
            float pY = (float)playerY;

            float alpha = ((p2[1] - p3[1]) * (pX - p3[0])
                    + (p3[0] - p2[0]) * (pY - p3[1]))
                    / ((p2[1] - p3[1]) * (p1[0] - p3[0])
                    + (p3[0] - p2[0]) * (p1[1] - p3[1]));
            float beta = ((p3[1] - p1[1]) * (pX - p3[0])
                    + (p1[0] - p3[0]) * (pY - p3[1]))
                    / ((p2[1] - p3[1])  * (p1[0] - p3[0])
                    + (p3[0] - p2[0]) * (p1[1] - p3[1]));
            float gamma = 1.0f - alpha - beta;
            return alpha > 0 && beta > 0 && gamma > 0;
        }
    }
}
