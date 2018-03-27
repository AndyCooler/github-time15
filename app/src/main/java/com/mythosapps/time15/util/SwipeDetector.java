package com.mythosapps.time15.util;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * Created by andreas on 15.03.18.
 */

public class SwipeDetector implements View.OnTouchListener {

    private ScrollView scrollView;
    static final int MIN_DISTANCE = 20;
    private float downX, downY, upX, upY;
    private int mActiveView;

    public SwipeDetector(final ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    public void onTopToBottomSwipe(int scrollTo) {
        scrollView.smoothScrollTo(0, scrollTo);
    }

    public void onBottomToTopSwipe(int scrollTo) {
        scrollView.smoothScrollTo(0, scrollTo);
    }

    public boolean onTouch(View v, MotionEvent event) {
        int scrollY = scrollView.getScrollY();

        ViewGroup layoutView = (ViewGroup) scrollView.getChildAt(0);
        int childCount = layoutView.getChildCount();

        int childViewHeight = layoutView.getChildAt(0).getMeasuredHeight();

        //mActiveView = Integer.valueOf(((ScrollView)v).getScrollY()); // ((scrollY + (viewHeight/2))/viewHeight);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downY = event.getY();
                //   return true;
            }
            case MotionEvent.ACTION_UP: {
                upY = event.getY();
                float deltaY = downY - upY;


                // do swipe
                if (Math.abs(deltaY) > MIN_DISTANCE) {
                    mActiveView = ((scrollY + (childViewHeight / 2)) / childViewHeight);
                    int scrollTo = mActiveView * childViewHeight;
                    if (deltaY < 0) {
                        this.onTopToBottomSwipe(scrollTo);
                        return true;
                    }
                    if (deltaY > 0) {
                        this.onBottomToTopSwipe(scrollTo);
                        return true;
                    }
                }

                //return true;
            }
        }
        return false;
    }
}
