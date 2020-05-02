package com.example.first;

import com.google.firebase.storage.StorageReference;

import java.util.List;

public interface itemClickListener {
    void onPicClicked(List<StorageReference> imagesRefs, int position);
}

