package com.example.oteloxtfgdam.activity.ui.mercado;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MercadoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MercadoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is mercado fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}