package org.rlan.myrecycler.recyclerlib;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.rlan.myrecycler.R;

/**
 * Created by Administrator on 2017/11/1.
 */

public class MySwipeRefreshLayout extends ViewGroup implements NestedScrollingParent {

    private View mTarget;  // 这个就是Recycler

    private View refreshView; // 这个下拉刷新视图
    private ObjectAnimator ob;
    private ViewWrapper wrapper;
    private boolean isFing = false;
    private boolean isRefresh = false;
    private int maxHeight = 1400;
    private int isRefreshHeight = 800;
    private int refreshHeight = 400;
    private int flingHeight = 200;

    private RecyclerView recyclerView;

    private OnRefreshListener listener;

    public MySwipeRefreshLayout(Context context) {
        super(context);
        init(context);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        Log.i("dddd","init");
        refreshView= LayoutInflater.from(context).inflate(R.layout.refresh_layout,null);
        ob = new ObjectAnimator();
        addView(refreshView);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        //父类的padding 与 子类的margin 同一个值，需要考虑的东西；
        View child = mTarget;
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int childWidth = width - getPaddingLeft() - getPaddingRight();
        int childHeight = height - getPaddingTop() - getPaddingBottom();
        refreshView.layout(childLeft,0,childWidth,0);
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
    }


    private void ensureTarget() {
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(refreshView)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    public void setOnRefresh(OnRefreshListener listener)
    {
        this.listener = listener;
    }

    public void OnRefershComplete()
    {
        recoverAnimator();
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {

    }

    boolean isRun ;
    @Override
    public boolean onStartNestedScroll(final View child, View target, int nestedScrollAxes) {
        if (wrapper!=null)
          wrapper.setIsend(true);
        ((RecyclerView)child).addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState==0&&isFing)
                {
                    if (!recyclerView.canScrollVertically(-1))
                    {
                    //    Log.i("dddd","到达顶部");
                   //     topFilingAnimator();
                    }else if (!recyclerView.canScrollVertically(1))
                    {
                    //    Log.i("dddd","到达底部");
                    }
                    isFing = false;
                    recyclerView.removeOnScrollListener(this);
                }
            }
        });
        return true;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        // 向上都动
        if (dyConsumed<0) {

        }

        // 向下能动
        if (dyConsumed>0) {
            if (refreshView.getBottom()>0) {
                layout(target, dyConsumed,false);
                ((RecyclerView)target).smoothScrollBy(0,-dyConsumed);
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
        Log.i("dddd",isFing+"  stop "+isRefresh);
        if (refreshView.getBottom()>0)
        ((RecyclerView) child).stopScroll();
      //  if (!isFing){
//            if (!isRefresh)
//            {
//                recoverAnimator();
//            }else {
//                refershAnimator();
//            }
      //  }
        recoverAnimator();
      //  ((RecyclerView)child).smoothScrollBy(0,500);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

    }

    private int velocti;

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.i("dddd","fing "+ velocityY);
        velocti = (int) velocityY;
         isFing = true;
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

        if (t>=isRefreshHeight)
        {
            isRefresh = true;
        }

        if (t<maxHeight) {
            chide.layout(l, t, r, b);
            refreshView.layout(l, 0, r, t);
        }
    }

    private void recoverAnimator()
    {
        wrapper = new ViewWrapper(mTarget,0);
        ob.ofInt(wrapper,"height",0).setDuration(300).start();
        wrapper.setOnAimatorEndListner(new OnAnimatiorEndListener() {
            @Override
            public void animationrEnd() {
                isRefresh = false;
            }
        });
    }

    private void topFilingAnimator()
    {
        Log.i("dddd","topFling");
         wrapper = new ViewWrapper(mTarget,flingHeight);
        ob.ofInt(wrapper,"height",flingHeight).setDuration(200).start();
        wrapper.setOnAimatorEndListner(new OnAnimatiorEndListener() {
            @Override
            public void animationrEnd() {
                recoverAnimator();
            }
        });
    }

    private void refershAnimator()
    {
        Log.i("dddd","refresh");
         wrapper = new ViewWrapper(mTarget,refreshHeight);
        ob.ofInt(wrapper,"height",refreshHeight).setDuration(300).start();
        wrapper.setOnAimatorEndListner(new OnAnimatiorEndListener() {
            @Override
            public void animationrEnd() {
                if (listener!=null)
                {
                    listener.onRefresh();
                }
            }
        });
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
