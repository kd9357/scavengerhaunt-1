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
    private int imageSize;

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
    private int speed;
    private boolean moving = false;

    public Player(Context context, int screenX, int screenY) {
        //Set current orientation to point northwards as default
        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder_player);
        //Currently assuming player is square
        imageSize = image.getWidth();
        x = screenX / 2 - imageSize / 2;
        y = screenY / 2 - imageSize / 2;
        speed = 15;
        centerX = x + imageSize / 2;
        centerY = y + imageSize / 2;
        hitBox = new Rect(x, y, x + imageSize, y + imageSize);
        screenMaxX = screenX - imageSize;
        screenMaxY = screenY - imageSize;
    }

    //Controls player location
    public void update() {
        if(moving) {
            //Set orientation
            angleDegrees = (float) GameActivity.getAngle(directionX, directionY);
            if(directionX < 0)
                angleDegrees = -angleDegrees;
            //Set new location
            x += directionX * speed;
            centerX = x + imageSize / 2;
            y += directionY * speed;
            centerY = y + imageSize / 2;
            distance = calculateDistance(destinationX, destinationY);
            //Reached goal
            if(distance <= 50)
                stopMoving();
        }
        //Ensure player does not leave screen
        if(x > screenMaxX)
            x = screenMaxX;
        else if(x < screenMinX)
            x = screenMinX;
        if(y > screenMaxY)
            y = screenMaxY;
        else if(y < screenMinY)
            y = screenMinY;

        centerX = x + imageSize / 2;
        centerY = y + imageSize / 2;

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + imageSize;
        hitBox.bottom = y + imageSize;
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
}
