package com.example.saveourwoman;

import java.util.ArrayList;
import java.util.List;

public class ContactListViewModel {
    public List<ContactViewModel> contacts;

    public ContactListViewModel()
    {
        //Empty Constructor
    }

    public ContactListViewModel(List<ContactViewModel> contacts)
    {
        this.contacts = contacts;
    }

}
