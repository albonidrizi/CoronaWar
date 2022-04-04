package www.virus.war;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.virus.war.R;

public class Backgraund {
    int x = 0,y = 0;
    Bitmap backgraund;
    Backgraund (int screenX , int screenY , Resources res){
  backgraund = BitmapFactory.decodeResource(res , R.drawable.backgraund2 );
  backgraund = Bitmap.createScaledBitmap(backgraund,screenX,screenY,false);

    }

}
