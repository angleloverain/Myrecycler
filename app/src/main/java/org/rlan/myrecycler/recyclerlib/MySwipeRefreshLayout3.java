package org.rlan.myrecycler.recyclerlib;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

public class MySwipeRefreshLayout3 extends ViewGroup implements NestedScrollingParent {


    private static final int viewHeight = 600;

    private View recycler;

    int rank = viewHeight / 100;
    private int normalbottom;
    private int normalTop = -viewHeight;
    private ObjectAnimator ob;
    private OverScroller scroller;

    private View footRefreshView;
    private View headRefreshView;

    private int headTop = -viewHeight;
    private int headBottom = 0;
    private int footTop;
    private int footBottom;

    private int leftSize =  0;
    private int rightSize;

    private boolean ishead;
    private boolean isfood;
    private boolean isAnmation;
    private boolean isLongUpLayout;
    private boolean isLongDownLyaout;

    public MySwipeRefreshLayout3(Context context) {
        super(context);
        init(context);
    }

    public MySwipeRefreshLayout3(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MySwipeRefreshLayout3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("ResourceType")
    private void init(Context context)
    {
        ob = new ObjectAnimator();
        scroller = new OverScroller(context);
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


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rightSize = w;
        footTop = h;
        normalbottom = footBottom = footTop + viewHeight;
    }


    @Override    // 初始化的时间，会layout 三次！
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        headTop -= l;
        headBottom -= l;
        footTop -= l;
        footBottom -= l;

        if (isLongUpLayout &&(headTop<normalTop || footBottom < normalbottom))
        {
                headTop = normalTop;
                headBottom = 0;
                footBottom = normalbottom;
                footTop = normalbottom - viewHeight;

                Log.i("dddd","up  ht : "+headTop+" hb : "+headBottom+" ft : "+footTop+" fb : "+footBottom);
        }

        if (isLongDownLyaout &&(headTop>normalTop || footBottom > normalbottom))
        {
                headTop = normalTop;
                headBottom = 0;
                footBottom = normalbottom;
                footTop = normalbottom - viewHeight;

                Log.i("dddd","down ht : "+headTop+" hb : "+headBottom+" ft : "+footTop+" fb : "+footBottom);
        }

        headRefreshView.layout(leftSize,headTop,rightSize,headBottom);

        for(int i = 0;i<getChildCount();i++)
        {
            View view = getChildAt(i);
            if (view instanceof RecyclerView) {
                recycler = view;
                recycler.layout(leftSize,headBottom,rightSize,footTop);
            }
        }
        footRefreshView.layout(leftSize,footTop,rightSize,footBottom);
    }


    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) { }
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) { }
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if ((ishead && velocityY > 0) || (isfood && velocityY < 0))
        {
            return true;           // 是在里把  fing拦截了
        }
        return false;
    }


    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
//        Log.i("dddd","onStart");
        isAnmation = false;
        if (headTop != normalTop || footBottom != normalbottom) {
            ishead = true;
            isfood = true;
        }
        return true;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // 向上能动
        if (dyConsumed < 0) {
//            Log.i("dddd","能够向上"); // dyc 为负值  dyU 为0
//            Log.i("dddd","dyc : "+dyConsumed +"  dyu : "+dyUnconsumed);
            if (footBottom<normalbottom && isfood)
            {
                Log.i("dddd","向下");
                isLongDownLyaout = true;
                myLayout(dyConsumed);
                target.scrollBy(0, -dyConsumed);
            }
        }

        // 向下能动
        if (dyConsumed > 0) {
//            Log.i("dddd","能够向下"); // dyc 为正值  dyu 为0
//           Log.i("dddd","dyc : "+dyConsumed +"  dyu : "+dyUnconsumed);
            if (headTop>normalTop && ishead)
            {
               Log.i("dddd","向上");
                isLongUpLayout = true;
                myLayout(dyConsumed);
                target.scrollBy(0, -dyConsumed);
            }
        }


        // 向下滑动到底部
        if (dyUnconsumed > 0 && dyConsumed == 0) {
//            Log.i("dddd","向下滑动到底部");  // dyc 为0  dyUnconsumed 为正值
//            Log.i("dddd","dyc : "+dyConsumed +"  dyu : "+dyUnconsumed);
            isLongUpLayout = false;
            isLongDownLyaout = false;
            if (headTop > normalTop)
            {
                myLayout(dyUnconsumed);   // 最后一次刷新的时候
//                Log.i("eeee","head bottom : "+ headBottom +" foot top : "+footTop);
            }else {
                isfood = true;
                int level = (normalbottom - footBottom) / rank;
                myLayout(dyUnconsumed * (99-level) / 100);
            }
        }

        if (dyUnconsumed<0&&dyConsumed==0) {
//            Log.i("dddd","向上滑动到顶部");  // dyc 为 0  dyUnconsumed 为负值
//            Log.i("dddd","dyc : "+dyConsumed +"  dyu : "+dyUnconsumed);
            isLongUpLayout = false;
            isLongDownLyaout = false;
            if (footBottom < normalbottom)
            {
                myLayout(dyUnconsumed );
            }else {
                ishead = true;
                int level = headBottom / rank;
                myLayout(dyUnconsumed * (99 - level) / 100);
            }
        }

    }
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        final int t_v = ((int) Math.abs(velocityY));
        if (t_v>6000) {
            ((RecyclerView) target).addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 0) {
                        if (!recyclerView.canScrollVertically(-1)) {
//                            Log.i("dddd", "到达顶部");
                            fling(0, -(t_v / 4), 0, 0, 100);
                        } else if (!recyclerView.canScrollVertically(1)) {
//                            Log.i("dddd", "到达底部");
                            fling(0, t_v / 4, 0, 0, 100);
                        }
                        recyclerView.removeOnScrollListener(this);
                    }
                }
            });
        }
        return false;
    }

    @Override
    public void onStopNestedScroll(View child) {

//        Log.i("dddd","onStop");
        isfood = false;
        ishead = false;
        isAnmation = true;
        isLongUpLayout = false;
        isLongDownLyaout = false;
        if (headTop > normalTop)
        {
            headRecover();
        }
        if (footBottom < normalbottom)
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

    @SuppressLint("WrongCall")
    private void myLayout(int y ) {
        onLayout(false,y,0,0,0);
    }



    private void headRecover()
    {
        ViewWrapper wrapper = new ViewWrapper(headRefreshView, new ViewWrapper.ViewWrapperlistener() {
            @Override
            public void begin(int x) {
                recoverHead(x);
            }
        });
        ob.ofInt(wrapper,"height",0).setDuration(300).start();
    }

    private void footRecover()
    {
        ViewWrapper wrapper = new ViewWrapper(footRefreshView, new ViewWrapper.ViewWrapperlistener() {
            @Override
            public void begin(int x) {
                recoverFoot(x);
            }
        });
        ob.ofInt(wrapper,"height",normalbottom).setDuration(300).start();
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

    public static class ViewWrapper
    {
        private View mTarget;
        private ViewWrapperlistener listener;

        public ViewWrapper(View Target,ViewWrapperlistener listener){
            mTarget = Target;
            this.listener = listener;
        }

        public int getHeight(){
            return mTarget.getBottom();
        }

        public void setHeight(int h){
            if (listener != null)
            {
                listener.begin(h);
            }
        }

        public interface ViewWrapperlistener
        {
            public void begin(int x);
        }
    }

    private void recoverHead(int h)
    {
        if (isAnmation) {
            int cazhi = headBottom - h;
            myLayout(cazhi);
        }
    }

    private void  recoverFoot(int h)
    {
        if (isAnmation) {
            int cazhi = footBottom - h;
            myLayout(cazhi);
        }
    }
}
