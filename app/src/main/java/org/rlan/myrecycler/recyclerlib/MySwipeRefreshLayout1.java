package org.rlan.myrecycler.recyclerlib;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/11/6.
 */

public class MySwipeRefreshLayout1 extends LinearLayout implements NestedScrollingParent {

    private View mTarget;
    private RelativeLayout refreshView;
    private OverScroller scroller;

    private int refresh_h = 400;
    private int vy;

    private boolean isFling;
    private boolean isFlingTop;

    private ObjectAnimator ob;
    private ViewWrapper wrapper;
    private OnRefreshListener listener;

    public MySwipeRefreshLayout1(Context context) {
        super(context);
        init(context);
    }

    public MySwipeRefreshLayout1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MySwipeRefreshLayout1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        scroller = new OverScroller(context);
        setOrientation(VERTICAL);
        ob = new ObjectAnimator();
        refreshView = new RelativeLayout(context);
        refreshView.setBackgroundColor(Color.GREEN);
        LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,refresh_h);
        addView(refreshView,lp);
    }

    public void setRefreshView(View refreshView)
    {
        this.refreshView.addView(refreshView);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        for(int i=0;i<getChildCount();i++)
        {
            View child = getChildAt(i);
            if (child instanceof RelativeLayout)
            {
                child.layout(child.getLeft(),-child.getBottom(),child.getRight(),0);
            }
            if (child instanceof RecyclerView)
            {
                mTarget = child;
                child.layout(child.getLeft(),0,child.getRight(),child.getBottom());
            }
        }

    }

//    @Override
////    public boolean onInterceptTouchEvent(MotionEvent ev) {
////        Log.i("dddd","thoch");
////        fling(0,-800,0,0,200);
////        return true;
////    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        Log.i("dddd","onNestedScrollAccepted");
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.i("dddd","onStartNestedScroll");
        if (wrapper!=null) {
            wrapper.setIsend(true);
        }
        return true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.i("dddd","onNestedPreScroll");
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.i("dddd","onNestedScroll");
        // 向上都动
        if (dyConsumed<0) {

        }

        // 向下能动
        if (dyConsumed>0) {
            RecyclerView recycler = (RecyclerView) target;
            if (recycler.getTop()!=0) {
                layout(target, dyConsumed,false);
                recycler.scrollBy(0,-dyConsumed);
            }
        }

        // 向下滑动到底部
        if (dyUnconsumed>0&&dyConsumed==0) {

        }

        // 向上滑动到顶部
        if (dyUnconsumed<0&&dyConsumed==0) {
            layout(target, dyUnconsumed,false);
        }

    }

    @Override
    public void onStopNestedScroll(View child) {
        Log.i("dddd","onStopNestedScroll");
        if (refreshView.getBottom()>0) {
            ((RecyclerView) mTarget).stopScroll();
            if (!isFling)
            recoverAnimator();
        }
        isFling = false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }



    @Override
    public boolean onNestedFling(View target, float velocityX,  float velocityY, boolean consumed) {
        Log.i("dddd","onNestedFling   "+velocityY);
        isFling = true;
        vy = (int) velocityY;
        if (refreshView.getBottom()>0) {
            ((RecyclerView) mTarget).stopScroll();
            recoverAnimator();
        }else {
            ((RecyclerView)mTarget).addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState==0)
                    {
                        if (!recyclerView.canScrollVertically(-1))
                        {
                            //    Log.i("dddd","到达顶部");
                            fling(0,vy,0,0,200);  //做滑到顶部的判断
                        }else if (!recyclerView.canScrollVertically(1))
                        {
                            //    Log.i("dddd","到达底部");
                        }
                        recyclerView.removeOnScrollListener(this);
                    }
                }
            });
        }
        return true;
    }


    private void layout(View chide,int date,boolean isend)
    {
        int l = chide.getLeft();
        int r = chide.getRight();
        int b = chide.getBottom();
        int t;
        if (!isend) {
            t = chide.getTop() - date;
        }else {
            t = date;
        }
        chide.layout(l, t, r, b);
        refreshView.layout(l, t-refresh_h, r, t);
    }

    private void fling(int starty,int velocityY,int minY,int maxY,int overY )
    {
        scroller.fling(0,starty,0,velocityY,0,0,minY,maxY,0,overY);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset())
        {
            int height = scroller.getCurrY();
            scrollTo(0, height);
            invalidate();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y<-200)
        {
            y = 0;
        }
        super.scrollTo(x, y);

    }

    private void recoverAnimator()
    {
        wrapper = new ViewWrapper(mTarget,0);
        ob.ofInt(wrapper,"height",0).setDuration(300).start();
    }


    public class ViewWrapper{
        private View mTarget;

        private int size;

        private boolean isend = false;

        public void setIsend(boolean isend)
        {
            this.isend = isend;
        }

        private OnAnimatiorEndListener listener;

        public void setOnAimatorEndListner(OnAnimatiorEndListener listner)
        {
            this.listener = listner;
        }

        public ViewWrapper(View Target,int size){
            mTarget = Target;
            this.size = size;
        }

        public int getHeight(){
            return mTarget.getTop();
        }

        public void setHeight(int width){
            if (isend)
            {
                return;
            }
            if (listener!=null&&size==width)
            {
                listener.animationrEnd();
            }
            layout(mTarget,width,true);
        }
    }




    interface OnAnimatiorEndListener{
        void animationrEnd();
    }

    public  interface OnRefreshListener{

        void onRefresh();
    }
}
