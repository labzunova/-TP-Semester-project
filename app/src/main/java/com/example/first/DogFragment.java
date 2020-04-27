package com.example.first;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class DogFragment extends Fragment {

    public static final String INFORMATION_PROCESS_FRAGMENT = "infFragment";

    private static final String ARG_ID_Photo = "idPhoto";
    private static final String ARG_INF_DOG = "infDog";

    private static final String DEFAULT_STRING = "Swipe\nleft or right";

    private ImageView imgDogMain;
    private TextView textMessenger;
    private View view;
    private Bitmap bmpImage;
    private String messenger = DEFAULT_STRING;

    public DogFragment() {
    }


    public static DogFragment newInstance(Bitmap bmpImage, Profile infUser) {
        Log.d(INFORMATION_PROCESS_FRAGMENT, "Get data for initialisation");

        DogFragment fragment = new DogFragment();
        Bundle args = new Bundle();

        String inf;
        if (infUser != null) {
            Log.d(INFORMATION_PROCESS_FRAGMENT, "We have data for fragment");

            inf = infUser.getName() + ", "
                    + infUser.getAge() + "\n"
                    + infUser.getCity();

            // put photo

            args.putParcelable(ARG_ID_Photo, bmpImage);

            // put string
            args.putString(ARG_INF_DOG, inf);
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
        if (getArguments() != null) {
            Log.d(INFORMATION_PROCESS_FRAGMENT, "Data initialisation fragment in onCrate");

            bmpImage = getArguments().getParcelable(ARG_ID_Photo);
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
        v.setOnTouchListener(new OnSwipeListener(getContext(), v));

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(INFORMATION_PROCESS_FRAGMENT, "onDestroy Fragment");
    }
}

