package group7.scavengerhaunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 3/28/2017.
 */

public class Obstacles {
    protected Rect hitBox;
    protected Rect imageBox;
    protected Bitmap image;
    //Coordinates
    protected int x;
    protected int y;

    //Lights attached to obstacle, if any
    protected boolean hasLight = false;
    protected Lights light;

    protected boolean illuminated = false;

    public Obstacles(Context context, int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean detectCollision(int playerX, int playerY) {
        return playerX >= hitBox.left && playerX <= hitBox.right && playerY >= hitBox.top && playerY <= hitBox.bottom;
    }

    public boolean detectCollision(Rect playerHitBox) {
        return Rect.intersects(playerHitBox, this.hitBox);
    }

    public void drawObstacle(Canvas canvas, Paint paint) {
        canvas.drawBitmap(getImage(), getX(), getY(), paint);
    }

    public void drawHitBox(Canvas canvas, Paint paint) {
        canvas.drawRect(hitBox, paint);
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

    public Rect getHitBox() {
        return hitBox;
    }

    public Rect getImageBox() {
        return imageBox;
    }

    public boolean hasLight() {
        return hasLight;
    }

    public Lights getLight() {
        return light;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static class Table extends Obstacles {
        List<Rect> hitBoxes = new ArrayList<>();
        public Table(Context context, int x, int y, int scaleX, int scaleY, boolean recolor) {
            super(context, x, y);
            //Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.table);
            Bitmap temp;
            if(recolor)
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.table_blue);
            else
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.table_full);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBoxes.add(new Rect(x + image.getWidth() / 7, y + image.getHeight() / 6, x + 6 *image.getWidth() / 7, y + 3 * image.getHeight() / 4));
            hitBoxes.add(new Rect(x, y + image.getHeight() / 6, x + image.getWidth() / 6, y + image.getHeight()/2 ));
            hitBoxes.add(new Rect(x + image.getWidth()/ 3, y + 2*image.getHeight()/3, x + 4 *image.getWidth()/7, y + 5 *image.getHeight() / 6));
            hitBoxes.add(new Rect(x + 5 *image.getWidth()/6, y + image.getHeight() / 6, x + image.getWidth(), y + 4 * image.getHeight() / 7));
            hasLight = true;
            illuminated = true;
            light = new Lights(x + image.getWidth() * 47 / 100, y + image.getHeight() * 35 /100, GameActivity.tileWidth * 2);
        }

        public boolean detectCollision(int playerX, int playerY) {
            for (Rect hitBox : hitBoxes) {
                if (playerX >= hitBox.left && playerX <= hitBox.right && playerY >= hitBox.top && playerY <= hitBox.bottom)
                    return true;
            }
            return false;
        }

        public void drawHitBox(Canvas canvas, Paint paint) {
            for(Rect hitBox : hitBoxes) {
                canvas.drawRect(hitBox, paint);
            }
        }
    }

    public static class ClothTable extends Obstacles {
        public ClothTable(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.long_table_with_cloth);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
            hasLight = true;
            illuminated = true;
            light = new Lights(x + image.getWidth() * 47 / 100, y + image.getHeight() * 35 /100, GameActivity.tileWidth * 2);
        }
    }

    public static class WoodTable extends Obstacles {
        public WoodTable(Context context, int x, int y, int scaleX, int scaleY, boolean empty) {
            super(context, x, y);
            Bitmap temp;
            if(empty)
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.long_table_wooden_blank);
            else
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.long_table_wooden);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+ image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + 5 * image.getHeight() / 6);
        }
    }

    public static class RoundTable extends Obstacles {


        public RoundTable(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.circle_table);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + 5 * image.getHeight() / 6);
            hasLight = true;
            illuminated = true;
            light = new Lights(x + image.getWidth() * 47 / 100, y + image.getHeight() * 33 /100, (int)(GameActivity.tileWidth * 2.5));
        }
    }

    //You must use getLights and add to lightList, not standard getLights
    public static class LongTable extends Obstacles {
        private List<Lights> lightList = new ArrayList<>();

        public LongTable(Context context, int x, int y, int scaleX, int scaleY, boolean recolor) {
            super(context, x, y);
            Bitmap temp;
            if(recolor)
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.giant_table_blank);
            else
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.giant_table);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + 5 * image.getHeight() / 6);
            if(!recolor) {
                hasLight = true;
                illuminated = true;
                lightList.add(new Lights(x + image.getWidth() * 22 / 100, y + image.getHeight() * 33 / 100, (int) (GameActivity.tileWidth * 2.5)));
                lightList.add(new Lights(x + image.getWidth() / 2, y + image.getHeight() * 33 / 100, (int) (GameActivity.tileWidth * 2.5)));
                lightList.add(new Lights(x + image.getWidth() * 79 / 100, y + image.getHeight() * 33 / 100, (int) (GameActivity.tileWidth * 2.5)));
            }
        }

        public List<Lights> getLights() {
            return lightList;
        }
    }

    public static class BookCase extends Obstacles {
        public BookCase(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.book_case);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
        }
    }

    public static class Bed extends Obstacles {
        public Bed(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bed);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
        }
    }

    public static class Couch extends Obstacles {
        public Couch(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.couch);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
        }
    }

    public static class LoungeChair extends Obstacles {
        public LoungeChair(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.lounge_chair);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
        }
    }

    public static class Dresser extends Obstacles {
        public Dresser(Context context, int x, int y, double scaleX, double scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.dresser);
            image = Bitmap.createScaledBitmap(temp, (int)(GameActivity.tileWidth * scaleX), (int)(GameActivity.tileHeight * scaleY), true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
            hasLight = true;
            illuminated = true;
            light = new Lights(x + image.getWidth() / 2, y + image.getHeight() / 4, GameActivity.tileWidth * 2);
        }
    }

    public static class Island extends Obstacles {
        public Island(Context context, int x, int y, double scaleX, double scaleY, boolean recolor) {
            super(context, x, y);
            Bitmap temp;
            if(recolor)
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.island_blank);
            else
                temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.island);
            image = Bitmap.createScaledBitmap(temp, (int)(GameActivity.tileWidth * scaleX), (int)(GameActivity.tileHeight * scaleY), true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
            if(!recolor) {
                hasLight = true;
                illuminated = true;
                light = new Lights(x + image.getWidth() * 60 / 100, y + image.getHeight() * 27 / 100, GameActivity.tileWidth * 2);
            }
        }
    }

    public static class Stove extends Obstacles {
        public Stove(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.stove);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
        }
    }

    public static class Television extends Obstacles {
        public Television(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.television);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
        }
    }

    public static class Wardrobe extends Obstacles {
        public Wardrobe(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.wardrobe);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
        }
    }

    public static class Box extends Obstacles {
        public Box(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.boxes);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
        }
    }

    public static class Lamp extends Obstacles {
        public Lamp(Context context, int x, int y, double scaleX, double scaleY, boolean lightOn) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.tall_lamp);
            image = Bitmap.createScaledBitmap(temp, (int)(GameActivity.tileWidth * scaleX), (int)(GameActivity.tileHeight * scaleY), true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
            if(lightOn) {
                hasLight = true;
                illuminated = true;
                light = new Lights(x + image.getWidth() / 2, y + image.getHeight() / 5, GameActivity.tileWidth * 2);
            }
        }
    }

    public static class WallSconce extends Obstacles {
        public WallSconce(Context context, int x, int y, double scaleX, double scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.wall_sconce);
            image = Bitmap.createScaledBitmap(temp, (int)(GameActivity.tileWidth * scaleX), (int)(GameActivity.tileHeight * scaleY), true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
            hasLight = true;
            illuminated = true;
            light = new Lights(x + image.getWidth() / 2, y + image.getHeight() / 2, GameActivity.tileWidth * 2);
        }
    }

    public static class Lantern extends Obstacles {
        public Lantern(Context context, int x, int y, double scaleX, double scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.lantern);
            image = Bitmap.createScaledBitmap(temp, (int)(GameActivity.tileWidth * scaleX), (int)(GameActivity.tileHeight * scaleY), true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            hitBox = new Rect(x, y, x + image.getWidth(), y + image.getHeight());
            hasLight = true;
            illuminated = true;
            light = new Lights(x + image.getWidth() / 2, y + image.getHeight() / 2, GameActivity.tileWidth * 2);
        }
    }
    //The fireplace is a triangle, not a rectangle hitbox
    public static class Fireplace extends Obstacles {
        private float[] p1 = new float[2];
        private float[] p2 = new float[2];
        private float[] p3 = new float[2];

        //Right now we're hardcoding the hitbox to be tile wider (to the left) and lower than normal
        public Fireplace(Context context, int x, int y, int scaleX, int scaleY) {
            super(context, x, y);
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.fireplace_complete);
            image = Bitmap.createScaledBitmap(temp, GameActivity.tileWidth * scaleX, GameActivity.tileHeight * scaleY, true);
            imageBox = new Rect(x, y, x+image.getWidth(), y+image.getHeight());
            p1[0] = (float)x - GameActivity.tileWidth;
            p1[1] = (float)y;
            p2[0] = (float)(x + image.getWidth());
            p2[1] = (float)y;
            p3[0] = (float)(x + image.getWidth());
            p3[1] = (float)(y + image.getHeight() + GameActivity.tileHeight);
            hasLight = true;
            illuminated = true;
            light = new Lights(x + image.getWidth() * 3 / 5, y + image.getHeight() / 3, GameActivity.tileWidth * (scaleX * 2) / 3);
        }

        //Calculating Barycentric coordinates (flashbacks to computer graphics)
        public boolean detectCollision(int playerX, int playerY) {
            float pX = (float)playerX;
            float pY = (float)playerY;
            float alpha = ((p2[1] - p3[1]) * (pX - p3[0])
                    + (p3[0] - p2[0]) * (pY - p3[1]))
                    / ((p2[1] - p3[1]) * (p1[0] - p3[0])
                    + (p3[0] - p2[0]) * (p1[1] - p3[1]));
            float beta = ((p3[1] - p1[1]) * (pX - p3[0])
                    + (p1[0] - p3[0]) * (pY - p3[1]))
                    / ((p2[1] - p3[1])  * (p1[0] - p3[0])
                    + (p3[0] - p2[0]) * (p1[1] - p3[1]));
            float gamma = 1.0f - alpha - beta;
            return alpha > 0 && beta > 0 && gamma > 0;
        }

        public void drawHitBox(Canvas canvas, Paint paint) {
            canvas.drawLine(p1[0], p1[1], p2[0], p2[1], paint);
            canvas.drawLine(p1[0], p1[1], p3[0], p3[1], paint);
            canvas.drawLine(p3[0], p3[1], p2[0], p2[1], paint);
        }
    }
}
