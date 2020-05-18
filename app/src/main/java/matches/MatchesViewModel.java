package matches;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

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

 /*    public LiveData<ArrayList<UserModel>> getData() {
        if (matchesMutableLiveData == null) {
            matchesMutableLiveData = new MutableLiveData<>();
            loadData();
        }
        return matchesMutableLiveData;
    }

    private void loadData() {
        Network.
    }

    public MatchesViewModel(@NonNull Application application) {
        super(application);
        matchesMutableLiveData.setValue(matches);
    }


   static class MatchesInfo {
        public List<UserModel> matches;
        public MatchesInfo(List<UserModel> matches){
            this.matches = matches;
        }
    } */
}
