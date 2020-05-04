package com.example.first.mainScreen;

import android.app.Application;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

public class MainViewModel extends AndroidViewModel {
    private MediatorLiveData<DataProfile> currentUser = new MediatorLiveData<DataProfile>();
    private boolean isProfile;

    public MainViewModel(@NonNull Application application) {
        super(application);
        isProfile = false;
        swipe(ConstValue.DEFAULT);
    }

    public LiveData<DataProfile> getProfile() {
        return currentUser;
    }

    public void swipe(int side) {
        if (!isProfile)
            side = ConstValue.DEFAULT;
        final LiveData<InfRepo.UserInformation> informationLiveData = InfRepo.getInstance(getApplication()).swipe(side);

        currentUser.addSource(informationLiveData, new Observer<InfRepo.UserInformation>() {
            @Override
            public void onChanged(InfRepo.UserInformation userInformation) {
                String infUser = new String();
                Bitmap bitmap;

                if (userInformation == null) {
                    infUser = "Default";
                    bitmap = null;
                    isProfile = false;
                }
                else {
                    isProfile = true;

                    if (userInformation.profile.getName() != null)
                        infUser += userInformation.profile.getName();
                    if (userInformation.profile.getAge() != null)
                        infUser += userInformation.profile.getAge();
                    if (userInformation.profile.getCity() != null)
                        infUser += userInformation.profile.getCity();

                    bitmap = userInformation.bitmap;
                }

                currentUser.postValue(new DataProfile(infUser,bitmap));
                currentUser.removeSource(informationLiveData);
            }
        });
    }

    class DataProfile {
        private String infProfile;
        private Bitmap mainImageUser;

        public DataProfile(String infProfile, Bitmap mainImageUser) {
            this.infProfile = infProfile;
            this.mainImageUser = mainImageUser;
        }

        public String getInfProfile() {
            return infProfile;
        }

        public void setInfProfile(String infProfile) {
            this.infProfile = infProfile;
        }

        public Bitmap getMainImageUser() {
            return mainImageUser;
        }
    }
}
