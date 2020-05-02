package com.example.first;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PictureBrowser extends Fragment {
    private static final String FRAGMENT_TAG = "PictureBrowseFragTag";

    private List<StorageReference> imagesRefs;
    private int position;

    private Activity activity;
    private ViewPager imagePager;
    private ImagesPagerAdapter imagePagerAdapter;
    private ImageView image;

    public PictureBrowser() { }

    public PictureBrowser(List<StorageReference> imagesRefs, int position) {
        this.imagesRefs = imagesRefs;
        this.position = position;
    }

    public static PictureBrowser newInstance(List<StorageReference> imagesRefs, int position) {
        PictureBrowser fragment = new PictureBrowser(imagesRefs, position);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture_browser, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imagePager = (ViewPager) view.findViewById(R.id.imagePager);

        ArrayList<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < imagesRefs.size(); i++) {
            ViewPagerItemFragment fragment = ViewPagerItemFragment.getInstance(imagesRefs.get(i));
            fragments.add(fragment);
        }
        imagePagerAdapter = new ImagesPagerAdapter(getChildFragmentManager(), fragments);

        imagePager.setAdapter(imagePagerAdapter);
        //imagePager.setOffscreenPageLimit(3);
        imagePager.setCurrentItem(position); //displaying the image at the current position

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity)context;
        }
    }


}
