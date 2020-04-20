package com.salekur.bachelor.classes;

public class Contacts
{
    public String first_name, last_name, about, profile_image, phone_number;

    public Contacts()
    {


    }

    public Contacts(String first_name, String last_name, String about, String profile_image, String phone_number) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.about = about;
        this.profile_image = profile_image;
        this.phone_number = phone_number;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
