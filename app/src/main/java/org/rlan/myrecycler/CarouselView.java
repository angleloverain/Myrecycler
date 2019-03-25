package org.rlan.myrecycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;



public class CarouselView extends RecyclerView {

    private boolean isStart = true;
    private boolean isSoller;
    private SwipeActivity.CarouselAdapter adapter;

    private final static int CAROUSEL_MSG = 0x1;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case CAROUSEL_MSG:
                    adapter = (SwipeActivity.CarouselAdapter) getAdapter();
                    smoothScrollBy(0,adapter.getItmeHeight(),new AccelerateDecelerateInterpolator());
                    break;
            }
        }
    };

    public CarouselView(Context context) {
        super(context);
    //   startCarousel();
       init(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 这可以处理点击事件
        return false;
    }

    public CarouselView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
     //   startCarousel();
        init(context);
    }

    private void  init(Context context)
    {
        setNestedScrollingEnabled(false);
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isSoller&& newState ==0)
                {
                    adapter.removeView(0);
                    recyclerView.scrollToPosition(0);
                    adapter.insetionView(adapter.getItemCount(),"");
                }
                isSoller = false;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0)
                {
                    isSoller = true;
                }
            }
        });
    }


    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return false;
    }

    private void startCarousel()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isStart)
                {
                    SystemClock.sleep(3000);
                    handler.sendEmptyMessage(CAROUSEL_MSG);
                }
            }
        }).start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isStart = false;
    }
}
