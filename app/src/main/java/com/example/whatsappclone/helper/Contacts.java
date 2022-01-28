package com.example.whatsappclone.helper;

public class Contacts {
    public String name , status , image , uid ,timeUploaded,valid;
    public Contacts()
    {

    }

    public Contacts(String name, String status, String image, String uid, String timeUploaded, String valid) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.uid = uid;
        this.timeUploaded = timeUploaded;
        this.valid = valid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimeUploaded() {
        return timeUploaded;
    }

    public void setTimeUploaded(String timeUploaded) {
        this.timeUploaded = timeUploaded;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }
}
