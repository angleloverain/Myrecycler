package org.rlan.myrecycler.recyclerlib;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.rlan.myrecycler.HeadBehavior;

/**
 * Created by Administrator on 2017/10/20.
 */

public class MyrecylerView extends RecyclerView {

    private float start_y;
    private float move_y;

    private View view;

    private ObjectAnimator oa;

    private boolean isTwo;
    private float now_y;

    private float start_y1;


    public MyrecylerView(Context context) {
        super(context);
        init();
    }

    public MyrecylerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyrecylerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        oa = new ObjectAnimator();
    }

    public void setView(View v)
    {
        this.view = v;
        new RecyclerView.Recycler();
    }

    public Recycler getRecycler()
    {
        return new Recycler();
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        super.setNestedScrollingEnabled(enabled);
    }


    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        Log.i("cccc","overScrollBy");
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }



    @Override
    public boolean onTouchEvent(MotionEvent e) {
//        Log.i("dddd","向上  " +canScrollVertically(1));   // false 表示底部
//        Log.i("dddd","向下  " +canScrollVertically(-1));  // false 表示顶部

        switch (e.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                if (oa.isRunning())
                    oa.cancel();
               start_y = e.getRawY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                start_y = e.getRawY();
                isTwo = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTwo)
                {
                    // 两个手指的情况
                    e = MotionEvent.obtain(e);
                    now_y = e.getRawY();
                    move_y = now_y - start_y;
                    start_y = now_y;
                }else {
                    //单个手指的情况
                    now_y = e.getRawY();
                    move_y = now_y - start_y;
                    start_y = now_y;
                }
                break;
            case MotionEvent.ACTION_UP:
            //    performAnimate(view);
                break;
            case MotionEvent.ACTION_POINTER_UP:
             Log.i("dddd"," 2 up");
                start_y = e.getRawY();
                isTwo = false;
                break;
        }
     //   setFrame();
        return super.onTouchEvent(e);
    }


    private void setFrame()
    {
          if (!canScrollVertically(-1)||(view.getLayoutParams().height>0))
          {
             //用layout
          }
    }

    private void performAnimate(View v){
        ViewWrapper wrapper = new ViewWrapper(v);
        oa.ofInt(wrapper,"height",0).setDuration(600).start();
        oa.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private static class ViewWrapper{
        private View mTarget;

        public ViewWrapper(View Target){
            mTarget = Target;
        }

        public int getHeight(){
            return mTarget.getLayoutParams().height;
        }

        public void setHeight(int width){
            mTarget.getLayoutParams().height = width;
            mTarget.requestLayout();
        }
    }

}
