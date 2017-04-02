package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

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

    public void drawObstacle(Canvas canvas, Paint paint) {
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
        List<Rect> hitBoxes = new ArrayList<>();
        public Table(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            //Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.table);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.table_full);
            image = Bitmap.createScaledBitmap(temp, GameView.tileWidth * scaleX, GameView.tileHeight * scaleY, true);
            //hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight() - GameView.tileHeight);
            //hitBoxes.add(new Rect(x, y, x + image.getWidth(), y + image.getHeight() - GameView.tileHeight));
            hitBoxes.add(new Rect(x + image.getWidth() / 7, y + image.getHeight() / 6, x + 6 *image.getWidth() / 7, y + 3 * image.getHeight() / 4));
            hitBoxes.add(new Rect(x, y + image.getHeight() / 6, x + image.getWidth() / 6, y + image.getHeight()/2 ));
            hitBoxes.add(new Rect(x + image.getWidth()/ 3, y + 2*image.getHeight()/3, x + 4 *image.getWidth()/7, y + 5 *image.getHeight() / 6));
            hitBoxes.add(new Rect(x + 5 *image.getWidth()/6, y + image.getHeight() / 6, x + image.getWidth(), y + 4 * image.getHeight() / 7));
            hasLight = true;
            //Messy, hardcoded values
            light = new Lights(x + image.getWidth() * 47 / 100, y + image.getHeight() * 35 /100, GameView.tileWidth * 2);
        }

        public boolean detectCollision(int playerX, int playerY) {
            for (Rect hitBox : hitBoxes) {
                if (playerX >= hitBox.left && playerX <= hitBox.right && playerY >= hitBox.top && playerY <= hitBox.bottom)
                    return true;
            }
            return false;
        }

        public void drawHitBox(Canvas canvas, Paint paint) {
            for(Rect hitBox : hitBoxes) {
                canvas.drawRect(hitBox, paint);
            }
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

    public static class Box extends Obstacles {
        public Box(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.boxes);
            image = Bitmap.createScaledBitmap(temp, GameView.tileWidth * scaleX, GameView.tileHeight * scaleY, true);
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
        }
    }

    public static class SeatOne extends Obstacles {
        public SeatOne(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.seat_1);
            image = Bitmap.createScaledBitmap(temp, GameView.tileWidth * scaleX, GameView.tileHeight * scaleY, true);
            hitBox = new Rect(x, y + image.getHeight() / 2, x + image.getWidth(), y + image.getHeight());
        }
    }

    public static class SeatTwo extends Obstacles {
        public SeatTwo(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.seat_2);
            image = Bitmap.createScaledBitmap(temp, GameView.tileWidth * scaleX, GameView.tileHeight * scaleY, true);
            hitBox = new Rect(x, y, x + 5 * image.getWidth() / 4, y + 5 * image.getHeight() / 4);
        }
    }

    public static class SeatThree extends Obstacles {
        public SeatThree(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.seat_3);
            image = Bitmap.createScaledBitmap(temp, (int)(GameView.tileWidth * (scaleX + 0.5)), (int)(GameView.tileHeight * (scaleY + 0.6)), true);
            hitBox = new Rect(x, y, x + image.getWidth(), y + 3 * image.getHeight() / 5);
        }
    }

    public static class SeatFour extends Obstacles {
        public SeatFour(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.seat_4);
            image = Bitmap.createScaledBitmap(temp, GameView.tileWidth * scaleX, (int)(GameView.tileHeight * (scaleY + 0.5)), true);
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
            hitBox = new Rect();
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
