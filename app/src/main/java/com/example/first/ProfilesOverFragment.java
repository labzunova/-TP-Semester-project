package com.example.first;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ProfilesOverFragment extends Fragment {

    private Button goProfile;
    public ProfilesOverFragment() {
        // Required empty public constructor
    }

    public static ProfilesOverFragment newInstance() {
        ProfilesOverFragment fragment = new ProfilesOverFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profiles_over, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        goProfile = view.findViewById(R.id.goProfile);
        goProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to menu
            }
        });
    }
}
