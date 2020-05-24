package com.example.first.AccountEdit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.first.R;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PictureBrowser extends Fragment {
    private static final String FRAGMENT_TAG = "PictureBrowseFragTag";

    private List<StorageReference> imagesRefs;
    private int position;

    private ViewPager imagePager;
    private ImagesPagerAdapter imagePagerAdapter;

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

        imagePager = view.findViewById(R.id.imagePager);

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
    public void onDestroyView() {
        // imagePagerAdapter = null;
        super.onDestroyView();

    }
}
