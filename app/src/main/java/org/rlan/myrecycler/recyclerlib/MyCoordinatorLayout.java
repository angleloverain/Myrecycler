package org.rlan.myrecycler.recyclerlib;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2017/10/25.
 */

public class MyCoordinatorLayout extends CoordinatorLayout {


    public MyCoordinatorLayout(Context context) {
        super(context);
    }

    public MyCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int viewHeight =0;
        for (int i=0;i<getChildCount();i++){
            View child = getChildAt(i);
            child.layout(child.getLeft(),viewHeight,child.getRight(),child.getBottom());
            viewHeight = viewHeight+child.getHeight();
        }
    }

}
