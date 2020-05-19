package com.example.first.matches;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class MatchesViewModel extends ViewModel {

    MutableLiveData<ArrayList<UserModel>> matchesMutableLiveData;

    public void init(){
        if (matchesMutableLiveData != null){
            return;
        }
        matchesMutableLiveData = Repository.getInstance().getMatches();
    }

    public LiveData<ArrayList<UserModel>> getLiveData(){
        return matchesMutableLiveData;
    }

}
