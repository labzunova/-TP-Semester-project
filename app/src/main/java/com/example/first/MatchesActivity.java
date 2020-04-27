package com.example.first;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private RecyclerView matchesRecycler;
    private matchesAdapter adapter;
    private List<UserModel> result; // matches array
    FirebaseStorage storage;
    StorageReference storageRef;
    final long ONE_MEGABYTE = 1024 * 1024;

    private TextView noMatches;

    public static class UserModel implements Serializable {
        public String key, name, seen;
        public UserModel(){}
        public UserModel(String key,  String name, String seen) {
            this.key = key;
            this.name = name;
            this.seen = seen;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        noMatches = (TextView)findViewById(R.id.noMatches);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        result = new ArrayList<>();

        matchesRecycler = (RecyclerView)findViewById(R.id.matchesRecycler);
        matchesRecycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new matchesAdapter(result);
        matchesRecycler.setAdapter(adapter);

        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("Profiles").child(user.getUid()).child("matches");

        UpdateList();

    }

    private void UpdateList(){
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                result.add(dataSnapshot.getValue(UserModel.class));
                adapter.notifyDataSetChanged();
                checkEmpty();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserModel model = dataSnapshot.getValue(UserModel.class);
                int index = getItemIndex(model);
                result.set(index,model);
                adapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                UserModel model = dataSnapshot.getValue(UserModel.class);
                int index = getItemIndex(model);
                result.remove(index);
                adapter.notifyItemRemoved(index);
                checkEmpty();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    };

    private int getItemIndex(UserModel user){
        int index = -1;
        for(int i = 0; i< result.size(); i++) {
            if(result.get(i).key.equals(user.key)) {
                index = i;
                break;
            }
        }
        return index;
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) { // меню на удаление
        if (item.getItemId() == 0 ) removeUser(item.getGroupId());
        return super.onContextItemSelected(item);
    }

    public void removeUser(int position){
        mRef.child(result.get(position).key).removeValue();
    }

    class matchesAdapter extends RecyclerView.Adapter<myViewHolder> {

        private List<UserModel> matches; // for adapter

        matchesAdapter(List<UserModel> matches) {
            this.matches = matches;
        }

        @NonNull
        @Override
        public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_matches_item,parent, false);
            return new myViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final myViewHolder holder, int position) {

            UserModel user = matches.get(position);
            holder.nameView.setText(user.name);
            if (user.seen.equals("false")) holder.cardView.setBackgroundColor(Color.parseColor("#338A2B4B"));

            // photo adding
            StorageReference myRef = storageRef.child("Profiles").child(user.key).child("AvatarImage");
            myRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    holder.photoView.setImageBitmap(bmp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
                }
            });

            holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add(holder.getAdapterPosition(),0,0,"delete");
                }
            });
        }

        @Override
        public int getItemCount() {
            return matches.size();
        }
    }


    class myViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;
        CardView cardView;
        ImageView photoView;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name);
            cardView = itemView.findViewById(R.id.card_view);
            photoView = itemView.findViewById(R.id.photo);
        }
    }

    private void checkEmpty(){
        if (result.size() == 0) {
            matchesRecycler.setVisibility(View.INVISIBLE);
            noMatches.setVisibility(View.VISIBLE);
        } else {
            matchesRecycler.setVisibility(View.VISIBLE);
            noMatches.setVisibility(View.INVISIBLE);
        }
    }

}
