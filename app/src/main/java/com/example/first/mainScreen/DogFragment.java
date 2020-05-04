package com.example.first.mainScreen;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.first.Profile;
import com.example.first.R;

public class DogFragment extends Fragment {

    public static final String INFORMATION_PROCESS_FRAGMENT = "infFragment";

    private static final String ARG_ID_PHOTO = "idPhoto";
    private static final String ARG_INF_DOG = "infDog";

    private static final String DEFAULT_STRING = "Default";

    MainViewModel mViewModel;

    private ImageView imgDogMain;
    private TextView textMessenger;
    private Bitmap bmpImage;
    private String messenger = DEFAULT_STRING;

    public DogFragment() {
    }


    public static DogFragment newInstance(String infUser, Bitmap bmpImage) {
        Log.d(INFORMATION_PROCESS_FRAGMENT, "Get data for initialisation");

        DogFragment fragment = new DogFragment();
        Bundle args = new Bundle();

        if (infUser != null) {
            Log.d(INFORMATION_PROCESS_FRAGMENT, "We have data for fragment");

            args.putParcelable(ARG_ID_PHOTO, bmpImage);
            args.putString(ARG_INF_DOG, infUser);
            fragment.setArguments(args);
        }
        else {
            Log.d(INFORMATION_PROCESS_FRAGMENT, "We don not have data for fragment");
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(INFORMATION_PROCESS_FRAGMENT, "onCreate start");

        mViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        if (getArguments() != null) {
            Log.d(INFORMATION_PROCESS_FRAGMENT, "Data initialisation fragment in onCrate");

            bmpImage = getArguments().getParcelable(ARG_ID_PHOTO);
            messenger = getArguments().getString(ARG_INF_DOG);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dog, container, false);

        imgDogMain = v.findViewById(R.id.imgDogMain);
        textMessenger = v.findViewById(R.id.textInf);

        if (bmpImage != null)
            imgDogMain.setImageBitmap(bmpImage);
        else {
            imgDogMain.setImageResource(R.drawable.dog_example2);
        }

        textMessenger.setText(messenger);

        Log.d(INFORMATION_PROCESS_FRAGMENT, "we have new fragment in OnCreateView");
        v.setOnTouchListener(new OnSwipeListener(getContext(), v, mViewModel));

        return v;
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ValueAnimator animator;
        animator = ValueAnimator.ofFloat(0.5f, 1f);
        animator.setDuration(300L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                view.setScaleX((float) animation.getAnimatedValue());
                view.setScaleY((float) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(INFORMATION_PROCESS_FRAGMENT, "onDestroy Fragment");
    }
}

