package com.kushal.kotlinuniversalvideoplayer.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.WindowManager;

public class ViewGestureListener extends SimpleOnGestureListener {

    public static final int SWIPE_LEFT = 1;
    public static final int SWIPE_RIGHT = 2;
    private static final int SWIPE_THRESHOLD = 60;
    private static final String TAG = "ViewGestureListener";
    private Context context;
    private VideoGestureListener listener;

    public ViewGestureListener(Context context2, VideoGestureListener videoGestureListener) {
        this.context = context2;
        this.listener = videoGestureListener;
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        this.listener.onSingleTap();
        return true;
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        float rawX = motionEvent.getRawX() - motionEvent2.getRawX();
        float rawY = motionEvent.getRawY() - motionEvent2.getRawY();
        if (Math.abs(rawX) > Math.abs(rawY)) {
            if (Math.abs(rawX) > 60.0f) {
                listener.onHorizontalScroll(rawX < 0.0f);
            }
        } else if (Math.abs(rawY) > 60.0f) {
            double x = motionEvent.getX();
            double deviceWidth = getDeviceWidth(context);
            Double.isNaN(deviceWidth);
            Double.isNaN(deviceWidth);
            if (x < (deviceWidth * 1.0d) / 5.0d) {
                listener.onVerticalScroll(rawY / ((float) getDeviceHeight(context)), 1);
            } else {
                double x2 = motionEvent.getX();
                double deviceWidth2 = getDeviceWidth(context);
                Double.isNaN(deviceWidth2);
                Double.isNaN(deviceWidth2);
                if (x2 > (deviceWidth2 * 4.0d) / 5.0d) {
                    listener.onVerticalScroll(rawY / ((float) getDeviceHeight(context)), 2);
                }
            }
        }
        return true;
    }

    public static int getDeviceWidth(Context context2) {
        @SuppressLint("WrongConstant") WindowManager windowManager = (WindowManager) context2.getSystemService("window");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getDeviceHeight(Context context2) {
        @SuppressLint("WrongConstant") WindowManager windowManager = (WindowManager) context2.getSystemService("window");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
