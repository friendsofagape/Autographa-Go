package com.bridgeconn.autographago.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.bridgeconn.autographago.R;

public class NonSwipeableViewPager extends ViewPager {

    public boolean isSwipeable() {
        return swipeable;
    }

    public void setSwipeable(boolean swipeable) {
        this.swipeable = swipeable;
    }

    private boolean swipeable;

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NonSwipeableViewPager);
        swipeable = a.getBoolean(R.styleable.NonSwipeableViewPager_swipeable, true);
        a.recycle();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return swipeable && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipeable && super.onTouchEvent(event);
    }

}
