package com.acme.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 *  represent entity from ContactContent
 */
public class Contact implements Parcelable {

    private long id;
    private String name;
    private List<String> phones = new ArrayList<>();
    private List<String> mails = new ArrayList<>();

    public Contact() {}

    // конструктор, считывающий данные из Parcel
    private Contact(Parcel parcel) {
        id = parcel.readLong();
        name = parcel.readString();
        parcel.readStringList(phones);
        parcel.readStringList(mails);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public List<String> getMails() {
        return mails;
    }

    public void setMails(List<String> mails) {
        this.mails = mails;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", name:'" + name + '\'' +
                ", phones:" + phones +
                ", mails:" + mails +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeStringList(phones);
        parcel.writeStringList(mails);
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        // распаковываем объект из Parcel
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
