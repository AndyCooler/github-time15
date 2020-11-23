package com.mythosapps.time15;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mythosapps.time15.types.KindOfDay;

public class SharedViewModel extends ViewModel {

    // selected / current kind of day in MainActivity
    MutableLiveData<KindOfDay> kindOfDay;

    public void selectKindOfDay(KindOfDay item) {
        kindOfDay.setValue(item);
    }

    public LiveData<KindOfDay> getSelected() {
        return kindOfDay;
    }

}
