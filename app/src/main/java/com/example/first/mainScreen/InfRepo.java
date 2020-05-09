package com.example.first.mainScreen;
import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.first.ApplicationModified;
import com.example.first.Matches;
import com.example.first.Profile;
import com.example.first.mainScreen.Storage.NetworkData;

import java.util.ArrayList;

public class InfRepo implements NetworkData.InfListener {
    private NetworkData networkData;
    private UserInformation newUserInfo;

    private UserInformation myUser;
    private ArrayList<String> idProfiles;
    private MediatorLiveData<UserInformation> userInfo;

    public InfRepo(NetworkData networkData) {
        idProfiles = new ArrayList<>();
        myUser = new UserInformation();
        newUserInfo = null;

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


    public LiveData<UserInformation> firstProfile() {
        userInfo = new MediatorLiveData<>();

        if (newUserInfo == null)
            newUserInformation();
        else
            userInfo.setValue(newUserInfo);

        return userInfo;
    }

    private void newUserInformation() {
        UserInformation lastUserInf = newUserInfo;
        newUserInfo = null;

        ArrayList<String> seen = new ArrayList<>();
        if ((myUser != null) && (myUser.profile.getSeen() != null))
            seen = myUser.profile.getSeen();

        if (lastUserInf != null) {
            seen.add(lastUserInf.id);
            networkData.addSeenById(myUser.id, lastUserInf.id);
        }

        int i = 0;
        while (i < idProfiles.size() && (newUserInfo == null)) {
            if (seen.indexOf(idProfiles.get(i)) == -1) {
                newUserInfo = new UserInformation();
                newUserInfo.id = idProfiles.get(i);
            }
            i++;
        }

        if (newUserInfo == null)
            userInfo.setValue(null);
        else {
            final LiveData<InfRepo.UserInformation> newInf = networkData.getNewProfile(newUserInfo.id);

            userInfo.addSource(newInf, new Observer<UserInformation>() {
                @Override
                public void onChanged(UserInformation userInformation) {
                    newUserInfo = userInformation;
                    userInfo.postValue(newUserInfo);
                    userInfo.removeSource(newInf);
                }
            });
        }
    }

    public LiveData<UserInformation> swipeRight() {
        userInfo = new MediatorLiveData<>();
        UserInformation lastUserInf = newUserInfo;
        newUserInformation();

        if (lastUserInf == null)
            return userInfo;

        if ((myUser.profile.getLikes() == null) || (myUser.profile.getLikes().indexOf(lastUserInf.id) == -1)) {

            networkData.addLikeById(lastUserInf.id, myUser.id);
        }
        else {
            networkData.removeLike(myUser.id, lastUserInf.id);

            Matches myMatch = new Matches(lastUserInf.id, lastUserInf.name, "false");
            networkData.addMatchesById(myUser.id, myMatch);

            Matches youMatch = new Matches(myUser.id, myUser.name, "false");
            networkData.addMatchesById(lastUserInf.id, youMatch);
        }

        return userInfo;
    }

    public LiveData<UserInformation> swipeLeft() {
        userInfo = new MediatorLiveData<>();

        newUserInformation();
        return userInfo;
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
