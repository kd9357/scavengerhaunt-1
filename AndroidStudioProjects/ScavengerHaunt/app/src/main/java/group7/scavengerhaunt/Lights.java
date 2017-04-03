package group7.scavengerhaunt;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

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

    public void drawLight(Canvas canvas, Paint paint) {
        canvas.drawCircle(getX(), getY(), getRadius(), paint);
    }

    public boolean detectCollision(Rect obj) {
        //Case 1: if the center is inside the rectangle
        if(obj.contains(centerX, centerY))
            return true;
        //If it's not that easy, check point of rectangle closest to circle
        int xStar = Math.min(Math.max(centerX, obj.left), obj.right);
        int yStar = Math.min(Math.max(centerY, obj.top), obj.bottom);
        double dist = GameActivity.calculateDistance(centerX, centerY, xStar, yStar);
        return dist <= radius;
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
        private int largerRadius;
        //Where the arc starts, where 270 is straight ahead
        private int startingAngle;
        //Theta of the sector, where 90 produces quarter of a circle
        private int sweepingAngle;
        Paint radialGradientPaint;      //Causes normal light to fade out
        Paint radialColorPaint;         //Causes blurred yellow light from source
        RadialGradient gradient;        //Radial gradient shader effect

        //x, y will correspond to player's center xy
        public Flashlight(int x, int y, int r, int startingAngle, int sweepingAngle) {
            super(x, y, r);
            this.startingAngle = startingAngle;
            this.sweepingAngle = sweepingAngle;
            radialGradientPaint = new Paint();
            radialColorPaint = new Paint();
            radialColorPaint.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.NORMAL));
            circle = new RectF();
            circle.set(x-r, y-r, x+r, y+r);
            largerRadius = radius + radius / 7;
        }

        public void setCircle(int x, int y, int r) {
            circle.set(x-r, y-r, x+r, y+r);
            this.centerX = x;
            this.centerY = y;
            this.radius = r;
        }

        public void drawLight(Canvas canvas, Paint paint) {
            canvas.drawArc(getCircle(), getStartingAngle(), getSweepingAngle(), true, paint);
            gradient = new RadialGradient(getX(), getY(), getRadius(),
                    new int[] {0x00000000, 0xFF000000}, null, android.graphics.Shader.TileMode.CLAMP);
            radialGradientPaint.setShader(gradient);
            canvas.drawCircle(getX(), getY(), largerRadius, radialGradientPaint);
        }

        public void drawColorLight(Canvas canvas) {
            gradient = new RadialGradient(getX(), getY(), getRadius(),
                    new int[] {0x64feece0, 0x00000000}, null, android.graphics.Shader.TileMode.CLAMP);
            radialColorPaint.setShader(gradient);
            canvas.drawArc(getCircle(), getStartingAngle(), getSweepingAngle(), true, radialColorPaint);
        }

        public boolean detectCollision(Rect obj) {
            //Case 1: if the center is inside the rectangle
            if(obj.contains(centerX, centerY))
                return true;
            //If it's not that easy, check point of rectangle closest to circle
            int xStar = Math.min(Math.max(centerX, obj.left), obj.right);
            int yStar = Math.min(Math.max(centerY, obj.top), obj.bottom);
            double dist = GameActivity.calculateDistance(centerX, centerY, xStar, yStar);
            return dist <= largerRadius;
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

        public int getStartingAngle() {
            return startingAngle;
        }

        public int getSweepingAngle() {
            return sweepingAngle;
        }
    }
}
