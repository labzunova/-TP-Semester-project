package com.example.first.Account.Repositories;

import androidx.lifecycle.LiveData;

public interface RepoDB {
     LiveData getProfile();
     LiveData getImage();
     void exit();
}
