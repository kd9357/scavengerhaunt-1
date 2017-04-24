package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Kevin on 4/1/2017.
 */

public class Enemies implements Parcelable {
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

    public double[] getDirection() {
        return direction;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(direction[0]);
        out.writeDouble(direction[1]);
        out.writeInt(x);
        out.writeInt(y);
    }

    public static final Parcelable.Creator<Enemies> CREATOR
            = new Parcelable.Creator<Enemies>() {
        public Enemies createFromParcel(Parcel in) {
            return new Enemies(in);
        }

        public Enemies[] newArray(int size) {
            return new Enemies[size];
        }
    };

    private Enemies(Parcel in) {
        double x1 = in.readDouble();
        double x2 = in.readDouble();
        direction = new double[]{x1, x2};
        x = in.readInt();
        y = in.readInt();

    }

    public static class Ghost extends Enemies {
        private Rect verticalHitBox;
        private Rect horizontalHitBox;
        private boolean movingHorizontally;

        double orbitX;
        double orbitY;
        double orbitRadius;

        double time;

        public Ghost (Context context, int x, int y, int scaleX, int scaleY, double xVector, double yVector, double radius) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.transparent_ghost);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            this.imageWidth = image.getWidth();
            this.imageHeight = image.getHeight();
            centerX = x + image.getWidth()/2;
            centerY = y + image.getHeight()/2;
            setRadius(radius);
            verticalHitBox = new Rect(x + imageWidth / 6, y + imageHeight / 5, x+ 5 * imageWidth / 6, y+ 4 * imageHeight / 5);
            horizontalHitBox = new Rect(x + imageWidth / 5, y + imageHeight/ 6, x+ 4 * imageWidth / 5, y+ 5 * imageHeight / 6);
            imageBox = new Rect(x, y, x+imageWidth, y+imageWidth);
            setHitBox(verticalHitBox);
            setDirection(xVector, yVector);
            moving = true;
            speed = (int)(GameActivity.tileWidth / 10f);
            time = 0;
        }

        public void setDirection(double xVector, double yVector) {
            direction[0] = xVector;
            direction[1] = yVector;
            angleDegrees = (float) GameActivity.getAngle(direction[0], direction[1]);
            if(direction[0] < 0)
                angleDegrees = -angleDegrees;

            movingHorizontally = Math.abs(direction[0]) > Math.abs(direction[1]);
            if(movingHorizontally)
                setHitBox(horizontalHitBox);
            else
                setHitBox(verticalHitBox);
        }
        public void update() {
            if(moving) {
                double radian = speed * time;
                if (Math.toDegrees(radian) > 359) time = 0;
                time+= 0.005;
                double locX = orbitX + orbitRadius*Math.cos(radian);
                double locY = orbitY + orbitRadius*Math.sin(radian);
                setX((int) locX);
                setY((int) locY);
                setAngleDegrees(Math.toDegrees(radian) + 180);
                hitBox.offsetTo(x + imageWidth / 6, y + imageHeight / 5);
//                if(movingHorizontally)
//                    hitBox.offsetTo(x + imageWidth / 5, y + imageHeight / 6);
//                else
//                    hitBox.offsetTo(x + imageWidth / 6, y + imageHeight / 5);
                imageBox.offsetTo(x, y);
            }
        }


        public void setAngleDegrees(double degrees) {
            movingHorizontally = 45 < degrees % 180 && degrees % 180 < 135;
            angleDegrees = degrees;
        }

        public void setRadius(double distance) {
            orbitRadius = distance;
            orbitX = x - distance;
            orbitY = y;
        }

        public void setHitBox(Rect newHitBox) {
            hitBox = newHitBox;
        }

    }


    public static class Zombie extends Enemies {
        private Rect verticalHitBox;
        private Rect horizontalHitBox;
        private boolean movingHorizontally;
        public Zombie (Context context, int x, int y, int scaleX, int scaleY, double xVector, double yVector) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.zombie);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            this.imageWidth = image.getWidth();
            this.imageHeight = image.getHeight();
            centerX = x + image.getWidth()/2;
            centerY = y + image.getHeight()/2;
            verticalHitBox = new Rect(x + imageWidth / 6, y + imageHeight / 5, x+ 5 * imageWidth / 6, y+ 4 * imageHeight / 5);
            horizontalHitBox = new Rect(x + imageWidth / 5, y + imageHeight/ 6, x+ 4 * imageWidth / 5, y+ 5 * imageHeight / 6);
            imageBox = new Rect(x, y, x+imageWidth, y+imageWidth);
            setDirection(xVector, yVector);
            moving = true;
            speed = (int)(GameActivity.tileWidth / 10f);
        }

        public void setDirection(double xVector, double yVector) {
            direction[0] = xVector;
            direction[1] = yVector;
            angleDegrees = (float) GameActivity.getAngle(direction[0], direction[1]);
            if(direction[0] < 0)
                angleDegrees = -angleDegrees;

            movingHorizontally = Math.abs(direction[0]) > Math.abs(direction[1]);
            if(movingHorizontally)
                setHitBox(horizontalHitBox);
            else
                setHitBox(verticalHitBox);
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
                distanceTraveled += Math.abs((int)(direction[0] * speed)) + Math.abs((int)(direction[1] * speed));
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
