package com.example.first.AccountEdit;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class EditActivityViewModel extends AndroidViewModel {
    private static final String TAG = "EditAccountActivity";
    private final static int NAME_MAX_LENGTH = 15;

    private MutableLiveData<ValidationStatus> mValidationState = new MutableLiveData<>();
    private MediatorLiveData<AvatarImage> userProfileImage = new MediatorLiveData<>();
    private MediatorLiveData<ProfileInfo> userProfileInfo = new MediatorLiveData<>();

    public EditActivityViewModel(@NonNull Application application) {
        super(application);
        mValidationState.setValue(ValidationStatus.NONE);
    }

    // getProfile() returning liveData
    public LiveData<ValidationStatus> getProgress() {
        return mValidationState;
    }

    public LiveData<AvatarImage> getAvatarImage() {
        return userProfileImage;
    }

    public LiveData<ProfileInfo> getProfileInfo() {
        return userProfileInfo;
    }

    public void onDoneClicked(ProfileInfo profileInfo) {
        // data validation sample
        if (profileInfo.getName().length() > NAME_MAX_LENGTH){
            mValidationState.postValue(ValidationStatus.FAILURE);
        } else {
            mValidationState.postValue(ValidationStatus.SUCCESS);
            // request data-upload to firebase
            EditActivityRepo.getInstance().uploadProfileData(profileInfo);
        }
    }

    public void uploadAvatarImage(Bitmap bitmap) {
        final LiveData<AvatarImage> avatarImageLiveData = EditActivityRepo.getInstance().getUserImage();
        userProfileImage.addSource(avatarImageLiveData, new Observer<AvatarImage>() {
            @Override
            public void onChanged(AvatarImage avatarImage) {
                Log.d(TAG, "userProfileImage.addSource onChanged() in uploadAvatarImage()");
                userProfileImage.postValue(avatarImage);
                userProfileImage.removeSource(avatarImageLiveData);
            }
        });
        EditActivityRepo.getInstance().uploadAvatarImage(bitmap);
    }

    public void getData() {
        // запршиваем у Repo данные для UI
        final LiveData<AvatarImage> avatarImageLiveData = EditActivityRepo.getInstance().getUserImage();
        final LiveData<ProfileInfo> profileInfoLiveData = EditActivityRepo.getInstance().getUserInfo();
        userProfileImage.addSource(avatarImageLiveData, new Observer<AvatarImage>() {
            @Override
            public void onChanged(AvatarImage avatarImage) {
                Log.d(TAG, "userProfileImage.addSource onChanged() in getData()");
                userProfileImage.postValue(avatarImage);
                userProfileImage.removeSource(avatarImageLiveData);
            }
        });
        userProfileInfo.addSource(profileInfoLiveData, new Observer<ProfileInfo>() {
            @Override
            public void onChanged(ProfileInfo profileInfo) {
                Log.d(TAG, "userProfileInfo.addSource onChanged()");
                userProfileInfo.postValue(profileInfo);
                userProfileInfo.removeSource(profileInfoLiveData);
            }
        });

        Log.d(TAG, "getData: refreshUserCash()");
        EditActivityRepo.getInstance().refreshUserCash();
    }


    public enum ValidationStatus {
        NONE,
        FAILURE,
        SUCCESS
    }

    public static class AvatarImage {
        private Bitmap avatarBitmap;

        public AvatarImage(Bitmap avatarBitmap) {
            this.avatarBitmap = avatarBitmap;
        }

        public AvatarImage() {  }

        public Bitmap getAvatarBitmap() {
            return avatarBitmap;
        }

        public void setAvatarBitmap(Bitmap avatarBitmap) {
            this.avatarBitmap = avatarBitmap;
        }
    }


    public static class ProfileInfo {
        private String name;
        private String email;
        private String phone;
        private String breed;
        private String age;
        private String country;
        private String city;
        private String address;

        public ProfileInfo(String name, String email, String phone, String breed, String age, String country, String city, String address) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.breed = breed;
            this.age = age;
            this.country = country;
            this.city = city;
            this.address = address;
        }

        public ProfileInfo() {
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getBreed() {
            return breed;
        }

        public String getAge() {
            return age;
        }

        public String getCountry() {
            return country;
        }

        public String getCity() {
            return city;
        }

        public String getAddress() {
            return address;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setBreed(String breed) {
            this.breed = breed;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
