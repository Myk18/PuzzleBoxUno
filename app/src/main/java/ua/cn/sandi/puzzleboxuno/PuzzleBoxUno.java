package ua.cn.sandi.puzzleboxuno;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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

        // start button
        mi.setOnClickListener(new View.OnClickListener() {  // buttonclick
            @Override
            public void onClick(View v) {
                iv.post(new Runnable() {
                    public void run() {
                        // code here   REFRESH

                        //old
                        /*
                        pz.make_backimg(pz.getC(),R.drawable.tv_back);
                        pz.makeitems(R.drawable.imgset);
                        */

                        ivw = iv.getWidth();
                        ivh = iv.getHeight();

                        //init class
                        pz = new Puzzle(iv,5);


                    }
                });
            }
        });

        iv.post(new Runnable() {
            public void run() {
                // code

                ivw = iv.getWidth();
                ivh = iv.getHeight();

                //init class
                pz = new Puzzle(iv,5);
                //pz.setIv(iv);


            }
        });
    }

    @Override
    public boolean onTouch (View v, MotionEvent event){

            x = event.getX();
            y = event.getY();

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

                    pz.selectItem((int)x,(int)y);

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

        Toast.makeText(this,sb1.toString(), Toast.LENGTH_LONG).show();


        // init all

        //pz.setDiff(item.getOrder());
        pz = new Puzzle(iv,item.getOrder());


        return super.onOptionsItemSelected(item);
    }



    // class start
    private class Puzzle{

        int diff;
        void setDiff(int diff) {
            this.diff = diff;
        }

        int fieldcolor;
        int itemcolor;
        int linecolor;
        Bitmap b;
        Paint p;
        int pw;
        int ph;
        int mwh;
        int itemlenght;

        Canvas c;
        public Canvas getC() {
            return c;
        }

        ImageView iv;
        void setIv(ImageView iv) { // old init
            this.iv = iv;
/*
            ///   additio0nal
            b = Bitmap.createBitmap(this.iv.getWidth(), this.iv.getHeight(), Bitmap.Config.ARGB_8888);
            c = new Canvas(b);
            iv.setImageBitmap(b);

            pw = this.iv.getWidth();
            ph = this.iv.getHeight();

            mwh = Math.min(pw,ph);
            itemlenght = (int) Math.ceil(mwh/diff);
*/
        }

        void make_backimg(Canvas c, int backimg){

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inScaled = true;
            opt.inMutable = true;
            //Bitmap back = BitmapFactory.decodeResource(getResources(), R.drawable.tv_back, opt);
            Bitmap back = BitmapFactory.decodeResource(getResources(), backimg, opt);
            c.drawBitmap(back, new Rect(0, 0, back.getWidth(), back.getHeight()), new Rect(0, 0, c.getWidth(), c.getHeight()), null);
        }

        GItem selectGItem(int x, int y, GItem[][] coords) {

            GItem gitem = new GItem(0,0,0, new Rect(),new Rect());

            for(int iw=0; iw<diff; iw++) {
                for(int ih = 0; ih < diff; ih++) {
                    if(coords[iw][ih].getRect_to().contains(x,y)){
                        coords[iw][ih].setBorder(1);
                        gitem = coords[iw][ih];
                    }
                }
            }

            return gitem;  // TODO   ok
        }
        void selectItem(int x, int y){
            selectGItem(x,y,pz.getGrid().getCoords());
            grid.renderGrid(pz.getGrid().getCoords(),c);

        }

        Grid grid;
        public Grid getGrid() {
            return grid;
        }

        Puzzle(ImageView iv, int diff){

            fieldcolor = Color.GRAY;
            itemcolor = Color.GREEN;
            linecolor = Color.RED;

            p = new Paint();

            setDiff(diff);
            setIv(iv);

            ///   init bitmap
            b = Bitmap.createBitmap(this.iv.getWidth(), this.iv.getHeight(), Bitmap.Config.ARGB_8888);
            c = new Canvas(b);
            //iv.setImageBitmap(b);

            pw = this.iv.getWidth();
            ph = this.iv.getHeight();

            mwh = Math.min(pw,ph);
            itemlenght = (int) Math.ceil(mwh/diff);

            grid = new Grid(diff);

            // old    setIv(iv);

        }    //  pz construct

        class Grid {        //   Grid class

            GItem[][] coords;
            public GItem[][] getCoords() {
                return coords;
            }

            Grid(int diff) {

                coords = new GItem[diff][diff];

                initcoords(diff, coords);

                renderGrid(coords, c);

            }

            void initcoords(int diff, GItem[][] coords) {

                int in = 0;

                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inMutable = true;
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.imgset, opt);


                int sx, sy, fx, fy;
                int ssx, ssy, ffx, ffy;

                Rect rect_from;
                Rect rect_to;

                int bitmaplength = bitmap.getWidth() / diff;

                for (int iw = 0; iw < diff; iw++) {
                    for (int ih = 0; ih < diff; ih++) {
                        in++;

                        sx = iw * bitmaplength;
                        sy = ih * bitmaplength;
                        fx = (iw + 1) * bitmaplength;
                        fy = (ih + 1) * bitmaplength;

                        ssx = iw * itemlenght;
                        ssy = ih * itemlenght;
                        ffx = (iw + 1) * itemlenght;
                        ffy = (ih + 1) * itemlenght;

                        rect_from = new Rect(sx, sy, fx, fy);
                        rect_to = new Rect(ssx, ssy, ffx, ffy);

                        coords[iw][ih] = new GItem(in, iw, ih, rect_from, rect_to);
                    }
                }
            }

            void renderGrid(GItem[][] coords, Canvas c) {

                for (GItem[] arr2 : coords) {
                    for (GItem arr1 : arr2) {

                        // code here
                        renderToCanvas(getResources(), R.drawable.imgset, arr1, c);

                    }
                }
                iv.setImageBitmap(b);
            }

            void renderToCanvas(Resources res, int obj, GItem gitem, Canvas c) {
                //void renderToCanvas(Resources res, int obj, Rect rect_from, Canvas c, Rect rect_to) {

                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inMutable = true;
                Bitmap bitmap = BitmapFactory.decodeResource(res, obj, opt);
                if(gitem.getBorder()>0){
                    c.drawRect(gitem.getRect_to(), new Paint(Color.BLACK));
                }else {
                    c.drawBitmap(bitmap, gitem.getRect_from(), gitem.getRect_to(), null);
                }
            }

            void updateGrid(GItem it1, GItem it2) {
                Rect rect12 = it1.rect_to;
                Rect rect22 = it2.rect_to;

                it1.setRect_to(rect22);
                it2.setRect_to(rect12);

            }


        }

        class GItem {

            int item_w,item_h,item_num;
            Rect rect_from, rect_to;

            int border =0;
            public int getBorder() {
                return border;
            }
            public void setBorder(int border) {
                this.border = border;
            }

            public Rect getRect_from() {
                return rect_from;
            }
            public Rect getRect_to() {
                return rect_to;
            }
            public void setRect_from(Rect rect_from) {
                this.rect_from = rect_from;
            }
            public void setRect_to(Rect rect_to) {
                this.rect_to = rect_to;
            }

            GItem(int item_num,int item_w,int item_h,Rect rect_from,Rect rect_to){
                this.item_num = item_num;
                this.item_w = item_w;
                this.item_h = item_h;
                this.rect_from = rect_from;
                this.rect_to = rect_to;

            }
        }

        // OLD ///////////////////////////////////////////

        void makeitems(int setimg) {

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), setimg, opt);

            int sx,sy,fx,fy;
            int ssx,ssy,ffx,ffy;
            int bitmaplength = bitmap.getWidth()/diff;

            ssx = 0; // init

            int item_number = 0;

            for(int iw=0; iw<diff; iw++) {

                for(int ih=0; ih<diff; ih++) {

                    ++item_number;

                    sx = iw * bitmaplength;
                    sy = ih * bitmaplength;
                    fx = (iw + 1) * bitmaplength;
                    fy = (ih + 1) * bitmaplength;

                    ssx = iw * itemlenght;
                    ssy = ih * itemlenght;
                    ffx = (iw + 1) * itemlenght;
                    ffy = (ih + 1) * itemlenght;

                    if( item_number >= 0) {
                        c.drawBitmap(bitmap, new Rect(sx, sy, fx, fy), new Rect(ssx, ssy, ffx, ffy), null);


                    } else{
                        c.drawRect(new Rect(ssx, ssy, ffx, ffy), new Paint(Color.YELLOW));
                    }

                    for(int i=-1;i<2;i++){
                        c.drawLine(0, ssy+i, itemlenght*diff, ssy+i , p );
                    }



                }

                for(int i=-1;i<2;i++){
                    c.drawLine(ssx+i, 0, ssx+i , itemlenght*diff , p );
                }

            }

            iv.setImageBitmap(b);


        }
        void makeitems_old2(int setimg) {

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), setimg, opt);
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.imgset, opt);

            int sx,sy,fx,fy;
            int ssx,ssy,ffx,ffy;
            int bitmaplength = bitmap.getWidth()/diff;

            ssx = 0; // init

            int[][] items = new int[diff-1][diff-1]; // frames

            int item_number = 0;

            for(int iw=0; iw<diff; iw++) {

                for(int ih=0; ih<diff; ih++) {

                    ++item_number;

                    sx = iw * bitmaplength;
                    sy = ih * bitmaplength;
                    fx = (iw + 1) * bitmaplength;
                    fy = (ih + 1) * bitmaplength;

                    ssx = iw * itemlenght;
                    ssy = ih * itemlenght;
                    ffx = (iw + 1) * itemlenght;
                    ffy = (ih + 1) * itemlenght;

                    int[] arr ={11,16,22,23};

                    if( item_number >= 0) {
                        c.drawBitmap(bitmap, new Rect(sx, sy, fx, fy), new Rect(ssx, ssy, ffx, ffy), null);


                    } else{
                        c.drawRect(new Rect(ssx, ssy, ffx, ffy), new Paint(Color.YELLOW));
                    }

                    for(int i=-1;i<2;i++){
                        c.drawLine(0, ssy+i, itemlenght*diff, ssy+i , p );
                    }



                }

                for(int i=-1;i<2;i++){
                    c.drawLine(ssx+i, 0, ssx+i , itemlenght*diff , p );
                }

            }

            iv.setImageBitmap(b);


        }
        void makeback_old() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // code
                        BitmapFactory.Options opt = new BitmapFactory.Options();
                        opt.inScaled = true;
                        opt.inMutable = true;

                        Bitmap back = BitmapFactory.decodeResource(getResources(), R.drawable.tv_back, opt);
                        //ivb.setImageBitmap(back);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        void makegrid_old(Paint p) {

            float iws,ihs;

            for( iws = 0; iws<=mwh; iws += itemlenght ){
                c.drawLine(iws, 0, iws, mwh, p );
            }
            for( ihs = 0; ihs <= mwh; ihs += itemlenght ){
                c.drawLine(0,ihs,mwh,ihs,p);
            }
        }
        Integer getFact(Integer i){
            if (i>1){
                return i*getFact(i-1);
            }else{
                return 1;
            }

        }

        /*
                for (GItem[] arr2 : coords) {
                    for (GItem arr1 : arr2) {
                        if (arr1.getRect_to().contains(x, y)) {
                            arr1.setBorder(1);
                            gitem = arr1;
                            break;
                        } else {
                            gitem = new GItem(0,0,0, new Rect(),new Rect());
                        }
                    }
                }
*/

    }   // class end
}

