package com.example.first.mainScreen;

import android.app.Application;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

public class MainViewModel extends AndroidViewModel {
    private static final String DEFAULT_INFO = "Default";

    private MediatorLiveData<DataProfile> currentUser = new MediatorLiveData<>();
    private LiveData<InfRepo.UserInformation> informationLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        informationLiveData = InfRepo.getInstance(getApplication()).firstProfile();
        currentUser.addSource(informationLiveData, observer);
    }

    public LiveData<DataProfile> getProfile() {
        return currentUser;
    }

    public void swipeLeft() {
        informationLiveData = InfRepo.getInstance(getApplication()).swipeLeft();
        currentUser.addSource(informationLiveData, observer);
    }

    public void swipeRight() {
        informationLiveData = InfRepo.getInstance(getApplication()).swipeRight();
        currentUser.addSource(informationLiveData, observer);
    }

    private Observer<InfRepo.UserInformation> observer = new Observer<InfRepo.UserInformation>() {
        @Override
        public void onChanged(InfRepo.UserInformation userInformation) {
            String infoUser = "";
            Bitmap bitmap;

            if ((userInformation == null) || (userInformation.profile == null)) {
                infoUser = DEFAULT_INFO;
                bitmap = null;
            }
            else {
                if (userInformation.profile.getName() != null)
                    infoUser += userInformation.profile.getName();
                if (userInformation.profile.getAge() != null)
                    infoUser += userInformation.profile.getAge();
                if (userInformation.profile.getCity() != null)
                    infoUser += userInformation.profile.getCity();

                bitmap = userInformation.bitmap;
            }

            currentUser.postValue(new DataProfile(infoUser, bitmap));
            currentUser.removeSource(informationLiveData);
        }
    };

    static class DataProfile {
        private String infoProfile;
        private Bitmap mainImageUser;

        public DataProfile(String infoProfile, Bitmap mainImageUser) {
            this.infoProfile = infoProfile;
            this.mainImageUser = mainImageUser;
        }

        public String getInfProfile() {
            return infoProfile;
        }

        public void setInfProfile(String infProfile) {
            this.infoProfile = infoProfile;
        }

        public Bitmap getMainImageUser() {
            return mainImageUser;
        }
    }
}
