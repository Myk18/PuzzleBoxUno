package ua.cn.sandi.puzzleboxuno;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mikni on 03.10.2017.
 */

public class PuzzleBoxUno extends Activity implements View.OnTouchListener {

    ImageButton mi;

    ImageView iv;
    ImageView iv_k;
    ImageView iv_p;

    TextView tv;
    TextView tvp;
    TextView tvk;

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

    public void setImg(int img) {
        this.img = img;
    }

    public int getImg() {
        return img;
    }

    int img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        h = new Handler();

        // layout splashscreen
        // setContentView(R.layout.main_splash);

        // layout menu
        setContentView(R.layout.main_img);
        // strart

        final ViewGroup sceneRoot = (ViewGroup) findViewById(R.id.container);
        final View square = sceneRoot.findViewById(R.id.transition_square);

        final int newSquareSize = getResources().getDimensionPixelSize(R.dimen.square_size_expanded);
        final int oldSquareSize = getResources().getDimensionPixelSize(R.dimen.square_size_normal);


        sceneRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams params = square.getLayoutParams();
                TransitionManager.beginDelayedTransition(sceneRoot);  // api 19
                if (params.width == oldSquareSize) {
                    params.width = newSquareSize;
                    params.height = newSquareSize;
                }else if(params.width == newSquareSize){
                    params.width = oldSquareSize;
                    params.height = oldSquareSize;
                }
                square.setLayoutParams(params); // set
            }
            });

        // end
        iv_p = (ImageView) findViewById(R.id.imageView_puffy);
        iv_k = (ImageView) findViewById(R.id.imageView_kitty);
        tvp = (TextView) findViewById(R.id.textView_p);
        tvk = (TextView) findViewById(R.id.textView_k);

        tvp.setOnClickListener(new OnClickListener() {  // buttonclick

            @Override
            public void onClick(View v) {
                tvp.post(new Runnable() {
                    @Override   // works w/o
                    public void run() {
                        setImg(R.drawable.img01); // puffy res
                        StartMain();
                    }
                });
            }
        });

        tvk.setOnClickListener(new OnClickListener() {  // buttonclick
            @Override
            public void onClick(View v) {
                tvk.post(new Runnable() {
                    @Override   // works w/o
                    public void run() {
                        setImg(R.drawable.img02); // kitty res
                        StartMain();
                    }
                });
            }
        });

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

                //   select and replace GItems
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        pz.selectGItem(PuzzleBoxUno.this,(int) x, (int) y, (int) sx, (int) sy);  //   new    2 items
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

        // make toast
        StringBuilder sb1 = new StringBuilder();
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
                        pz = new Puzzle(PuzzleBoxUno.this,iv, menu_item, img);
                    }
                });
            }
        });
        t.start();

        return super.onOptionsItemSelected(item);
    }

    //  ------------
    void StartMain() {

        // main_layout in 5 sec
        Timer t = new Timer(false);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {

                        // start view
                        setContentView(R.layout.main_layout);

                        mi = (ImageButton) findViewById(R.id.button_menu);
                        tv = (TextView) findViewById(R.id.textView);
                        iv = (ImageView) findViewById(R.id.imageView);

                        iv.setOnTouchListener(PuzzleBoxUno.this);
                        iv.post(new Runnable() {
                            public void run() {
                                // code here   REFRESH

                                ivw = iv.getWidth();
                                ivh = iv.getHeight();
                                iv.getLayoutParams().height = ivw;
                                iv.getLayoutParams().width = ivw;

                                iv.requestLayout();

                                pz = new Puzzle(PuzzleBoxUno.this, iv, 4, img);
                                pz.selectGItem(PuzzleBoxUno.this, 1, 1, 1, 1);
                                //iv.getLayoutParams().height = ivw;
                                //iv.getLayoutParams().width = ivw;
                            }
                        });

                        // start button
                        mi.setOnClickListener(new OnClickListener() {  // buttonclick
                            @Override
                            public void onClick(View v) {
                                iv.post(new Runnable() {
                                    @Override   // works w/o
                                    public void run() {
                                        // menu layout
                                        setContentView(R.layout.main_img);
                                        iv_p = (ImageView) findViewById(R.id.imageView_puffy);
                                        iv_k = (ImageView) findViewById(R.id.imageView_kitty);
                                        tvp = (TextView) findViewById(R.id.textView_p);
                                        tvk = (TextView) findViewById(R.id.textView_k);

                                        tvp.setOnClickListener(new OnClickListener() {  // buttonclick
                                            @Override
                                            public void onClick(View v) {
                                                tvp.post(new Runnable() {
                                                    @Override   // works w/o
                                                    public void run() {

                                                        setImg(R.drawable.img01);
                                                        StartMain();  // puffy res

                                                    }
                                                });
                                            }
                                        });

                                        tvk.setOnClickListener(new OnClickListener() {  // buttonclick
                                            @Override
                                            public void onClick(View v) {
                                                tvk.post(new Runnable() {
                                                    @Override   // works w/o
                                                    public void run() {

                                                        setImg(R.drawable.img02); // kitty res
                                                        StartMain();  // puffy res

                                                    }
                                                });
                                            }
                                        });

                                    }
                                });
                            }
                        });
                        // end
                    }
                });
            }
        }, 500);
    }
    //  ---------------

}  // PuzzleBoxUno ends