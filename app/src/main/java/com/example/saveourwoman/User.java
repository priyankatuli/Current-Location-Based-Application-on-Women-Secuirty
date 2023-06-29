package com.example.saveourwoman;

public class User {
    public String name;
    public String email;
    public String phone;
    public String dateOfBirth;
    public String age;

    public User()
    {

    }

    public User(String name, String email, String phone, String age, String dateOfBirth)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
    }

}
