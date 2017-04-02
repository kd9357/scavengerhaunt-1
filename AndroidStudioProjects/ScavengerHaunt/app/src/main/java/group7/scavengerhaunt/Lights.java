package group7.scavengerhaunt;

import android.graphics.RectF;

/**
 * Created by Kevin on 3/30/2017.
 */

public class Lights {
    protected int centerX;
    protected int centerY;
    protected int radius;

    public Lights(int x, int y, int r) {
        centerX = x;
        centerY = y;
        radius = r;
    }

    public int getX() {
        return centerX;
    }

    public int getY() {
        return centerY;
    }

    public int getRadius() {
        return radius;
    }

    public void setCircle(int x, int y, int r) {
        centerX = x;
        centerY = y;
        radius = r;
    }

    public static class Flashlight extends Lights{
        //The base Circle with given radius. The light will be a sector of this
        private RectF circle;
        private RectF largerCircle;
        //Where the arc starts, where 270 is straight ahead
        private int startingAngle;
        //Theta of the sector, where 90 produces quarter of a circle
        private int sweepingAngle;

        //x, y will correspond to player's center xy
        public Flashlight(int x, int y, int r, int startingAngle, int sweepingAngle) {
            super(x, y, r);
            this.startingAngle = startingAngle;
            this.sweepingAngle = sweepingAngle;
            circle = new RectF();
            circle.set(x-r, y-r, x+r, y+r);
            largerCircle = new RectF();
            largerCircle.set(x - (r + r/10), y-(r + r/10), x+(r + r/10), y+(r + r/10));
        }

        public void setCircle(int x, int y, int r) {
            circle.set(x-r, y-r, x+r, y+r);
            largerCircle.set(x - (r + r/10), y-(r + r/10), x+(r + r/10), y+(r + r/10));
            this.centerX = x;
            this.centerY = y;
            this.radius = r;
        }

        public void setStartingAngle(int theta) {
            startingAngle = theta;
        }

        public void setSweepingAngle(int theta) {
            sweepingAngle = theta;
        }

        public RectF getCircle() {
            return circle;
        }

        public RectF getLargerCircle() { return largerCircle;};

        public int getStartingAngle() {
            return startingAngle;
        }

        public int getSweepingAngle() {
            return sweepingAngle;
        }
    }
}
