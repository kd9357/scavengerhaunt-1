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

    //Attached Lights
    Lights.Flashlight flashlight;
    Lights selflight;
    private int radius;
    private int startingAngle;
    private int sweepingAngle;

    //Has key?
    private boolean hasKey = false;

    //Context, startX, startY, screenX min, screenY min, screenX max, screenY max, width of player, width of height
    public Player(Context context, int startX, int startY, int minX, int minY, int screenX, int screenY, int tileWidth, int tileHeight) {
        //For the wall
        screenMinX = minX;
        screenMinY = minY;
        //Set current orientation to point northwards as default
        Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar);
        image = Bitmap.createScaledBitmap(temp, tileWidth, tileHeight, true);
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
        //Set position
//        updateX(screenX/2);
//        updateY(screenY/2);
        updateX(startX);
        updateY(startY);
        hitBox = new Rect(x, y, x + imageWidth, y + imageHeight);
        screenMaxX = screenX - imageWidth;
        screenMaxY = screenY - imageHeight;
        //Create flashlight
        radius = tileWidth * 3; //Is actually tileWidth * 8 of original view
        startingAngle = 243;
        sweepingAngle = 51;
        flashlight = new Lights.Flashlight(x + imageWidth/8, y + imageHeight/8, radius, startingAngle, sweepingAngle);
        selflight = new Lights(centerX,centerY,imageHeight/2);
    }

    //Controls player location & flashlight radius
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
        //TODO: if doing battery, update radius of flashlight here
        return temp;
    }

    //Sets player location
    public void setLocation(int newX, int newY) {
        updateX(newX - imageWidth/2);
        updateY(newY - imageHeight/2);

        distance = calculateDistance(destinationX, destinationY);
        //Reached goal
        if(distance <= 20)
                stopMoving();

        //Ensure player does not leave screen
        if(x > screenMaxX)
            updateX(screenMaxX);
        else if(centerX < screenMinX)
            updateX(screenMinX - imageWidth/2);
        if(y > screenMaxY)
            updateY(screenMaxY);
        else if(centerY < screenMinY)
            updateY(screenMinY - imageHeight/2);

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + imageWidth;
        hitBox.bottom = y + imageHeight;

        flashlight.setCircle(x + imageWidth/8, y + imageHeight/8, radius);
        selflight.setCircle(centerX, centerY, imageHeight/2);
    }

    public void setDestination(int destX, int destY) {
        destinationX = destX;
        destinationY = destY;
        distance = calculateDistance(destX, destY);
        directionX = (destinationX - centerX) / distance;
        directionY = (destinationY - centerY) / distance;
    }

    //X, Y must be normalized
    public void setDirection(int xVector, int yVector) {
        directionX = xVector;
        directionY = yVector;
        angleDegrees = (float) GameActivity.getAngle(directionX, directionY);
        if(directionX < 0)
            angleDegrees = -angleDegrees;
    }

    private double calculateDistance(int destX, int destY) {
        return Math.sqrt(Math.pow((destX - centerX), 2) + Math.pow((destY - centerY), 2));
    }

    private void updateX(int x) {
        this.x = x;
        centerX = x + imageWidth/2;
    }

    private void updateY(int y) {
        this.y = y;
        centerY = y + imageHeight/2;
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

    public Lights.Flashlight getFlashLight() { return flashlight; }

    public Lights getSelfLight() {return selflight; }

    public boolean hasKey() {
        return hasKey;
    }
}
