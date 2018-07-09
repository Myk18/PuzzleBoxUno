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
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



/**
 * Created by mikni on 03.10.2017.
 */

public class PuzzleBoxUno extends Activity implements View.OnTouchListener {

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

    Handler h;
    int menu_item;

    Puzzle pz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        h = new Handler();
/*
        Thread t = new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                    h = new Handler();
                Looper.loop();
            }});
        t.start();
*/
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

                        ivw = iv.getWidth();
                        ivh = iv.getHeight();

                        pz = new Puzzle(iv, 4);


                    }
                });
            }
        });

        iv.post(new Runnable() {
            public void run() {
                // code here   REFRESH

                ivw = iv.getWidth();
                ivh = iv.getHeight();

                pz = new Puzzle(iv, 4);
            }
        });

/*  dont get ivw ivh
        h.post(new Runnable() {
            @Override
            public void run() {
                ivw = iv.getWidth();
                ivh = iv.getHeight();
                pz = new Puzzle(iv,4);
            }
        });
        */

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

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


/*    WORKS
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pz.selectItem((int)x,(int)y);
                        }
                    });
*/
                h.post(new Runnable() {

                    @Override
                    public void run() {
                        pz.selectItem((int) x, (int) y, (int) sx, (int) sy);  //   new    2 items
                        //   pz.selectItem((int)x,(int)y);    // old 1 item
                    }

                });


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
        //
        StringBuilder sb1 = new StringBuilder();

        // Выведем в TextView информацию о нажатом пункте меню
        sb1.append("Item Menu");
        sb1.append("\r\n groupId: " + String.valueOf(item.getGroupId()));
        sb1.append("\r\n itemId: " + String.valueOf(item.getItemId()));

        Toast.makeText(this, sb1.toString(), Toast.LENGTH_LONG).show();

        // init all

        menu_item = item.getOrder();

        Thread t = new Thread(new Runnable() {
            public void run() {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        pz = new Puzzle(iv, menu_item);
                    }
                });
            }
        });
        t.start();

        return super.onOptionsItemSelected(item);
    }

    private class Puzzle {        // Puzzle class start

        Puzzle(ImageView iv, int diff) {            //  pz construct

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

            mwh = Math.min(pw, ph);
            itemlenght = (int) Math.ceil(mwh / diff);

            grid = new Grid(diff);

        }

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

        public Canvas getCanvas() {
            return c;
        }

        Grid grid;

        public Grid getGrid() {
            return grid;
        }

        ImageView iv;

        void setIv(ImageView iv) { // old init
            this.iv = iv;
        }

        void selectItem(int x, int y, int sx, int sy) {

            selectGItem(x, y, sx, sy);

            //    grid.initcoords(pz.diff);

            //    grid.renderGrid();

            //    selectGItem(x,y,sx,sy,pz.getGrid().getCoords());
            //    grid.renderGrid(pz.getGrid().getCoords(),c);
            //    selectGItem(x,y,sx,sy,grid.coords);
            //    grid.renderGrid(grid.coords, c);

        }

        void selectGItem(int x, int y, int sx, int sy) {

            int ex = 0;
            int ey = 0;
            int sex = 0;
            int sey = 0;

            GItem gitem_temp_e = null;
            GItem gitem_temp_s = null;


            for (int iw = 0; iw < diff; iw++) {
                for (int ih = 0; ih < diff; ih++) {

                    if (grid.coords[iw][ih].getRect_to().contains(x, y)) {   // end coords

                        grid.coords[iw][ih].setBorder(1);
                        gitem_temp_e = new GItem(grid.coords[iw][ih].item_num, grid.coords[iw][ih].item_w, grid.coords[iw][ih].item_h, grid.coords[iw][ih].rect_from, grid.coords[iw][ih].rect_to);
                        ex = iw;
                        ey = ih;

                        //grid.renderToCanvas(getResources(), R.drawable.imgset, grid.coords[iw][ih],0);

                    } else if (grid.coords[iw][ih].getRect_to().contains(sx, sy)) {    // start coords

                        grid.coords[iw][ih].setBorder(2);
                        gitem_temp_s = new GItem(grid.coords[iw][ih].item_num, grid.coords[iw][ih].item_w, grid.coords[iw][ih].item_h, grid.coords[iw][ih].rect_from, grid.coords[iw][ih].rect_to);
                        sex = iw;
                        sey = ih;

                        //  grid.renderToCanvas(getResources(), R.drawable.imgset, grid.coords[iw][ih],0);

                    } else {
                        grid.coords[iw][ih].setBorder(0);
                        grid.renderToCanvas(getResources(), R.drawable.imgset, grid.coords[iw][ih], 1);
                    }
                }
            }

            if (gitem_temp_e != null && gitem_temp_s != null) {
                grid.coords[ex][ey].rect_from = gitem_temp_s.rect_from;
                grid.renderToCanvas(getResources(), R.drawable.imgset, grid.coords[ex][ey], 1);
                grid.coords[sex][sey].rect_from = gitem_temp_e.rect_from;
                grid.renderToCanvas(getResources(), R.drawable.imgset, grid.coords[sex][sey], 1);
            }


            iv.setImageBitmap(b);

        }

        class Grid {        //   Grid class
            Grid(int diff) {    // constructor
                coords = new GItem[diff][diff];
                orcoords = new GItem[diff][diff];

                initcoords(diff);

            }

            GItem[][] coords;
            GItem[][] orcoords;
            Bitmap bitmap;


            void initcoords(int diff) {

                int in = 0;

                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inMutable = true;
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.imgset, opt);

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

                        if (orcoords[iw][ih] == null) {
                            orcoords[iw][ih] = new GItem(in, iw, ih, rect_from, rect_to);
                        }
                    }
                }

                GItem gitem_temp_r1 = null;

                int x1 = 0;
                int y1 = 0;
                int xx1 = 0;
                int yy1 = 0;

                int step = diff*diff;

                for (int mstep = 0; mstep < step; mstep = mstep + 1) {


                    x1 = (int)(Math.random()*100)*diff/100;
                    y1 = (int)(Math.random()*100)*diff/100;
                    xx1 = (int)(Math.random()*100)*diff/100;
                    yy1 = (int)(Math.random()*100)*diff/100;

                    gitem_temp_r1 = new GItem(0, 0, 0, coords[x1][y1].rect_from, coords[x1][y1].rect_to); // TODO  works!

                    coords [x1][y1].rect_from = coords [xx1][yy1].rect_from;
                    coords [xx1][yy1].rect_from = gitem_temp_r1.rect_from;

                    gitem_temp_r1 = null;

                }

            }

            void renderToCanvas(Resources res, int obj, GItem gitem, int pp) {

                //   BitmapFactory.Options opt = new BitmapFactory.Options();
                //   opt.inMutable = true;
                //   Bitmap bitmap = BitmapFactory.decodeResource(res, obj, opt);

                if (pp == 1) {
                    c.drawBitmap(grid.bitmap, gitem.getRect_from(), gitem.getRect_to(), null);
                } else {
                    c.drawRect(gitem.getRect_to(), new Paint(Color.RED));
                }
                /*
                    if(gitem.getBorder()>0){    // >0 - old
                        c.drawRect(gitem.getRect_to(), new Paint(Color.YELLOW));
                    //    c.drawBitmap(bitmap, gitem.getRect_from(), gitem.getRect_to(), null);
                    }else {
                    //    c.drawRect(gitem.getRect_to(), new Paint(Color.YELLOW));
                        c.drawBitmap(bitmap, gitem.getRect_from(), gitem.getRect_to(), null);
                    }
                */
            }

        }

        class GItem {

        int item_w, item_h, item_num;
        Rect rect_from, rect_to;

        int border = 0;

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

        GItem(int item_num, int item_w, int item_h, Rect rect_from, Rect rect_to) {
            this.item_num = item_num;
            this.item_w = item_w;
            this.item_h = item_h;
            this.rect_from = rect_from;
            this.rect_to = rect_to;

        }
    }

    }   // Puzzle class ends

}  // PuzzleBoxUno ends

