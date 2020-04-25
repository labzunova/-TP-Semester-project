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

    private static final String INFORMATION_PROCESS_FRAGMENT = "infFragment";

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
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmpImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            args.putByteArray(ARG_ID_Photo, byteArray);

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

            byte[] byteArray = getArguments().getByteArray(ARG_ID_Photo);
            bmpImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
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

        Log.d(INFORMATION_PROCESS_FRAGMENT, "we have new fragment in OoCreateView");
        v.setOnTouchListener(new OnSwipeListener(getContext(), v));

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        view.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(INFORMATION_PROCESS_FRAGMENT, "OnClick Right");

                Intent intentService;
                intentService = new Intent(getActivity(), MainActivityService.class);
                intentService.setAction("right");

                getActivity().startService(intentService);
            }
        });

        view.findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(INFORMATION_PROCESS_FRAGMENT, "OnClick Left");

                Intent intentService;
                intentService = new Intent(getActivity(), MainActivityService.class);
                intentService.setAction("left");

                getActivity().startService(intentService);
            }
        });
    }
}

