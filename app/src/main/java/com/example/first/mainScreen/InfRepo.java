package com.example.first.mainScreen;
import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.first.ApplicationModified;
import com.example.first.Profile;
import com.example.first.mainScreen.Storage.NetworkData;

import java.util.ArrayList;

public class InfRepo implements NetworkData.InfListener {
    private NetworkData networkData;
    private UserInformation newUserInf;
    private UserInformation lastUserInf;
    private UserInformation myUser;
    private ArrayList<String> idProfiles;

    public InfRepo(NetworkData networkData) {
        idProfiles = new ArrayList<>();
        myUser = new UserInformation();
        newUserInf = new UserInformation();
        lastUserInf = new UserInformation();

        this.networkData = networkData;
        networkData.Connect(this);
    }

    @Override
    public void setMyProfile(UserInformation user) {
        this.myUser = user;
    }

    @Override
    public void setIdProfiles(ArrayList<String> idProfiles) {
        this.idProfiles = idProfiles;
    }


    @NonNull
    public static InfRepo getInstance(Context context) {
        return ApplicationModified.from(context).getInfRepo();
    }

    private MediatorLiveData<UserInformation> userInf;


    public LiveData<UserInformation> swipe(int side) {
        userInf = new MediatorLiveData<>();
        lastUserInf = newUserInf;

        if (side == ConstValue.SIDE_LEFT) {
            newUserInformation();
        }
        else if (side == ConstValue.SIDE_RIGHT) {
            swipeRight();
            newUserInformation();
        }
        else if (side == ConstValue.DEFAULT) {
            if (newUserInf.profile == null)
                newUserInformation();
            else {
                userInf.postValue(newUserInf);
            }

        }

        // TODO go local storage

        return userInf;
    }

    private void newUserInformation() {
        newUserInf = new UserInformation();
        ArrayList<String> seen = new ArrayList<>();
        if ((myUser != null) && (myUser.profile.getSeen() != null))
            seen = myUser.profile.getSeen();

        seen.add(lastUserInf.id);

        networkData.addSeenById(myUser.id, lastUserInf.id);
        int i = 0;
        while (i < idProfiles.size() && (newUserInf.id == null)) {
            if (seen.indexOf(idProfiles.get(i)) == -1) {
                newUserInf.id = idProfiles.get(i);
            }
            i++;
        }

        if (newUserInf.id == null)
            userInf.postValue(null);
        else {
            final LiveData<InfRepo.UserInformation> newInf = networkData.getNewProfile(newUserInf.id);

            userInf.addSource(newInf, new Observer<UserInformation>() {
                @Override
                public void onChanged(UserInformation userInformation) {
                    newUserInf = userInformation;
                    userInf.postValue(newUserInf);
                    userInf.removeSource(newInf);
                }
            });
        }
    }

    private void swipeRight() {
        if ((myUser.profile.getLikes() == null) || (myUser.profile.getLikes().indexOf(lastUserInf.id) == -1)) {

            networkData.addLikeById(lastUserInf.id, myUser.id);
        }
        else {
            networkData.removeLike(myUser.id, lastUserInf.id);

            Profile.Matches myMatch = new Profile.Matches(lastUserInf.id, lastUserInf.name, "false");
            networkData.addMatchesById(myUser.id, myMatch);

            Profile.Matches youMatch = new Profile.Matches(myUser.id, myUser.name, "false");
            networkData.addMatchesById(lastUserInf.id, youMatch);
        }
    }


    public static class UserInformation {
        public Profile profile;
        public Bitmap bitmap;
        public String name;
        public String id;

        public UserInformation() {
            profile = null;
            bitmap = null;
            name = null;
            id = null;
        }
    }
}
