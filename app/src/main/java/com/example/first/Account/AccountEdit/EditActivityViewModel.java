package com.example.first.Account.AccountEdit;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.first.Account.AccountCache;
import com.example.first.Account.Repositories.RepoDB;
import com.example.first.Profile;

public class EditActivityViewModel extends AndroidViewModel {
    private static final String TAG = "EditAccountActivity";
    private final static int NAME_MAX_LENGTH = 12;

    private MutableLiveData<ValidationStatus> mValidationState = new MutableLiveData<>();
    private MediatorLiveData<EditActivityRepo.AvatarImage> userProfileImage = new MediatorLiveData<>();
    private MediatorLiveData<EditActivityRepo.ProfileInfo> userProfileInfo = new MediatorLiveData<>();

    private final LiveData<EditActivityRepo.AvatarImage> UserImage;
    private final LiveData<EditActivityRepo.ProfileInfo> UserInfo;

    public EditActivityViewModel(@NonNull Application application) {
        super(application);

        UserImage = EditActivityRepo.getInstance().getUserImage();
        UserInfo = EditActivityRepo.getInstance().getUserInfo();
    }

    LiveData<ValidationStatus> getProgress() {
        return mValidationState;
    }

    LiveData<EditActivityRepo.AvatarImage> getAvatarImage() {
        return userProfileImage;
    }

    LiveData<EditActivityRepo.ProfileInfo> getProfileInfo() {
        return userProfileInfo;
    }

    void uploadProfileData(EditActivityRepo.ProfileInfo profileInfo) {
        // data validation
        if (!profileInfo.getAge().equals("")) {
            try {
                Integer.parseInt(profileInfo.getAge());
            } catch (NumberFormatException nfe) {
                mValidationState.setValue(ValidationStatus.AGE_FAILURE);
                return;
            }
        }

        if (profileInfo.getName().length() > NAME_MAX_LENGTH){
            mValidationState.setValue(ValidationStatus.NAME_FAILURE);
            return;
        }

        // EditActivityRepo.getInstance().uploadProfileData(profileInfo);
        AccountCache.getInstance(getApplication())
                .getRepo()
                .setProfile(profileInfo, new RepoDB.CallbackUpload() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "CallbackUpload onSuccess");
            }

            @Override
            public void Error() {
                Log.d(TAG, "CallbackUpload Error");
            }
        });

        mValidationState.setValue(ValidationStatus.SUCCESS);
    }

    public enum ValidationStatus {
        NONE,
        SUCCESS,
        DEFAULT_FAILURE,
        NAME_FAILURE,
        AGE_FAILURE,
    }

    void uploadAvatarImage(Bitmap bitmap) {
        // EditActivityRepo.getInstance().updateAvatarImageCashe(bitmap);
        // EditActivityRepo.getInstance().uploadAvatarImage();

        AccountCache.getInstance(getApplication())
                .getRepo()
                .setAvatarImage(new EditActivityRepo.AvatarImage(bitmap), new RepoDB.CallbackUpload() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "CallbackUpload onSuccess");
            }

            @Override
            public void Error() {
                Log.d(TAG, "CallbackUpload Error");
            }
        });
    }


    void getData() {
        Log.d(TAG, "getData()");
        // EditActivityRepo.getInstance().getData();

        AccountCache.getInstance(getApplication()).getRepo().getProfile(new RepoDB.CallbackProfile() {
            @Override
            public void onSuccess(Profile profile) {
                Log.d(TAG, "CallbackProfile onSuccess ");
                userProfileInfo.setValue(new EditActivityRepo.ProfileInfo(profile));
            }

            @Override
            public void notFound() {
                Log.d(TAG, "CallbackProfile: notFound");
            }

            @Override
            public void Error() {
                Log.d(TAG, "CallbackProfile: Error");
            }
        });

        AccountCache.getInstance(getApplication()).getRepo().getImage(new RepoDB.CallbackImage() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                Log.d(TAG, "CallbackImage onSuccess ");
                userProfileImage.setValue(new EditActivityRepo.AvatarImage(bitmap));
            }

            @Override
            public void notFound() {
                Log.d(TAG, "CallbackImage: notFound");
            }

            @Override
            public void Error() {
                Log.d(TAG, "CallbackImage: Error");
            }
        });

    }



    void subscribeRepoData() {
        userProfileImage.addSource(UserImage, new Observer<EditActivityRepo.AvatarImage>() {
            @Override
            public void onChanged(EditActivityRepo.AvatarImage avatarImage) {
                Log.d(TAG, "userProfileImage.addSource onChanged() in getData()");
                userProfileImage.setValue(avatarImage);
            }
        });
        userProfileInfo.addSource(UserInfo, new Observer<EditActivityRepo.ProfileInfo>() {
            @Override
            public void onChanged(EditActivityRepo.ProfileInfo profileInfo) {
                Log.d(TAG, "userProfileInfo.addSource onChanged()");
                userProfileInfo.setValue(profileInfo);
            }
        });
    }

    void unsubscribeRepoData() {
        userProfileImage.removeSource(UserImage);
        userProfileInfo.removeSource(UserInfo);
    }

}
