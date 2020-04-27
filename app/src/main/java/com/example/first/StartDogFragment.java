package com.example.first;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class StartDogFragment extends Fragment {
    private static final String INFORMATION_PROCESS_FRAGMENT = "infFragment";

    private static final String DEFAULT_STRING = "Swipe\nleft or right";

    private ImageView imgDogMain;
    private TextView textMessenger;
    private View view;

    public StartDogFragment() {
    }


    public static StartDogFragment newInstance() {
        Log.d(INFORMATION_PROCESS_FRAGMENT, "Get data for initialisation");

        StartDogFragment fragment = new StartDogFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(INFORMATION_PROCESS_FRAGMENT, "onCreate start");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dog, container, false);

        imgDogMain = v.findViewById(R.id.imgDogMain);
        textMessenger = v.findViewById(R.id.textInf);

        imgDogMain.setImageResource(R.drawable.dog_example2);

        textMessenger.setText(DEFAULT_STRING);

        Log.d(INFORMATION_PROCESS_FRAGMENT, "we have new fragment in OoCreateView");

        v.setOnTouchListener(new OnSwipeListener(getContext(), v));

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
    }

}
