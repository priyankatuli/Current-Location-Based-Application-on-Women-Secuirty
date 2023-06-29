package com.example.saveourwoman;

import androidx.lifecycle.ViewModel;

import com.google.type.DateTime;

public class ContactViewModel {
    public String name;
    public String phone;

    public String createdOn;

    public ContactViewModel()
    {

    }

    public ContactViewModel(String name, String phone, String createdOn)
    {
        this.name = name;
        this.phone = phone;
        this.createdOn = createdOn;
    }

}
