package com.vedantsuram.civiladvocacyapp;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.Map;

public class Official implements Serializable {
    public String name;
    private String office;
    public String party;
    public String address;
    private String phoneNumber;
    private String email;
    private String website;
    public String photo;
    private Map<String,String> socialMedia;

    public Official(String name, String office, String party, String address, String phoneNumber, String email, String website, String photo, Map<String, String> socialMedia) {
        this.name = name;
        this.office = office;
        this.party = party;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
        this.photo = photo;
        this.socialMedia = socialMedia;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Map<String, String> getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(Map<String, String> socialMedia) {
        this.socialMedia = socialMedia;
    }
}
