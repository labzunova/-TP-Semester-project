package com.example.first.mainScreen.Storage;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.first.Profile;
import com.example.first.mainScreen.InfRepo;

import java.util.ArrayList;

public interface Database {
    void Connect(@NonNull final InfListener repo);
    LiveData<InfRepo.TransportCase> getNewProfile(@NonNull final String idProfile);
    void addSeenById (@NonNull final String id, final String newSeen);
    void addLikeById(@NonNull final String id, final String newLike);
    void addMatchesById (@NonNull final String id, final Profile.Matches newMatch);
    void removeLike (@NonNull final String id, final String removeLike);

    interface InfListener {
        void setMyProfile(InfRepo.TransportCase user);
        void setIdProfiles(ArrayList<String> idProfiles);
    }
}
