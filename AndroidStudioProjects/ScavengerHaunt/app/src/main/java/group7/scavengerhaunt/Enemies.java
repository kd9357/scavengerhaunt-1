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

    protected int directionX;
    protected int directionY;
    protected int patrolRoute;
    protected int distanceTraveled = 0;

    protected double angleDegrees = 0;

    protected boolean illuminated = false;

    public Enemies(Context context, int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean detectCollision(int playerX, int playerY) {
        //return Rect.intersects(playerHitBox, this.hitBox);
        return playerX >= hitBox.left && playerX <= hitBox.right && playerY >= hitBox.top && playerY <= hitBox.bottom;
    }

    public void drawEnemy(Canvas canvas, Paint paint) {
        canvas.drawBitmap(getImage(), getX(), getY(), paint);
    }

    public void drawHitBox(Canvas canvas, Paint paint) {
        canvas.drawRect(hitBox, paint);
    }

    public void update() {
        if(moving) {
            setX(directionX * speed);
            setY(directionY * speed);
            hitBox.offset(x, y);
            distanceTraveled += directionX * speed + directionY * speed;
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

    public void setDirection(int xVector, int yVector) {
        directionX = xVector;
        directionY = yVector;
        angleDegrees = (float) GameActivity.getAngle(directionX, directionY);
        if(directionX < 0)
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

    public static class Ghost extends Enemies {
        public Ghost (Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.transparent_ghost);
            image = Bitmap.createScaledBitmap(temp, GameView.tileWidth * scaleX, GameView.tileHeight * scaleY, true);
            this.imageWidth = image.getWidth();
            this.imageHeight = image.getHeight();
            centerX = x + image.getWidth()/2;
            centerY = y + image.getHeight()/2;
            hitBox = new Rect(x, y, x+imageWidth, y+imageHeight);
            imageBox = new Rect(x, y, x+imageWidth, y+imageWidth);
            setDirection(-1, 0);
            patrolRoute = GameView.tileWidth * 7;
            moving = true;
            speed = 7;
        }

        public void update() {
            if(moving) {
                setX(x + directionX * speed);
                //setY(directionY * speed);
                hitBox.offsetTo(x, y);
                imageBox.offsetTo(x, y);
                distanceTraveled += Math.abs(directionX * speed);
                if(distanceTraveled >= patrolRoute) {
                    setDirection(-directionX, directionY);
                    distanceTraveled = 0;
                }
            }
        }

    }
}