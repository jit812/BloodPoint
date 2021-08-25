package com.android.blooddonor.bloodbank.viewmodels;


import androidx.annotation.NonNull;

import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.database.annotations.Nullable;

public class UserData {

    private String Name, Email, Contact, Address, Division,Locality;
    private int Gender, BloodGroup;

    public UserData() {

    }
    public String getContact() {
        return Contact;
    }
    public void setContact(@NotNull String contact) {
        Contact = contact;
    }
    public String getAddress() {
        return Address;
    }
    public void setAddress(@NotNull String address) {
        this.Address = address;
    }
    public String getDivision() {
        return Division;
    }
    public void setDivision(@NotNull String division) {
        this.Division = division;
    }
    public String  getLocality() {
        return Locality;
    }
    public void  setLocality(@NotNull String locality) {
        this.Locality = locality;
    }
    public String getName() {
        return Name;
    }
    public int getBloodGroup() {
        return BloodGroup;
    }
    public int getGender() {
        return Gender;
    }
    public String getEmail() {
        return Email;
    }
    public void setBloodGroup(@NotNull int bloodGroup) {
        this.BloodGroup = bloodGroup;
    }
    public void setName(@NonNull String name) { this.Name = name; }

    public void setEmail(@NotNull String email) {
        this.Email = email;
    }

    public void setGender(@NotNull int gender) {
        this.Gender = gender;
    }


}
