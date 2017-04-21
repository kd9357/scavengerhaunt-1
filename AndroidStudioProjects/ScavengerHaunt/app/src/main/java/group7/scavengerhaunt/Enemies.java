package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by Kevin on 4/1/2017.
 */

public class Enemies {
    //Image
    protected Bitmap image;
    protected int imageWidth;
    protected int imageHeight;

    //Coordinates
    //Top-left
    protected int x;
    protected int y;
    protected int centerX;
    protected int centerY;

    //Collision Detection
    protected Rect hitBox;
    protected Rect imageBox;

    //Movement mechanics
    protected int speed;
    protected boolean moving = false;

    protected double direction[];
    protected int patrolRoute;
    protected int distanceTraveled = 0;

    protected double angleDegrees = 0;

    protected boolean illuminated = false;

    public Enemies(Context context, int x, int y) {
        this.x = x;
        this.y = y;
        direction = new double[]{0, -1};
    }

//    public boolean detectCollision(int playerX, int playerY) {
//        return playerX >= hitBox.left && playerX <= hitBox.right && playerY >= hitBox.top && playerY <= hitBox.bottom;
//    }

    public boolean detectCollision(Rect playerHitBox) {
        return Rect.intersects(playerHitBox, this.hitBox);
    }

    public void drawEnemy(Canvas canvas, Paint paint) {
        canvas.drawBitmap(getImage(), getX(), getY(), paint);
    }

    public void drawHitBox(Canvas canvas, Paint paint) {
        canvas.drawRect(hitBox, paint);
    }

    public void update() {
        if(moving) {
            setX((int)(x + direction[0] * speed));
            setY((int)(y + direction[1] * speed));
            hitBox.offset(x, y);
            distanceTraveled += direction[0] * speed + direction[1] * speed;
        }
    }

    public void setX(int x) {
        this.x = x;
        centerX = x + imageWidth / 2;
    }

    public void setY(int y) {
        this.y = y;
        centerY = y + imageHeight / 2;
    }

    public void setDirection(double xVector, double yVector) {
        direction[0] = xVector;
        direction[1] = yVector;
        angleDegrees = (float) GameActivity.getAngle(direction[0], direction[1]);
        if(direction[0] < 0)
            angleDegrees = -angleDegrees;
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public double getAngleDegrees() {
        return angleDegrees;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public Rect getImageBox() {
        return imageBox;
    }

    public static class Zombie extends Enemies {
        private Rect verticalHitBox;
        private Rect horizontalHitBox;
        private boolean movingHorizontally;
        public Zombie (Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.zombie);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            this.imageWidth = image.getWidth();
            this.imageHeight = image.getHeight();
            centerX = x + image.getWidth()/2;
            centerY = y + image.getHeight()/2;
            //hitBox = new Rect(x + imageWidth / 5, y + imageHeight / 5, x+ 4 * imageWidth / 5, y+ 4 * imageHeight / 5);
            verticalHitBox = new Rect(x + imageWidth / 6, y + imageHeight / 5, x+ 5 * imageWidth / 6, y+ 4 * imageHeight / 5);
            horizontalHitBox = new Rect(x + imageWidth / 5, y + imageHeight/ 6, x+ 4 * imageWidth / 5, y+ 5 * imageHeight / 6);
            imageBox = new Rect(x, y, x+imageWidth, y+imageWidth);
            setDirection(-1, 0);
            //Going left/right
            movingHorizontally = Math.abs(direction[0]) > Math.abs(direction[1]);
            if(movingHorizontally)
                setHitBox(horizontalHitBox);
            else
                setHitBox(verticalHitBox);
            moving = true;
            speed = (int)(GameActivity.tileWidth / 10f);
        }

        public void update() {
            if(moving) {
                setX((int)(x + direction[0] * speed));
                setY((int)(y + direction[1] * speed));
                if(movingHorizontally)
                    hitBox.offsetTo(x + imageWidth / 5, y + imageHeight / 6);
                else
                    hitBox.offsetTo(x + imageWidth / 6, y + imageHeight / 5);
                imageBox.offsetTo(x, y);
                distanceTraveled += Math.abs((int)(direction[0] * speed));
                //distanceTraveled += Math.abs((int) (direction[1] * speed));
                if(distanceTraveled >= patrolRoute) {
                    setDirection(-direction[0], -direction[1]);
                    distanceTraveled = 0;
                }
            }
        }

        public void setPath(int distance) {
            patrolRoute = distance;
        }

        public void setHitBox(Rect newHitBox) {
            hitBox = newHitBox;
        }

    }
}
