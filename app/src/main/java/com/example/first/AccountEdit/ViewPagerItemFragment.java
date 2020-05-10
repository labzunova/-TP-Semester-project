package com.example.first.AccountEdit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.first.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

public class ViewPagerItemFragment extends Fragment {
    private static final String FRAGMENT_TAG = "PictureBrowseFragTag";

    private StorageReference imageRef;
    private ImageView image;

    public ViewPagerItemFragment() { }

    public ViewPagerItemFragment(StorageReference imageRef) {
        this.imageRef = imageRef;
    }

    public static ViewPagerItemFragment getInstance(StorageReference imageRef) {
        ViewPagerItemFragment fragment = null;
        if (imageRef != null) {
            fragment = new ViewPagerItemFragment(imageRef);
        }

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d(FRAGMENT_TAG, "getArguments() != null in onCreate() itemFragment");
            if (getArguments().getParcelable("imageRef") != null) {
                Log.d(FRAGMENT_TAG, "getParcelable() != null in onCreate() itemFragment");
                imageRef = getArguments().getParcelable("imageRef");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_pager_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        image = (ImageView) view.findViewById(R.id.imageItemPager);

        init();
    }

    private void init() {
        if (imageRef != null) {
            final long ONE_MEGABYTE = 1024 * 1024;
            Log.d(FRAGMENT_TAG, "imageRef != null in init() itemFragment");
            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image.setImageBitmap(AccountEditActivity.resizeBitmap(bitmap, 500.0f));
                    Log.d(FRAGMENT_TAG, "getButes onSuccess() listener in itemFragment: " + imageRef);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(FRAGMENT_TAG, "getButes ONFAILURE() listener in itemFragment: " + imageRef);
                }
            });
        }
    }
}
