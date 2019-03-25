package org.rlan.myrecycler.recyclerlib;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import org.rlan.myrecycler.R;

public class MySwipeRefreshLayout2 extends LinearLayout implements NestedScrollingParent {

    private View recycler;
    private View headRefreshView;
    private View footRefreshView;
    private ObjectAnimator ob;
    private OverScroller scroller;

    private int top ;
    private int bottom;
    private int viewHeight = 600;
    private int limtSize = viewHeight -50;

    private boolean ishead;
    private boolean isfood;
    private boolean isAnmation = false;

    public MySwipeRefreshLayout2(Context context) {
        super(context);
        init(context);
    }

    public MySwipeRefreshLayout2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MySwipeRefreshLayout2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        scroller = new OverScroller(context);
        setOrientation(VERTICAL);
        initView(context);
        ob = new ObjectAnimator();
    }

    @SuppressLint("ResourceType")
    private void initView(Context context)
    {
        if (headRefreshView ==null)
        {
            headRefreshView = new View(context);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,viewHeight);
            headRefreshView.setBackgroundColor(Color.parseColor("#FFFA98"));
            headRefreshView.setId(1);
            addView(headRefreshView,lp);
        }
        if (footRefreshView == null)
        {
            footRefreshView = new View(context);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,viewHeight);
            footRefreshView.setBackgroundColor(Color.parseColor("#FFFA98"));
            footRefreshView.setId(2);
            addView(footRefreshView,lp);
        }
    }

    // 重新布局
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);  // 这里还有些bug，  只有子类申请全局刷新，就会回到初始状态
        for(int i = 0;i<getChildCount();i++)  // 初始化的时间，会layout 三次！
        {
            View view = getChildAt(i);
            switch (view.getId()) {
                case 1:
                   view.layout(view.getLeft(),-view.getBottom(),view.getRight(),getTop());
                     break;
                case 2:
                    view.layout(view.getLeft(),getBottom(),view.getRight(),getBottom()+view.getBottom());
                     break;
                case R.id.recycler:
                    recycler = view;
                    view.layout(view.getLeft(),getTop(),view.getRight(),getBottom());

                    break;
            }
        }
        top = getTop();
        bottom = getBottom();
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) { }
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) { }
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {

        if ((ishead && velocityY > 0) || (isfood && velocityY < 0))
        {
            Log.i("dddd","  阻拦  : "+velocityY);
            return true;
        }
        return false;
    }


    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
//        Log.i("dddd","onStartNestedScroll");
        isAnmation = false;
        return true;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        // 向上能动
        if (dyConsumed<0) {
//            Log.i("dddd","能够向上"); // dyc 为负值  dyU 为0
//            Log.i("dddd","dyc : "+dyConsumed +"  dyu : "+dyUnconsumed);
            if (footRefreshView.getTop()<bottom)
            {
//                Log.i("dddd","foot 下滑");
                footLayout(target,dyConsumed);
                target.scrollBy(0,-dyConsumed);
            }
        }

        // 向下能动
        if (dyConsumed>0) {
//            Log.i("dddd","能够向下"); // dyc 为正值  dyu 为0
//            Log.i("dddd","dyc : "+dyConsumed +"  dyu : "+dyUnconsumed);
            if (headRefreshView.getBottom()>top)
            {
//                Log.i("dddd","head  上滑");
                headLayout(target,dyConsumed);
                target.scrollBy(0,-dyConsumed);      // 两个移动冲突，出现栈溢出
            }
        }

        // 向下滑动到底部
        if (dyUnconsumed>0&&dyConsumed==0) {
//            Log.i("dddd","向下滑动到底部");  // dyc 为0  dyUnconsumed 为正值
//            Log.i("dddd","dyc : "+dyConsumed +"  dyu : "+dyUnconsumed);
            isfood = true;
            if ((bottom - footRefreshView.getTop()) > 50 && (bottom - footRefreshView.getTop()) < 150)
            {
                footLayout(target,dyUnconsumed /2);
            }else if ((bottom - footRefreshView.getTop()) > 150 && (bottom - footRefreshView.getTop()) < limtSize)
            {
                footLayout(target,dyUnconsumed /4);
            }else if ((bottom - footRefreshView.getTop()) >= limtSize)
            {
                return;
            }else {
                footLayout(target,dyUnconsumed);
            }
        }

        // 向上滑动到顶部
        if (dyUnconsumed<0&&dyConsumed==0) {
//            Log.i("dddd","向上滑动到顶部");  // dyc 为 0  dyUnconsumed 为负值
//            Log.i("dddd","dyc : "+dyConsumed +"  dyu : "+dyUnconsumed);
            ishead = true;
            if (headRefreshView.getBottom()>50 && headRefreshView.getBottom()<150)
            {
                headLayout(target,dyUnconsumed / 2);
            }else if (headRefreshView.getBottom()>150 && headRefreshView.getBottom() < limtSize)
            {
                headLayout(target,dyUnconsumed / 4);
            }else if (headRefreshView.getBottom() >= limtSize)
            {
                return;
            }else {
                headLayout(target,dyUnconsumed);
            }

        }
    }

    @Override
    public boolean onNestedFling(final View target, float velocityX, final float velocityY, boolean consumed) {
        Log.i("dddd"," on velocityY : "+velocityY);
        final int t_v = ((int) Math.abs(velocityY));    // 这里还有bug
//        if (t_v>6000) {
//            ((RecyclerView) target).addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                    if (newState == 0) {
//                        if (!recyclerView.canScrollVertically(-1)) {
//                            Log.i("dddd", "到达顶部");
//                            fling(0, -(t_v / 4), 0, 0, 100);
//                        } else if (!recyclerView.canScrollVertically(1)) {
//                            Log.i("dddd", "到达底部");
//                            fling(0, t_v / 4, 0, 0, 100);
//                        }
//                        ((RecyclerView) target).removeOnScrollListener(this);
//                    }
//                }
//            });
//        }
        return false;
    }


    @Override
    public void onStopNestedScroll(View child) {
//        Log.i("dddd","onStopNestedScroll");
        isfood = false;
        ishead = false;
        isAnmation = true;
        if (headRefreshView.getBottom() != top)
        {
            headRecover();
        }
        if (footRefreshView.getTop() != bottom)
        {
            footRecover();
        }
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

    private void headLayout(View mTarget,int y ) {
        headRefreshView.layout(headRefreshView.getLeft(), headRefreshView.getTop()-y, headRefreshView.getRight(), headRefreshView.getBottom()-y);
        mTarget.layout(mTarget.getLeft(),mTarget.getTop()-y,mTarget.getRight(),mTarget.getBottom()-y);
    }

    private void headAnimator(int h) {
        int cazhi = headRefreshView.getBottom() - h;
        headRefreshView.layout(headRefreshView.getLeft(), headRefreshView.getTop()-cazhi, headRefreshView.getRight(), h);
        recycler.layout(recycler.getLeft(),h,recycler.getRight(),recycler.getBottom()-cazhi);
    }

    private void footAnimator(int h)
    {
        int cazhi = footRefreshView.getTop() - h;
        footRefreshView.layout(footRefreshView.getLeft(),h,footRefreshView.getRight(),footRefreshView.getBottom()-cazhi);
        recycler.layout(recycler.getLeft(),recycler.getTop()-cazhi,recycler.getRight(),h);
    }

    private void footLayout(View mTarget,int y){
        footRefreshView.layout(footRefreshView.getLeft(),footRefreshView.getTop()-y,footRefreshView.getRight(),footRefreshView.getBottom()-y);
        mTarget.layout(mTarget.getLeft(),mTarget.getTop()-y,mTarget.getRight(),mTarget.getBottom()-y);
    }

    private void headRecover()
    {
        ViewWrapperHead wrapper = new  ViewWrapperHead(recycler);
        ob.ofInt(wrapper,"height",top).setDuration(300).start();
    }

    private void footRecover()
    {
        ViewWrapperFoot wrapper = new ViewWrapperFoot(recycler);
        ob.ofInt(wrapper,"height",bottom).setDuration(300).start();
    }


    public View getHeadRefreshView() {
        return headRefreshView;
    }

    public void setHeadRefreshView(View headRefreshView) {
        this.headRefreshView = headRefreshView;
    }

    public View getFootRefreshView() {
        return footRefreshView;
    }

    public void setFootRefreshView(View footRefreshView) {
        this.footRefreshView = footRefreshView;
    }

    public void setHeadViewBackguand(String color)
    {
        headRefreshView.setBackgroundColor(Color.parseColor(color));
    }

    public void setFootViewbackguand(String color)
    {
        footRefreshView.setBackgroundColor(Color.parseColor(color));
    }

    private class ViewWrapperHead{
        private View mTarget;

        public ViewWrapperHead(View Target){
            mTarget = Target;
        }

        public int getHeight(){
            return mTarget.getTop();
        }

        public void setHeight(int h){
            // 这里处理最终动画事件
            if (isAnmation) {
                headAnimator(h);
            }
        }
    }

    private class ViewWrapperFoot{
        private View mTarget;

        public ViewWrapperFoot(View Target){
            mTarget = Target;
        }

        public int getHeight(){
            return mTarget.getBottom();
        }

        public void setHeight(int h){
            // 这里处理最终动画事件
            if (isAnmation) {
                footAnimator(h);
            }
        }
    }
}
