package com.mythosapps.time15;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mythosapps.time15.types.KindOfDay;

/**
 * ViewModel saves the state of the day's data presented in MainActivity as well as in other
 * dialogs that mostly require only the ID. As it's live data linked to UI elements,
 * this eliminates the need for complex modelToView / viewToModel methods. UI elements listen
 * for changes which are represented on the UI and saved to the data store immediately.
 */
public class SharedViewModel extends ViewModel {

    // selected / current kind of day in MainActivity
    MutableLiveData<KindOfDay> kindOfDay = new MutableLiveData<>(KindOfDay.WORKDAY);

    public void selectKindOfDay(KindOfDay item) {
        kindOfDay.setValue(item);
    }

    public LiveData<KindOfDay> getSelected() {
        return kindOfDay;
    }

}
