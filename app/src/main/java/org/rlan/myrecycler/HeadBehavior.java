package org.rlan.myrecycler;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.rastermill.FrameSequence;
import android.support.rastermill.FrameSequenceDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.rlan.myrecycler.recyclerlib.MyrecylerView;

import java.io.IOException;
import java.io.InputStream;


public class HeadBehavior extends CoordinatorLayout.Behavior<MyrecylerView> {


    private ImageView imageView;
    private boolean isFing;
    FrameSequenceDrawable mDrawable;
    private int minSize = 600;
    private int actionSize = 400;

    private Context context;

    ObjectAnimator oa = new ObjectAnimator();

    public HeadBehavior() {
        super();
    }

    public HeadBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, MyrecylerView child, View dependency) {
        if (dependency instanceof ImageView)
            imageView = (ImageView) dependency;
        return dependency instanceof ImageView;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, MyrecylerView child, View directTargetChild, View target, int nestedScrollAxes) {
       child.addOnScrollListener(new RecyclerView.OnScrollListener() {
           @Override
           public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
               if (newState==0&&isFing)
               {
                   if (!recyclerView.canScrollVertically(-1))
                   {
                       Log.i("dddd","到达顶部");
                   //     topAnimate(imageView);
                   }else if (!recyclerView.canScrollVertically(1))
                   {
                       Log.i("dddd","到达底部");
                   }
                   isFing = false;
                   recyclerView.removeOnScrollListener(this);
               }
           }
       });
        return true;
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, MyrecylerView child, View target, float velocityX, float velocityY, boolean consumed) {
       Log.i("dddd","fing");
        isFing = true;
        return false;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, MyrecylerView child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
       Log.i("dddd",dyConsumed+"    "+dyUnconsumed);
         if (dyConsumed<0)
         {
      //       Log.i("dddd","向上能动"+dyConsumed);
         }else if (dyConsumed>0)
         {
             Log.i("dddd","向下能动"+dyConsumed);

             if (imageView.getBottom()>0)
             {
                 layout(child, dyConsumed);
                 child.scrollBy(0,-dyConsumed);
             }
         }else if (dyConsumed==0)
         {
             if (dyUnconsumed>0)
             {
    //             Log.i("dddd","向下滑到底部");
             }else if (dyUnconsumed<0)
             {
                 Log.i("dddd","向上滑到顶部"+dyUnconsumed);
                 layout(child,dyUnconsumed);
             }
         }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, MyrecylerView child, View target) {
        Log.i("dddd","stop");
      //  performAnimate(imageView);
        fistViewAnimator(imageView);
    }


    private void layout(MyrecylerView chide,int date)
    {
        if (imageView.getBottom()<1400) {
            if ((imageView.getBottom() - date) < 0) {
                imageView.getLayoutParams().height = 0;
                imageView.layout(imageView.getLeft(), imageView.getTop(), imageView.getRight(), 0);
                chide.layout(chide.getLeft(), 0, chide.getRight(), chide.getBottom());
            } else {
                imageView.getLayoutParams().height -= date;
                imageView.layout(imageView.getLeft(), imageView.getTop(), imageView.getRight(), imageView.getBottom() - date);
                chide.layout(chide.getLeft(), chide.getTop() - date, chide.getRight(), chide.getBottom());
            }
        }
    }


    private void performAnimate(View v){
        ViewWrapper wrapper = new ViewWrapper(v);
        wrapper.setOnAimatorEndListner(new ViewWrapper.OnAnimatiorEndListener() {
            @Override
            public void animationrEnd(int end) {
                if (end==600)
                {

                }
            }
        });
        oa.ofInt(wrapper,"height",600).setDuration(600).start();
    }

    private void fistViewAnimator(View v)
    {
        ViewWrapper wrapper = new ViewWrapper(v);
        oa.ofInt(wrapper,"height",0).setDuration(600).start();
    }

    private void topAnimate(View v)
    {

        ViewWrapper wrapper = new ViewWrapper(v);
        wrapper.setOnAimatorEndListner(new ViewWrapper.OnAnimatiorEndListener() {
            @Override
            public void animationrEnd(int end) {
                if (end==300)
                {
                 fistViewAnimator(imageView);
                }
            }
        });
        oa.ofInt(wrapper,"height",300).setDuration(300).start();
    }

    private static class ViewWrapper{
        private View mTarget;

        private OnAnimatiorEndListener listener;

        public void setOnAimatorEndListner(OnAnimatiorEndListener listner)
        {
            this.listener = listner;
        }

        public ViewWrapper(View Target){
            mTarget = Target;
        }

        public int getHeight(){
            return mTarget.getLayoutParams().height;
        }

        public void setHeight(int width){
            if (listener!=null)
            {
                listener.animationrEnd(width);
            }
            mTarget.getLayoutParams().height = width;
            mTarget.requestLayout();
        }

        interface OnAnimatiorEndListener{

            void animationrEnd(int end);
        }
    }

    private void setGif()  {
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open("grilshh.gif");
            FrameSequence fs = FrameSequence.decodeStream(is);
            Log.i("dddd",fs.getFrameCount()+"  gif  ");
            mDrawable = new FrameSequenceDrawable(fs);
            imageView.setImageDrawable(mDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}