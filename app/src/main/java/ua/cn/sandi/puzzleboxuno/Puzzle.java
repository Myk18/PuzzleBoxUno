package ua.cn.sandi.puzzleboxuno;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;

/**
 * Created by mikni on 05.10.2017.
 */

public class Puzzle {        // Puzzle class start

    Puzzle(Context context, ImageView iv, int diff, int img1) {            //  pz construct

        /*
        fieldcolor = Color.GRAY;
        itemcolor = Color.GREEN;
        linecolor = Color.RED;
        p = new Paint();
        */

        setDiff(diff);
        setIv(iv);

        //   init bitmap
        int minn = Math.min(this.iv.getWidth(), this.iv.getHeight());
        b = Bitmap.createBitmap(minn, minn, Bitmap.Config.ARGB_8888);
        c = new Canvas(b);

        int pw = this.iv.getWidth();
        int ph = this.iv.getHeight();

        int mwh = Math.min(pw, ph); // was min
        itemlenght = (int) Math.ceil(mwh / diff);

        img = img1;

        grid = new Grid(context, diff);

    }

    private int diff;
    private void setDiff(int diff) {
        this.diff = diff;
    }

    private int img = R.drawable.img01;;
    public void setImg(int img) {
        this.img = img;
    }

    /*
    private Paint p;
    private int fieldcolor;
    private int itemcolor;
    private int linecolor;
    */
    /*
    private int pw;
    private int ph;
    private int mwh;
    */

    private int itemlenght;

    private Bitmap b;
    private Canvas c;

    private Grid grid;

    private ImageView iv;

    private void setIv(ImageView iv) { // old init
        this.iv = iv;
    }

    void selectGItem(Context context,int x, int y, int sx, int sy) {

        int ex = 0;
        int ey = 0;
        int sex = 0;
        int sey = 0;

        GItem gitem_temp_e = null;
        GItem gitem_temp_s = null;

        c.drawColor(Color.WHITE); // clear canvas works+

        for (int iw = 0; iw < diff; iw++) {
            for (int ih = 0; ih < diff; ih++) {

                if (grid.coords[iw][ih].getRect_to().contains(x, y)) {   // end coords

                    grid.coords[iw][ih].setBorder(1);
                    gitem_temp_e = new GItem(grid.coords[iw][ih].item_num, grid.coords[iw][ih].item_w, grid.coords[iw][ih].item_h, grid.coords[iw][ih].rect_from, grid.coords[iw][ih].rect_to);
                    ex = iw;
                    ey = ih;

                } else if (grid.coords[iw][ih].getRect_to().contains(sx, sy)) {    // start coords

                    grid.coords[iw][ih].setBorder(2);
                    gitem_temp_s = new GItem(grid.coords[iw][ih].item_num, grid.coords[iw][ih].item_w, grid.coords[iw][ih].item_h, grid.coords[iw][ih].rect_from, grid.coords[iw][ih].rect_to);
                    sex = iw;
                    sey = ih;

                    //  grid.renderToCanvas(getResources(), img, grid.coords[iw][ih],0);

                } else {
                    grid.coords[iw][ih].setBorder(0);
                    grid.renderToCanvas(context.getResources(), grid.coords[iw][ih], 1);
                }
            }
        }

        if (gitem_temp_s != null)  {
            grid.coords[ex][ey].rect_from = gitem_temp_s.rect_from;
            grid.renderToCanvas(context.getResources(), grid.coords[ex][ey], 1);
            grid.coords[sex][sey].rect_from = gitem_temp_e.rect_from;
            grid.renderToCanvas(context.getResources(), grid.coords[sex][sey], 1);
        } else {
            grid.renderToCanvas(context.getResources(), grid.coords[ex][ey], 1);
        }

        iv.setImageBitmap(b);

    }

    class Grid {        //   Grid class
        Grid(Context context, int diff) {    // Grid class constructor

            coords = new GItem[diff][diff];
            orcoords = new GItem[diff][diff];

            initcoords(context, diff);
        }

        GItem[][] coords;
        GItem[][] orcoords;
        Bitmap bitmap;

        void initcoords(Context context,int diff) {

            // make coords
            int in = 0;

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inMutable = true;
            bitmap = BitmapFactory.decodeResource(context.getResources(), img, opt);

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
                    }    // original coords
                }
            }

            // randomize picture

            GItem gitem_temp_r1;

            int x1;
            int y1;
            int xx1;
            int yy1;

            int step = diff*diff;

            for (int mstep = 0; mstep < step; mstep = mstep + 1) {


                x1 = (int)(Math.random()*100)*diff/100;
                y1 = (int)(Math.random()*100)*diff/100;
                xx1 = (int)(Math.random()*100)*diff/100;
                yy1 = (int)(Math.random()*100)*diff/100;

                gitem_temp_r1 = new GItem(0, 0, 0, coords[x1][y1].rect_from, coords[x1][y1].rect_to);

                coords [x1][y1].rect_from = coords [xx1][yy1].rect_from;
                coords [xx1][yy1].rect_from = gitem_temp_r1.rect_from;

                gitem_temp_r1 = null;

            }

            iv.setImageBitmap(b);  // view after init
        }

        void renderToCanvas(Resources res, GItem gitem, int pp) {    // draw grids to canvas


            if (pp == 1) {
                c.drawBitmap(grid.bitmap, gitem.getRect_from(), gitem.getRect_to(), null);
            } else {
                c.drawRect(gitem.getRect_to(), new Paint(Color.RED));
            }
        }
    }

    class GItem {

        int item_w, item_h, item_num;
        Rect rect_from, rect_to;

        int border = 0;

        public int getBorder() {
            return border;
        }
        void setBorder(int border) {
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
    }  // GItem class ends

}   // Puzzle class ends
