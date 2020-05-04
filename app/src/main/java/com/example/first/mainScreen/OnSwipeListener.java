package com.example.first.mainScreen;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
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
    private static final long SPEED_SWIPE = 150L;
    private static final int MIN_DELTA_SWIPE = 150;

    View view;
    Context context;
    MainViewModel mViewModel;

    public OnSwipeListener(Context context, View view, MainViewModel mViewModel) {
        gestureDetector = new GestureDetector(context, new OnSwipeListener.GestureListener());
        this.view = view;
        this.context = context;
        this.mViewModel = mViewModel;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP: // отпускание
            case MotionEvent.ACTION_CANCEL:
                if ((deltaLeft < MIN_DELTA_SWIPE) && (deltaRight < MIN_DELTA_SWIPE)) {
                    onNotSwipe();
                }
                else if (deltaLeft < -MIN_DELTA_SWIPE){
                    onSwipeLeft();
                }
                else {
                    onSwipeRight();
                }
                break;
        }

        return gestureDetector.onTouchEvent(event);
    }

    private void onNotSwipe() {
        LinearLayout.LayoutParams linearLay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        deltaLeft = 0;
        deltaRight = 0;
        linearLay.leftMargin = deltaLeft;
        linearLay.rightMargin = deltaRight;

        view.setLayoutParams(linearLay);
    }

    private void onSwipeLeft() {
        final long duration = SPEED_SWIPE;
        final AnimatorSet all = new AnimatorSet();
        all.playSequentially(ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0, -1000).setDuration(duration));
        all.start();

        view = null;

        mViewModel.swipe(ConstValue.SIDE_LEFT);
    }

    private void onSwipeRight() {
        final long duration = SPEED_SWIPE;
        final AnimatorSet all = new AnimatorSet();
        all.playSequentially(ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0, 1000).setDuration(duration));
        all.start();

        view = null;

        mViewModel.swipe(ConstValue.SIDE_RIGHT);
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

    }

    public void onScrollRight(float diffX) {
        Log.d(ConstValue.INF, "Right");

        LinearLayout.LayoutParams linearLay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        deltaLeft += diffX;
        deltaRight -= diffX;
        linearLay.leftMargin = deltaLeft;
        linearLay.rightMargin = deltaRight;

        view.setLayoutParams(linearLay);
    }

    public void onScrollLeft(float diffX) {
        Log.d(ConstValue.INF, "Left");

        LinearLayout.LayoutParams linearLay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        deltaLeft -= diffX;
        deltaRight += diffX;
        linearLay.leftMargin = deltaLeft;
        linearLay.rightMargin = deltaRight;

        view.setLayoutParams(linearLay);
    }

}
