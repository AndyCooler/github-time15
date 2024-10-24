package com.mythosapps.time15;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.mythosapps.time15.types.ScrollViewType;
import com.mythosapps.time15.util.SwipeDetector;

import java.util.Map;

/**
 * Created by andreas on 22.03.18.
 */

public final class ScrollViewUI {

    public static Integer childViewHeight;

    public static Integer requestedBegin = 8;
    public static Integer requestedEnd = 16;

    public static boolean isBeginHourVisible(int childNumber) {
        boolean visible = (childNumber >= requestedBegin && childNumber < requestedBegin + 4);
        return visible;
    }

    public static boolean isEndHourVisible(int childNumber) {
        boolean visible = (childNumber >= requestedEnd && childNumber < requestedEnd + 4);
        return visible;
    }

    public static void scrollToChild(ScrollView scrollView, int childNumber, ScrollViewType type) {

        if (type == ScrollViewType.BEGIN) {
            requestedBegin = childNumber;
        } else {
            requestedEnd = childNumber;
        }
        if (childViewHeight != null) {
            scrollView.smoothScrollTo(0, childViewHeight * childNumber);
        }
    }

    public static void populateHoursUI(View.OnClickListener listener, Context context, ScrollView scrollView, Map mapValueToView, int rootId, int defaultValue, ScrollViewType type) {

        if (type == ScrollViewType.BEGIN) {
            requestedBegin = 8;
        } else {
            requestedEnd = 16;
        }

        scrollView.setOnTouchListener(new SwipeDetector(scrollView, type));

        LinearLayout layoutView = (LinearLayout) scrollView.getChildAt(0);

        TextView view = null;
        for (int i = 0; i < 24; i++) {
            view = new TextView(context);
            // works already! maybe set width to match_parent
            view.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT, 1f));
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f);
            view.setId(rootId + i);
            view.setText(i < 10 ? "0" + i : "" + i);
            //view.setTextAppearance(this, android.R.style.TextAppearance_Large);
            view.setOnClickListener(listener);
            view.setClickable(true);
            view.setGravity(Gravity.CENTER);
            view.setBackgroundColor(Color.WHITE);
            layoutView.addView(view);
            mapValueToView.put(i, view);
        }
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int height = bottom - top;

                childViewHeight = height;
                if (type == ScrollViewType.BEGIN) {
                    scrollToChild(scrollView, requestedBegin, type);
                } else {
                    scrollToChild(scrollView, requestedEnd, type);
                }
            }
        });
        scrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                scrollView.removeOnLayoutChangeListener(this);
                ViewGroup.LayoutParams params = scrollView.getLayoutParams();
                params.height = 4 * childViewHeight;
                scrollView.setLayoutParams(params);
                layoutView.requestLayout();
            }
        });
    }

    public static void populateFifteensUI(View.OnClickListener listener, Context context, ScrollView scrollView, Map mapValueToView, int rootId) {

        scrollView.setOnTouchListener(new SwipeDetector(scrollView, null));

        LinearLayout layoutView = (LinearLayout) scrollView.getChildAt(0);
        //layoutView.removeAllViews();

        TextView view = null;
        for (int i = 0; i < 60; i += 15) {
            view = new TextView(context);
            // works already! maybe set width to match_parent
            view.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT, 1f));
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f);
            view.setId(rootId + i);
            view.setText(i < 10 ? "0" + i : "" + i);
            //view.setTextAppearance(this, android.R.style.TextAppearance_Large);
            view.setOnClickListener(listener);
            view.setClickable(true);
            view.setGravity(Gravity.CENTER);
            view.setBackgroundColor(Color.WHITE);
            layoutView.addView(view);
            mapValueToView.put(i, view);
        }
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int height = bottom - top;
                //v.removeOnLayoutChangeListener(this);
            }
        });
        scrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                scrollView.removeOnLayoutChangeListener(this);
                ViewGroup.LayoutParams params = scrollView.getLayoutParams();
                params.height = 4 * childViewHeight;
                scrollView.setLayoutParams(params);
                layoutView.requestLayout();
            }
        });
    }
}
