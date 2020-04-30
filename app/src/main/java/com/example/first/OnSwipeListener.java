package com.example.first;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class OnSwipeListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private int deltaRight = 0, deltaLeft = 0;

    private static final int SWIPE_VELOCITY_THRESHOLD = 10;
    private static final int SWIPE_THRESHOLD = 10;
    private static final float DX = 0.00005f;

    View view;
    Context context;

    public OnSwipeListener(Context context, View view) {
        gestureDetector = new GestureDetector(context, new GestureListener());

        this.view = view;
        this.context = context;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: // отпускание
            case MotionEvent.ACTION_CANCEL:
                if ((deltaLeft < 400) && (deltaRight < 400)) {
                    LinearLayout.LayoutParams linearLay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    deltaLeft = 0;
                    deltaRight = 0;
                    linearLay.leftMargin = deltaLeft;
                    linearLay.rightMargin = deltaRight;

                    view.setLayoutParams(linearLay);


                    view.setScaleX(1);
                    view.setScaleY(1);
                }
                else if (deltaLeft < -400){
                    // swipe left

                    // transport information in service
                    Log.d(DogFragment.INFORMATION_PROCESS_FRAGMENT, "OnCSwipe Left");

                    Intent intentService;
                    intentService = new Intent(context, MainActivityService.class);
                    intentService.setAction("left");

                    context.startService(intentService);

                    LinearLayout.LayoutParams linearLay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    deltaLeft = -1050;
                    deltaRight = 1050;
                    linearLay.leftMargin = deltaLeft;
                    linearLay.rightMargin = deltaRight;

                    view.setLayoutParams(linearLay);


                    view.setScaleX(1);
                    view.setScaleY(1);
                }
                else {
                    // swipe right

                    // transport information in service
                    Log.d(DogFragment.INFORMATION_PROCESS_FRAGMENT, "OnCSwipe Right");

                    Intent intentService;
                    intentService = new Intent(context, MainActivityService.class);
                    intentService.setAction("right");

                    context.startService(intentService);

                    LinearLayout.LayoutParams linearLay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    deltaLeft = 1050;
                    deltaRight = -1050;
                    linearLay.leftMargin = deltaLeft;
                    linearLay.rightMargin = deltaRight;

                    view.setLayoutParams(linearLay);


                    view.setScaleX(1);
                    view.setScaleY(1);
                }
                break;
        }

        return gestureDetector.onTouchEvent(event);
    }

    final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();

            if (Math.abs(diffX) > 0) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onScrollRight(Math.abs(diffX));
                    } else {
                        onScrollLeft(Math.abs(diffX));
                    }
                }
            }

            return true;
        }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }

                return true;
            }

    }

    public void onScrollRight(float diffX) {
        Log.d(MainActivity.INF, "Right");

        LinearLayout.LayoutParams linearLay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        deltaLeft += diffX;
        deltaRight -= diffX;
        linearLay.leftMargin = deltaLeft;
        linearLay.rightMargin = deltaRight;

        view.setLayoutParams(linearLay);

        if ((deltaRight > 0) && (view.getScaleX() + DX * diffX< 1)) {
            view.setScaleX(view.getScaleX() + DX * diffX);
            view.setScaleY(view.getScaleY() + DX * diffX);
        }
        else if ((view.getScaleX() - DX * diffX > 0.9f) && (deltaRight < 0)) {
            view.setScaleX(view.getScaleX() - DX * diffX);
            view.setScaleY(view.getScaleY() - DX * diffX);
        }
    }

    public void onScrollLeft(float diffX) {
        Log.d(MainActivity.INF, "Left");

        LinearLayout.LayoutParams linearLay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        deltaLeft -= diffX;
        deltaRight += diffX;
        linearLay.leftMargin = deltaLeft;
        linearLay.rightMargin = deltaRight;

        view.setLayoutParams(linearLay);

        if ((deltaLeft > 0) && (view.getScaleX() + DX * diffX < 1)) {
            view.setScaleX(view.getScaleX() + DX * diffX);
            view.setScaleY(view.getScaleY() + DX * diffX);
        }
        else if ((view.getScaleX() - DX * diffX > 0.9f) && (deltaLeft < 0)) {
            view.setScaleX(view.getScaleX() - DX * diffX);
            view.setScaleY(view.getScaleY() - DX * diffX);
        }
    }

    public void onSwipeRight() {
        deltaRight = 550;
        deltaLeft = -550;
    }

    public void onSwipeLeft() {
        deltaRight = -550;
        deltaLeft = 550;
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }

}
