package com.example.first;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static android.widget.GridLayout.HORIZONTAL;
import static android.widget.GridLayout.LEFT;
import static android.widget.GridLayout.RIGHT;

public class MainActivity extends AppCompatActivity implements MainActivityService.ProfileListener {
    public static final String INF = "information";
    public static final String INFORMATION_PROCESS = "informationProgress";
    private static final String SIGNAL_NEW_FRAGMENT = "signalNewFragment";

    private TextView textName;
    private Button nextFragment;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;

    private String nameDog, idDog;
    private String nameMyDog, idMyDog;
    private ArrayList<String> listLikes, listMatches;
    private Map<String, String> allDogs;
    private MainActivityService mainService;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        nextFragment = findViewById(R.id.nextFragment);
        intent = new Intent(this, MainActivityService.class);

        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mainService = ((MainActivityService.MyBinder)service).getService();
                mainService.listenEvents(MainActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);

        Profile userProfile = new Profile();
        userProfile.setName("Test");
        userProfile.setAge("7");
        userProfile.setCity("Moscow");

        final DogFragment firstFragment = DogFragment.newInstance(R.drawable.dog_example2, userProfile);

        if (savedInstanceState == null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dogFragment, firstFragment)
                    .commit();


        }

        // -----------init------------------
        /*mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();*/


        /*listData = new ArrayList<Data>();
        adapter = new MyAdapter(listData);
        listData.add(new Data(R.drawable.dog));
        RecyclerView list = findViewById(R.id.recycle);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(list);*/


        //listLikes = new ArrayList<>();



        // ------------listeners--------------


        /*if (user != null) {
            DatabaseReference childRef = myRef.child("ProfilesSergei").child(user.getUid()).child("seen");
            childRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // draw pictures


                    for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {
                        idDog = snapshotNode.getKey();
                    }
                    //GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                    //listLikes = dataSnapshot.child("likes").getValue(t);

                    //Log.d(INF, listLikes.get(1));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/

            /*myRef.child("ProfilesSergei").child(user.getUid()).child("likes")
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                    //listLikes = dataSnapshot.getValue(t);

                    for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {
                        if (listLikes.indexOf(snapshotNode.getKey()) == -1)
                            listLikes.add(snapshotNode.getKey());
                    }

                    Log.d(INF, Integer.toString(listLikes.size()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/

            /*myRef.child("ProfilesSergei").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {
                      //  allDogs.put(snapshotNode.getKey(), snapshotNode.child("name").getValue(String.class));
                    //}
                    GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {
                    };
                    allDogs = dataSnapshot.getValue(t);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/
        //}
        // --------------------metods----------------
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void newProfile(Profile profile) {
        if (profile != null) {
            DogFragment newFragment = DogFragment.newInstance(R.drawable.dog_example2, profile);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dogFragment, newFragment)
                    .commit();
        }
        else {

        }
    }

    /*class AllDog {
        private String name;

        public AllDog() {};
        public AllDog(String name) {
            this.name = name;
        }

        public String setName() {
            return name;
        }
    }

    private void nextDog() {
        if (user != null) {
            idDog = dogs.get(1);
            //textName.setText(Integer.toString(allDogs.size()));
        }
    }*/






    /*class MyAdapter extends RecyclerView.Adapter<MyHolder> {
        private ArrayList<Data> listDataAdapter;

        public MyAdapter(ArrayList<Data> listDataAdapter) {
            this.listDataAdapter = listDataAdapter;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_dog, parent, false);
            return new MyHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            holder.imgView.setImageResource(listDataAdapter.get(position).imgId);
        }

        @Override
        public int getItemCount() {
            return listDataAdapter.size();
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView imgView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.imageMain);
        }
    }

    class Data {
        int imgId;


        public Data(int imgId) {
            this.imgId = imgId;
        }
    }*/



    /*ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            if(swipeDir == ItemTouchHelper.LEFT) {
                if(listLikes.indexOf(idDog) != -1) {
                    Log.d("information", "Yes");

                    myRef.child("ProfilesSergei").child(user.getUid()).child("matches").child(idDog).setValue("1");
                    myRef.child("ProfilesSergei").child(idDog).child("matches").child(user.getUid()).setValue("1");
                }
                else {
                    Log.d(INF, "No");

                    myRef.child("ProfilesSergei").child(idDog).child("likes").child(user.getUid()).setValue("1");
                }
            }
            else {

            }

            if(listLikes.indexOf(idDog) != -1) {
                myRef.child("ProfilesSergei").child(user.getUid()).child("likes").child(idDog).removeValue();
                listLikes.remove(listLikes.indexOf(idDog));
            }

            listData.add(new Data(R.drawable.dog));
            listData.remove(viewHolder.getAdapterPosition());
            myRef.child("ProfilesSergei").child(user.getUid()).child("seen").child(idDog).removeValue();
            adapter.notifyDataSetChanged();
        }
    };*/

}
