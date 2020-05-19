package com.example.first.matches;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.first.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    FirebaseStorage storage;
    StorageReference storageRef;
    final long BATCH_SIZE = 1024 * 1024;

    private ArrayList<UserModel> matches;
    public MyAdapter(ArrayList<UserModel> matches) {
        this.matches = matches;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_matches_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.itemView.setTag(matches.get(position));
        UserModel user = matches.get(position);
        holder.nameView.setText(user.name);
        if (user.seen.equals("false")) holder.newMatch.setVisibility(View.VISIBLE);

        setPhoto(holder,position);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        CardView cardView;
        ImageView photoView;
        LinearLayout newMatch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name);
            cardView = itemView.findViewById(R.id.card_view);
            photoView = itemView.findViewById(R.id.photo);
            newMatch = itemView.findViewById(R.id.newMatch); }
        }

        public void setPhoto(final ViewHolder holder, int position) {
            UserModel user = matches.get(position);
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            StorageReference myRef = storageRef.child("Profiles").child(user.id).child("AvatarImage");
            myRef.getBytes(BATCH_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    holder.photoView.setImageBitmap(bmp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });

        }

    }

