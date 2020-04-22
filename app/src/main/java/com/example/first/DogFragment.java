package com.example.first;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DogFragment extends Fragment {
    private static final String ARG_ID_Photo = "idPhoto";
    private static final String ARG_INF_DOG = "infDog";

    private ImageView imgDogMain;
    private TextView textMessenger;
    private View view;
    private int idPhoto;
    private String messenger;

    public DogFragment() {
    }


    public static DogFragment newInstance(int idPhoto, Profile infUser) {
        DogFragment fragment = new DogFragment();
        Bundle args = new Bundle();

        String inf = infUser.getName() + ", "
                + infUser.getAge() + "\n"
                + infUser.getCity();

        args.putInt(ARG_ID_Photo, idPhoto);
        args.putString(ARG_INF_DOG, inf);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idPhoto = getArguments().getInt(ARG_ID_Photo);
            messenger = getArguments().getString(ARG_INF_DOG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dog, container, false);

        imgDogMain = v.findViewById(R.id.imgDogMain);
        textMessenger = v.findViewById(R.id.textInf);

        imgDogMain.setImageResource(idPhoto);
        textMessenger.setText(messenger);
        v.setOnTouchListener(new OnSwipeListener(getContext()));

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        view.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentService;
                intentService = new Intent(getActivity(), MainActivityService.class);
                intentService.setAction("right");

                getActivity().startService(intentService);
            }
        });

        view.findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentService;
                intentService = new Intent(getActivity(), MainActivityService.class);
                intentService.setAction("left");

                getActivity().startService(intentService);
            }
        });
    }





    public class OnSwipeListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;
        private int deltaRight = 10, deltaLeft = 10;

        private static final int SWIPE_VELOCITY_THRESHOLD = 10;
        private static final int SWIPE_THRESHOLD = 10;
        private static final float DX = 0.00005f;

        public OnSwipeListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

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

            /*@Override
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
            }*/

        }

        public void onScrollRight(float diffX) {
            Log.d(MainActivity.INF, "Right");

            LinearLayout.LayoutParams linearLay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

            LinearLayout.LayoutParams linearLay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        }

        public void onSwipeLeft() {

        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }

    }
}
