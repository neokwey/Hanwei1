package com.example.demo_demo.ui.user_history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserHistoryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public UserHistoryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is user_history fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}