package com.example.first.mainScreen;
import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.first.ApplicationModified;
import com.example.first.Profile;
import com.example.first.mainScreen.Storage.Database;

import java.util.ArrayList;

public class InfRepo implements Database.InfListener {
    private Database database;
    private TransportCase newCase;

    private TransportCase myUserCase;
    private ArrayList<String> idUsers;
    private MediatorLiveData<TransportCase> liveDataRepo;

    public InfRepo(Database database) {
        idUsers = new ArrayList<>();
        myUserCase = new TransportCase();
        newCase = null;

        this.database = database;
        database.Connect(this);
    }

    @Override
    public void setMyProfile(TransportCase userCase) {
        this.myUserCase = userCase;
    }

    @Override
    public void setIdProfiles(ArrayList<String> idUsers) {
        this.idUsers = idUsers;
    }


    @NonNull
    public static InfRepo getInstance(Context context) {
        return ApplicationModified.from(context).getInfRepo();
    }


    public LiveData<TransportCase> getFirstCase() {
        liveDataRepo = new MediatorLiveData<>();

        if (newCase == null)
            newTransportCase();
        else
            liveDataRepo.setValue(newCase);

        return liveDataRepo;
    }

    public LiveData<TransportCase> getCase() {
        liveDataRepo = new MediatorLiveData<>();

        newTransportCase();

        return liveDataRepo;
    }

    private void newTransportCase() {
        TransportCase lastCase = newCase;
        TransportCase myLocalCase = myUserCase;
        newCase = null;

        ArrayList<String> seen = new ArrayList<>();
        if ((myLocalCase != null) && (myLocalCase.profile.getSeen() != null))
            seen = myLocalCase.profile.getSeen();

        if (lastCase != null) {
            seen.add(lastCase.id);
            myUserCase.profile.setSeen(seen);
            database.addSeenById(myLocalCase.id, lastCase.id);
        }

        int i = 0;
        while (i < idUsers.size() && (newCase == null)) {
            if (seen.indexOf(idUsers.get(i)) == -1) {
                newCase = new TransportCase();
                newCase.id = idUsers.get(i);
            }
            i++;
        }

        if (newCase == null)
            liveDataRepo.setValue(null);
        else {
            final LiveData<InfRepo.TransportCase> liveDataStorage = database.getNewProfile(newCase.id);

            liveDataRepo.addSource(liveDataStorage, new Observer<TransportCase>() {
                @Override
                public void onChanged(TransportCase CaseDatabase) {
                    newCase = CaseDatabase;
                    liveDataRepo.postValue(newCase);
                    liveDataRepo.removeSource(liveDataStorage);
                }
            });
        }
    }

    public void processInformation() {
        TransportCase lastUserInf = newCase;
        TransportCase myLocalCase = myUserCase;

        if (lastUserInf == null)
            return;

        if ((myLocalCase.profile.getLikes() == null) || (myLocalCase.profile.getLikes().indexOf(lastUserInf.id) == -1)) {

            database.addLikeById(lastUserInf.id, myLocalCase.id);
        }
        else {
            database.removeLike(myLocalCase.id, lastUserInf.id);

            Profile.Matches myMatches = new Profile.Matches(myLocalCase.id, myLocalCase.name, "false");
            database.addMatchesById(myLocalCase.id, myMatches);

            Profile.Matches youMatches = new Profile.Matches(myLocalCase.id, myLocalCase.name, "false");
            database.addMatchesById(lastUserInf.id, youMatches);
        }
    }


    public static class TransportCase {
        public Profile profile;
        public Bitmap bitmap;
        public String name;
        public String id;

        public TransportCase() {
            profile = null;
            bitmap = null;
            name = null;
            id = null;
        }
    }
}
