package com.salekur.bachelor.classes;

public class Users {
    // for users
    public String uid, first_name, last_name, email, phone_number, profile_image, about;

    public Users() {
    }

    public Users(String uid, String first_name, String last_name, String email, String phone_number, String profile_image, String about) {
        this.uid = uid;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone_number = phone_number;
        this.profile_image = profile_image;
        this.about = about;
    }
}
