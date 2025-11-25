package com.example.hikerdatabase.HikesEdit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hikerdatabase.HikesInfo.Hike;

public class HikeEditViewModel extends ViewModel {
    private final MutableLiveData<Hike> hikeDetailsData = new MutableLiveData<>();

    public void setHikeDetailsData (Hike hikeDetailsData) {
        this.hikeDetailsData.setValue(hikeDetailsData);
    }

    public LiveData<Hike> getHikeDetailsData() {
        return hikeDetailsData;
    }
}


