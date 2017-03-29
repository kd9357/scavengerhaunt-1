package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;


/**
 * Created by Kevin on 3/18/2017.
 */

public class Player {
    //Image
    private Bitmap image;
    private int imageWidth;
    private int imageHeight;

    //Screen Borders
    private int screenMinX = 0;
    private int screenMinY = 0;
    private int screenMaxX;
    private int screenMaxY;

    //Coordinates
    //Top-left for drawing purposes
    private int x;
    private int y;
    //For calculation purposes
    private int centerX;
    private int centerY;

    //Collision Detection
    private Rect hitBox;

    //The point the player should travel to
    private int destinationX;
    private int destinationY;
    //The current distance between the player and the point
    private double distance;
    //The normalized direction the player should be heading to
    private double directionX;
    private double directionY;
    //Player's heading in relation with North
    private double angleDegrees = 0;

    //Movement speed
    private int speed = 10;
    private boolean moving = false;

    //Has key?
    private boolean hasKey = false;

    public Player(Context context, int screenX, int screenY, int tileWidth, int tileHeight) {
        //For the wall
        screenMinX = tileWidth;
        //Set current orientation to point northwards as default
        Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder_player);
        image = Bitmap.createScaledBitmap(temp, tileWidth, tileHeight, true);
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
        //Set position
        x = screenX / 2;
        y = screenY / 2;
        centerX = x + imageWidth / 2;
        centerY = y + imageHeight / 2;
        hitBox = new Rect(x, y, x + imageWidth, y + imageHeight);
        screenMaxX = screenX - imageWidth;
        screenMaxY = screenY - imageHeight;
    }

    //Controls player location
    public int[] update() {
        int[] temp = {this.centerX, this.centerY};
        if(moving) {
            //Set orientation
            angleDegrees = (float) GameActivity.getAngle(directionX, directionY);
            if(directionX < 0)
                angleDegrees = -angleDegrees;
            temp[0] += directionX * speed;
            temp[1] += directionY * speed;
        }
        return temp;
    }

    public void setLocation(int newX, int newY) {
        x = newX - imageWidth/2;
        y = newY - imageHeight/2;

        distance = calculateDistance(destinationX, destinationY);
        //Reached goal
        if(distance <= 50)
                stopMoving();
        //Ensure player does not leave screen
        if(x > screenMaxX)
            x = screenMaxX;
        else if(x < screenMinX)
            x = screenMinX;
        if(y > screenMaxY)
            y = screenMaxY;
        else if(y < screenMinY)
            y = screenMinY;

        centerX = x + imageWidth / 2;
        centerY = y + imageHeight / 2;

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + imageWidth;
        hitBox.bottom = y + imageHeight;
    }

    public void setDestination(int destX, int destY) {
        destinationX = destX;
        destinationY = destY;
        distance = calculateDistance(destX, destY);
        directionX = (destinationX - centerX) / distance;
        directionY = (destinationY - centerY) / distance;
    }

    private double calculateDistance(int destX, int destY) {
        return Math.sqrt(Math.pow((destX - centerX), 2) + Math.pow((destY - centerY), 2));
    }

    public void startMoving() { moving = true; }

    public void stopMoving() { moving = false; }

    public void foundKey() {
        hasKey = true;
    }

    public Bitmap getImage() {
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

    public boolean hasKey() {
        return hasKey;
    }
}
