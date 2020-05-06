package com.example.first;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {
    private static final String FRAGMENT_TAG = "GalleryFragmentTag";
    private static final int VERTICAL_SPAN_COUNT = 4;
    private static final int RESULT_OK = -1;
    private static final String ITEM_ADD_IMAGE = "Open chooser";

    private Activity activity;
    private RecyclerView previewImages;
    private List<StorageReference> galleryList;

    private FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(FRAGMENT_TAG, "onCreateView()");
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(FRAGMENT_TAG, "onViewCreated()");

        // views init
        previewImages = view.findViewById(R.id.photoPreviewList);

        // firebase init
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("Profiles").child(user.getUid());



        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                Log.d(FRAGMENT_TAG, "lastAll() onSuccess() called");
                galleryList = listResult.getItems();
                galleryList.add(0, null);
                previewImages.setAdapter(new previewImagesAdapter(galleryList, savedInstanceState));
                }
            });

        previewImages.setLayoutManager(new GridLayoutManager(view.getContext(), VERTICAL_SPAN_COUNT, RecyclerView.VERTICAL, false));

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity)context;
        }
    }

    class previewImagesAdapter extends RecyclerView.Adapter<previewImagesHolder> {

        List<StorageReference> imagesRefs;
        Bundle savedInstanceState;

        public previewImagesAdapter(List<StorageReference> imagesRefs, Bundle savedInstanceState) {
            this.imagesRefs = imagesRefs;
            this.savedInstanceState = savedInstanceState;
        }

        @NonNull
        @Override
        public previewImagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_preview, parent, false);
            return new previewImagesHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final previewImagesHolder holder, final int position) {
            final long ONE_MEGABYTE = 1024 * 1024;
            StorageReference imageRef = imagesRefs.get(position);
            if (imageRef != null) {
                imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.image.setImageBitmap(AccountEditActivity.resizeBitmap(bitmap, 200.0f));
                        Log.d(FRAGMENT_TAG, "getButes onSuccess() listener on element " + position);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity.getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
                    }
                });



                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(FRAGMENT_TAG, "item image onClickListener()");
                        // implement image scaling
                        ((itemClickListener) activity).onPicClicked(imagesRefs, position);
                    }
                });
            } else {
                Log.d(FRAGMENT_TAG, "Chooser element of the list");

                holder.image.setImageResource(R.drawable.unnamed);

                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select picture"), AccountEditActivity.PICK_IMAGE_REQUEST);
                        // When user chose photo - onActivityResult() is called
                    }
                });
            }
        }


        @Override
        public int getItemCount() {
            return imagesRefs.size();
        }
    }

    static class previewImagesHolder extends RecyclerView.ViewHolder {
        ImageView image;

        previewImagesHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(FRAGMENT_TAG, "View Holder constructor called successfully");

            image = (ImageView) itemView.findViewById(R.id.galleryListItemImage); // (ImageView)
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // upload to storage, add to list and update Recycler
        Log.d(FRAGMENT_TAG, "Fragment onActivityResult() started");

        // get Uri
        Uri filepath = null;
        if (requestCode == AccountEditActivity.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Log.d(FRAGMENT_TAG, "onActivityResult() condition is true, data.getData() called");
            filepath = data.getData();
        }

        // upload to storage
        StorageReference imageRef = null;
        if (filepath != null) {
            Log.d(FRAGMENT_TAG, "filepath != null");
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading..");
            progressDialog.show();

            imageRef = storageRef.child(System.currentTimeMillis() + "GalleryImg");

            byte[] bytes = compressImageFromDevise(filepath);

            // ref.putFile(filepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            final StorageReference finalImageRef = imageRef;
            imageRef.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                    // add to list and update Recycler
                    galleryList.add(1, finalImageRef);
                    previewImages.getAdapter().notifyItemInserted(1);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int)progress + "%");
                        }
                    });
        } else {
            Log.d(FRAGMENT_TAG, "filepath is null");
        }


        // getAdapter().notifyItemInserted(itemList.size()-1);

    }

    private byte[] compressImageFromDevise(Uri filepath) {
        byte[] bytes = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), filepath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // requlate quality to change size of image, uploading to firebase storage
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            bytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bytes == null)
            Log.d(FRAGMENT_TAG, "bytes is null in compressImageFromDevise()");

        return bytes;
    }
}
