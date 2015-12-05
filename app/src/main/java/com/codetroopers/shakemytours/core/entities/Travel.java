package com.codetroopers.shakemytours.core.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Travel implements Parcelable{

    public String name;
    public String distance;
    public String tarif;
    public boolean selected;


    public Travel() {
    }

    protected Travel(Parcel in) {
        name = in.readString();
        distance = in.readString();
        tarif = in.readString();
        selected = in.readByte() != 0;
    }

    public static final Creator<Travel> CREATOR = new Creator<Travel>() {
        @Override
        public Travel createFromParcel(Parcel in) {
            return new Travel(in);
        }

        @Override
        public Travel[] newArray(int size) {
            return new Travel[size];
        }
    };

    public Travel setName(String name) {
        this.name = name;
        return this;
    }

    public Travel setDistance(String distance) {
        this.distance = distance;
        return this;
    }

    public Travel setTarif(String tarif) {
        this.tarif = tarif;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(distance);
        dest.writeString(tarif);
        dest.writeByte((byte) (selected ? 1 : 0));
    }
}
