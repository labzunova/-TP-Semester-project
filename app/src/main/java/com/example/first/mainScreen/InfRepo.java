package com.example.first.mainScreen;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.first.ApplicationModified;
import com.example.first.Profile;
import com.example.first.mainScreen.Storage.NetworkData;

import java.util.ArrayList;

public class InfRepo {
    private NetworkData networkData;
    private MainProfileInf newUserInf;
    private MainProfileInf lastUserInf;
    private Profile myUser;
    private ArrayList<String> idProfiles;

    public InfRepo() {
        idProfiles = new ArrayList<>();
        myUser = null;

        this.networkData = new NetworkData(this);
    }

    public void setMyProfile(Profile profile) {
        this.myUser = profile;
    }

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
        if (lastUserInf == null)
            lastUserInf = new MainProfileInf();

        if (side == ConstValue.SIDE_LEFT) {
            swipeRLeft();
        }
        else if (side == ConstValue.SIDE_RIGHT) {
            swipeRight();
        }
        else if (side == ConstValue.DEFAULT) {
            swipeDefault();
        }

        // TODO go local storage

        newUserInformation();

        return userInf;
    }

    private void newUserInformation() {
        newUserInf = new MainProfileInf();
        ArrayList<String> seen = new ArrayList<>();
        if ((myUser != null) && (myUser.getSeen() != null))
            seen = myUser.getSeen();

        seen.add(lastUserInf.getId());

        networkData.addSeenById(null, lastUserInf.getId());
        int i = 0;
        while (i < idProfiles.size() && (newUserInf.getId() == null)) {
            if (seen.indexOf(idProfiles.get(i)) == -1) {
                newUserInf.setId(idProfiles.get(i));
            }
            i++;
        }

        if (newUserInf == null)
            userInf.postValue(null);
        else {
            final LiveData<InfRepo.UserInformation> newInf = networkData.getNewProfile(newUserInf.getId());

            userInf.addSource(newInf, new Observer<UserInformation>() {
                @Override
                public void onChanged(UserInformation userInformation) {
                    newUserInf.setName(userInformation.getProfile().getName());

                    userInf.postValue(userInformation);
                    userInf.removeSource(newInf);
                }
            });
        }
    }

    private void swipeRight() {
        if (myUser.getLikes().indexOf(lastUserInf.getId()) == -1) {

            networkData.addLikeById(lastUserInf.getId());
        }
        else {
            networkData.removeLike(null, lastUserInf.getId());

            networkData.addMatchesById(null, lastUserInf);
            MainProfileInf mInf = new MainProfileInf();
            mInf.setName(myUser.getName());
            networkData.addMatchesById(lastUserInf.getId(), mInf);
        }
    }

    private void swipeRLeft() {

    }

    private void swipeDefault() {

    }


    public static class UserInformation {
        private Profile profile;
        private Bitmap bitmap;

        public UserInformation() {}
        public UserInformation(Profile profile, Bitmap bitmap) {
            this.profile = profile;
            this.bitmap = bitmap;
        }

        public Profile getProfile() {
            return profile;
        }

        public void setProfile(Profile profile) {
            this.profile = profile;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }

    public static class MainProfileInf {
        private String name;
        private String id;

        MainProfileInf (String name, String id) {
            this.name = name;
            this.id = id;
        }

        public MainProfileInf() {
            this.name = null;
            this.id = null;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
