package www.virus.war;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;


import com.virus.war.R;

import static www.virus.war.GameView.screenRationX ;
import static www.virus.war.GameView.screenRationY;

public class Bullet {
    int x , y , width, height;
    Bitmap bullet ;

    Bullet (Resources res) {

        bullet = BitmapFactory.decodeResource(res, R.drawable.bullet);

        int width = bullet.getWidth();
        int height = bullet.getHeight();

        width /= 4;
        height /= 4;


        width *= (int) screenRationX;
        height *= (int) screenRationY;

        bullet = Bitmap.createScaledBitmap(bullet, width , height , false );

    }

    Rect getCollisionShape (){
        return new Rect(x , y ,x + width, y + height );
    }

}
