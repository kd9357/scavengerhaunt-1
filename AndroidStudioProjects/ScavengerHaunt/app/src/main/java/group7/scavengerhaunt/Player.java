package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * Created by Kevin on 3/18/2017.
 */

public class Player {
    //Image
    private Bitmap image;

    //Screen Borders
    private int minX = 0;
    private int minY = 0;
    private int maxX;
    private int maxY;

    //Coordinates
    private int x;
    private int y;

    //Direction
    private int destinationX;
    private int destinationY;

    private double distance;
    //Normalized vector
    private double directionX;
    private double directionY;

    //Movement speed
    private int speed = 0;
    private boolean moving = false;
    private float timeElapse = 0.01f;

    public Player(Context context, int screenX, int screenY) {
        x = 100;
        y = 100;
        speed = 15;
        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder_player);
        maxX = screenX - image.getWidth();
        maxY = screenY - image.getHeight();
    }

    //Controls player location
    public void update() {
        if(moving) {
            x += directionX * speed;
            y += directionY * speed;
            //Should calculate by center, not x, y, because x,y is top left corner
            distance = calculateDistance(destinationX, destinationY);
            if(distance <= 20) {
                stopMoving();
            }
        }
        //Ensure player does not leave screen
        if(x > maxX)
            x = maxX;
        else if(x < minX)
            x = minX;
        if(y > maxY)
            y = maxY;
        else if(y < minY)
            y = minY;
    }

    public void setDestination(int destX, int destY) {
        destinationX = destX;
        destinationY = destY;
        distance = calculateDistance(destX, destY);
        directionX = (destinationX - x) / distance;
        directionY = (destinationY - y) / distance;
    }

    private double calculateDistance(int destX, int destY) {
        return Math.sqrt(Math.pow((destX - x), 2) + Math.pow((destY- y), 2));
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

    public int getSpeed() {
        return speed;
    }

}
