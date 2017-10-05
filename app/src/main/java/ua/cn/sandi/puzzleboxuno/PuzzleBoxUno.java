package ua.cn.sandi.puzzleboxuno;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.x;

/**
 * Created by mikni on 03.10.2017.
 */

public class PuzzleBoxUno extends Activity implements View.OnTouchListener  {

    Button mi;
    ImageView iv;
    TextView tv;

    float x;
    float y;
    float sx;
    float sy;

    String sDown;
    String sMove;
    String sUp;

    int ivw;
    int ivh;

    Puzzle pz;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);


        mi = (Button) findViewById(R.id.make_item);
        tv = (TextView) findViewById(R.id.textView);
        iv = (ImageView) findViewById(R.id.imageView);
        iv.setOnTouchListener(this);

        mi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv.post(new Runnable() {
                    public void run() {
                        // code
                        pz.makeitem();
                    }
                });
            }
        });

        iv.post(new Runnable() {
            public void run() {
                // code

                ivw = iv.getWidth();
                ivh = iv.getHeight();
                pz = new Puzzle();
                pz.setIv(iv);

            }
        });
    }

    @Override
    public boolean onTouch (View v, MotionEvent event){

            x = event.getX();
            y = event.getY();

            //   final StatBitmap sb = new StatBitmap();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sDown = "Tdown: x=" + x + ", y=" + y;
                    sMove = "";
                    sUp = "";
                    sx = x;
                    sy = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    sMove = "Tmove: x=" + x + ", y=" + y;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    sUp = "Tup: x=" + x + ", y=" + y;

                    break;
            }

            tv.setText(sDown + "\n" + sMove + "\n" + sUp);

            return true;
        }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        StringBuilder sb1 = new StringBuilder();

        // Выведем в TextView информацию о нажатом пункте меню
        sb1.append("Item Menu");
        sb1.append("\r\n groupId: " + String.valueOf(item.getGroupId()));
        sb1.append("\r\n itemId: " + String.valueOf(item.getItemId()));

        Toast.makeText(this,sb1.toString(), Toast.LENGTH_LONG);

        return super.onOptionsItemSelected(item);
    }



    // class start
    private class Puzzle{



        int diff;
        int fieldcolor;
        int itemcolor;
        int linecolor;

        Bitmap b;
        Bitmap b1;
        Canvas c;
        Canvas c1;
        ImageView iv;
        Paint p;

        int pw;
        int ph;

        int mwh;
        int itemlenght;

        Puzzle(){



            diff = 5;
            fieldcolor = Color.GRAY;
            itemcolor = Color.GREEN;
            linecolor = Color.RED;

            p = new Paint();
        }

        private void setIv(ImageView iv) {
            this.iv = iv;
            b = Bitmap.createBitmap(this.iv.getWidth(), this.iv.getHeight(), Bitmap.Config.ARGB_8888);
            c = new Canvas(b);
            iv.setImageBitmap(b);

            pw = this.iv.getWidth();
            ph = this.iv.getHeight();

            mwh = Math.min(pw,ph);
            itemlenght = (int) Math.ceil(mwh/diff);


            //p.setColor(linecolor);
            //makegrid(p);

            // image

        }

        void makegrid(Paint p) {

            float iws,ihs;

            for( iws = 0; iws<=mwh; iws += itemlenght ){
                c.drawLine(iws, 0, iws, mwh, p );
            }
            for( ihs = 0; ihs <= mwh; ihs += itemlenght ){
                c.drawLine(0,ihs,mwh,ihs,p);
            }
        }

        void makeitem() {             // TODO image

            Paint p = new Paint();
            p.setColor(Color.RED);

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inScaled = true;
            opt.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.imgset, opt);

            int sx,sy,fx,fy;
            int ssx,ssy,ffx,ffy;
            int bitmaplength = bitmap.getWidth()/diff;

            ssx = 0; // init

            for(int iw=0; iw<diff; iw++) {

                for(int ih=0; ih<diff; ih++) {

                    sx = iw * bitmaplength;
                    sy = ih * bitmaplength;
                    fx = (iw + 1) * bitmaplength;
                    fy = (ih + 1) * bitmaplength;

                    ssx = iw * itemlenght;
                    ssy = ih * itemlenght;
                    ffx = (iw + 1) * itemlenght;
                    ffy = (ih + 1) * itemlenght;

                    c.drawBitmap(bitmap, new Rect(sx, sy, fx, fy), new Rect(ssx, ssy, ffx, ffy), null);

                    c.drawLine(0, ssy, itemlenght*diff, ssy , p );

                }

                c.drawLine(ssx, 0, ssx , itemlenght*diff , p );

            }

            iv.setImageBitmap(b);

            //draw top bit
        //    c.drawBitmap(bitmap, new Rect(0,0,300,300), new Rect(0,0,180,180), null);

          //  c.drawBitmap(bitmap, new Rect(300,0,600,300), new Rect(180,0,360,180), null);
            //c.drawBitmap(bitmap, new Rect(600,0,900,300), new Rect(360,0,540,180), null);

         //   c.drawBitmap(bitmap, new Rect(900,0,1200,300), new Rect(540,0,720,180), null);

            //c.drawBitmap(bitmap,400, 400, null);

            //iv.setImageBitmap(bitmap);


        }

        void makeback() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // code
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        // c1.drawBitmap(bitmap, 0, 0, p);
    }   // class start
}

