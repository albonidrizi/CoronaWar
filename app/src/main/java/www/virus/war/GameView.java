package www.virus.war;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements  Runnable {


    private  Thread thread;
    private boolean isPlaying , isGameOver = false;
    private int screenX,screenY, score = 0 ;
    public static   float screenRationX,screenRationY;
    private Paint paint;
    private Bird[] birds;
    private SharedPreferences prefs;
    private Random random;
    private SoundPool soundPool;
    private List<Bullet> bullets;
    private  int saund;
    private Flight flight;
    private GameActivity activity;
    private Backgraund backgraund1,backgraund2;




    public GameView(GameActivity activity,int screenX, int screenY) {
        super(activity);

        this.activity = activity;

        prefs = activity.getSharedPreferences( "game", Context.MODE_PRIVATE );

        this.screenX = screenX;
        this.screenY = screenY;
        screenRationX = 1920f / screenX;
        screenRationY = 1080f / screenY;

        backgraund1 = new Backgraund(screenX,screenY,getResources());
        backgraund2 = new Backgraund(screenX,screenY,getResources());

        flight = new Flight(this , screenY,getResources());

        bullets = new ArrayList<>();

        backgraund2.x = screenX;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        birds = new Bird[4];
        for (int i=0;i<4;i++){

            Bird bird = new Bird(getResources());
            birds[i] = bird;

        }

        random = new Random();
    }





    @Override
    public void run() {
while (isPlaying){
    update ();
    draw ();
    sleep ();

}
    }



    private void update () {

        backgraund1.x -= 5 * screenRationX;
        backgraund2.x -= 5 * screenRationX;

        if (backgraund1.x + backgraund1.backgraund.getWidth() < 0) {
            backgraund1.x = screenX;
        }

        if (backgraund2.x + backgraund2.backgraund.getWidth() < 0) {
            backgraund2.x = screenX;
        }

        if (flight.isGoingUp)
            flight.y -= 20* screenRationY;

        else
            flight.y += 8 * screenRationY;

        if (flight.y < 0)
            flight.y = 0;

        if (flight.y > screenY - flight.height)
            flight.y = screenY - flight.height;

        List<Bullet> trash = new ArrayList<>();

        for (Bullet bullet : bullets) {

            if (bullet.x > screenX)
                trash.add(bullet);

            bullet.x += 50 * screenRationX;


            for (Bird bird : birds) {
                if (Rect.intersects(bird.getCollisionShape(),
                        bullet.getCollisionShape())) {

                    score++;
                    bird.x = -500;
                    bullet.x = screenX + 500;
                    bird.wasShot = true;
                }

            }

        }
        for (Bullet bullet : trash)
            bullets.remove(bullet);

        for (Bird bird : birds){
            bird.x -= bird.speed;

            if (bird.x + bird.width < 0){

                if (!bird.wasShot){
                  isGameOver = true;
                  return;
                }

                int bound = (int) (2 * screenRationX);
                bird.speed = random.nextInt(bound);

                if(bird.speed < (8 * screenRationX))
                bird.speed = (int) (13 * screenRationX);

                bird.x = screenX;
                bird.y = random.nextInt(screenY - bird.height);

                bird.wasShot = false;

            }
            if (Rect.intersects(bird.getCollisionShape(),flight.getCollisionShape())){
                isGameOver = true;
                return;
            }
        }

    }





    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(backgraund1.backgraund, backgraund1.x, backgraund1.y, paint);
            canvas.drawBitmap(backgraund2.backgraund, backgraund2.x, backgraund2.y, paint);

            for (Bird bird : birds)
                canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint);

            canvas.drawText(score + " ", screenX / 2f, 164, paint);

            if (isGameOver) {
                isPlaying = false;
                canvas.drawBitmap(flight.getDead(), flight.x, flight.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                saveHighScore();
                waitBeforeExiting();
                return;
            }

            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);

            for (Bullet bullet : bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }





    private void   waitBeforeExiting() {

        try {
            Thread.sleep(3000);
            activity.startActivity(new Intent(activity , MainActivity.class ));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }





    private void saveHighScore() {

        if (prefs.getInt("highscore", 0) < score ){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore",score);
            editor.apply();
        }

    }





    private void sleep () {
        try {
            thread.sleep(20);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    public void  resum () {
      isPlaying = true;
      thread = new Thread (this);
      thread.start();
    }



    public void pause () {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < screenX /2){
                    flight.isGoingUp = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                flight.isGoingUp = false;
                if (event.getX() > screenX /2 )
                    flight.toShoot++;

                break;
        }

        return true;
    }





    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void newBullet() {
        soundPool = new SoundPool.Builder().setMaxStreams(2).build();
        if (!prefs.getBoolean("isMute", false))
            soundPool.play(saund,1,1,0,0,1);

        Bullet bullet = new Bullet(getResources());
        bullet.x = flight.x + flight.width;
        bullet.y = flight.y + (flight.height / 2);
        bullets.add(bullet);

    }
}
