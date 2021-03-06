package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private double[] direction;
    //Player's heading in relation with North
    private double angleDegrees = 0;

    //Movement speed
    private float speed;
    private boolean moving = false;

    //Attached Lights
    Lights.Flashlight flashlight;
    private int flashLightXOffset;
    private int flashLightYOffset;
    Lights selflight;
    private int flashLightRadius;
    private int selfLightRadius;
    private int startingAngle;
    private int sweepingAngle;

    Interactables.Battery battery;
    private long lastTime;  //For battery
    private long movementTime;  //For movement
    private float charge;

    private boolean hasKey = false;

    //Context, startX, startY, screenX min, screenY min, screenX max, screenY max, width of player, width of height
    public Player(Context context, int startX, int startY, int minX, int minY, int screenX, int screenY, int tileWidth, int tileHeight) {
        //For the walls
        screenMinX = minX;
        screenMinY = minY;
        Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar);
        image = Bitmap.createScaledBitmap(temp, tileWidth, tileHeight, true);
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
        //Set current orientation to point northwards as default
        direction = new double[]{0, -1};
        //Set location
        updateX(startX);
        updateY(startY);
        hitBox = new Rect(x + imageWidth / 5, y + imageWidth / 5, x + 4 * imageWidth / 5, y + 4 * imageHeight / 5);
        screenMaxX = screenX - imageWidth;
        screenMaxY = screenY - imageHeight;
        //Create flashlight
        flashLightRadius = tileWidth * 3;
        selfLightRadius = 55 * imageWidth / 100;
        startingAngle = 243;
        sweepingAngle = 54;
        flashLightXOffset = 22 * imageWidth / 100;
        flashLightYOffset = imageHeight/9;
        flashlight = new Lights.Flashlight(x + flashLightXOffset, y + flashLightYOffset, flashLightRadius, startingAngle, sweepingAngle, getDirection());
        selflight = new Lights(centerX, centerY, selfLightRadius);
        charge = 1.0f;
        battery = new Interactables.Battery(context, charge, GameActivity.tileWidth /3, 0, GameActivity.tileWidth, GameActivity.tileHeight);
        lastTime = System.currentTimeMillis();
        movementTime = System.currentTimeMillis();
        speed = GameActivity.tileWidth / 10f;
    }

    //Controls player location & flashlight radius
    public int[] update() {
        int[] coords = {this.centerX, this.centerY};
        float deltaTime = (System.currentTimeMillis() - movementTime);
        deltaTime /= 33.3f;
        movementTime = System.currentTimeMillis();
        if(moving) {
            //Set orientation
            angleDegrees = (float) GameActivity.getAngle(direction[0], direction[1]);
            if(direction[0] < 0)
                angleDegrees = -angleDegrees;
            coords[0] += direction[0] * GameActivity.tileWidth / 10f * deltaTime;
            coords[1] += direction[1] * GameActivity.tileWidth / 10f * deltaTime;
        }
        if (charge > 0.05f && System.currentTimeMillis() - lastTime > 500) {
            lastTime = System.currentTimeMillis();
            if(!MainActivity.mDebugModeOn)
                charge -= 0.01f;
            battery.setPercentage(charge);
            flashLightRadius = (int) (charge * flashlight.getMaxRadius());
            flashlight.setRadius(flashLightRadius);
        }
        return coords;
    }

    //Sets player location
    public void setLocation(int newX, int newY) {
        updateX(newX - imageWidth/2);
        updateY(newY - imageHeight/2);

        distance = GameActivity.calculateDistance(centerX, centerY, destinationX, destinationY);
        //Reached goal with some buffer
        if(distance <= 50)
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

        hitBox.offsetTo(x + imageWidth / 4, y + imageHeight / 4);
        flashlight.setCircle(x + flashLightXOffset, y + flashLightYOffset, flashLightRadius);
        flashlight.setDirection(getDirection());
        selflight.setCircle(centerX, centerY, selfLightRadius);
    }

    public void setDestination(int destX, int destY) {
        destinationX = destX;
        destinationY = destY;
        distance = GameActivity.calculateDistance(centerX, centerY, destX, destY);
        direction[0] = (destinationX - centerX) / distance;
        direction[1] = (destinationY - centerY) / distance;
    }

    //X, Y must be normalized
    public void setDirection(double xVector, double yVector) {
        direction[0] = xVector;
        direction[1] = yVector;
        angleDegrees = (float) GameActivity.getAngle(direction[0], direction[1]);
        if(direction[0] < 0)
            angleDegrees = -angleDegrees;
        flashlight.setCircle(x + flashLightXOffset, y + flashLightYOffset, flashLightRadius);
        flashlight.setDirection(getDirection());
    }

    private void updateX(int x) {
        this.x = x;
        centerX = x + imageWidth/2;
    }

    private void updateY(int y) {
        this.y = y;
        centerY = y + imageHeight/2;
    }

    public void newLocation(int x, int y) {
        updateX(x);
        updateY(y);
        hitBox.offsetTo(x + imageWidth / 4, y + imageHeight / 4);
        flashlight.setCircle(x + flashLightXOffset, y + flashLightYOffset, flashLightRadius);
        flashlight.setDirection(getDirection());
        selflight.setCircle(centerX, centerY, selfLightRadius);
    }

    public void startMoving() { moving = true; }

    public void stopMoving() { moving = false; }

    public boolean isMoving() { return moving; }

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

    public double[] getDirection() {
        return direction;
    }
    public Lights.Flashlight getFlashLight() { return flashlight; }

    public Lights getSelfLight() {return selflight; }

    public int getRadius() {
        return flashLightRadius;
    }

    public float getCharge() { return charge; }

    public void updateCharge(float change) {
        stopMoving();
        if (charge + change > 1.0f) {
            charge = 1.0f;
        }
        else charge += change;
    }

    public void setCharge(float charge) {
        this.charge = charge;
    }
    public boolean hasKey() {
        return hasKey;
    }
}
