package matches;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Repository {

    static Repository instance;
    private ArrayList<UserModel> matches = new ArrayList<>();
    private MutableLiveData<ArrayList<UserModel>> dog = new MutableLiveData<>();


    public static Repository getInstance() {

        if(instance == null){
            instance = new Repository();
        }
        return instance;
    }

    public MutableLiveData<ArrayList<UserModel>> getMatches() {

        if (matches.size() == 0 ) loadMatches(); //or -1

        dog.setValue(matches);

        return dog;
    }

    private void loadMatches() {

        FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Profiles").child(user.getUid()).child("matches");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    matches.add(snapshot.getValue(UserModel.class));
                }
                dog.postValue(matches);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
