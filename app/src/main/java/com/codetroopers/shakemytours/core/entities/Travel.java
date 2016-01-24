package com.codetroopers.shakemytours.core.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import com.codetroopers.shakemytours.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import timber.log.Timber;

public class Travel implements Parcelable {

    @SerializedName("name")
    public String name;
    @SerializedName("distance")
    public String distance;
    @SerializedName("tel")
    public String telephone;
    @SerializedName("site")
    public String siteWeb;
    @SerializedName("address")
    public String address1;
    public boolean selected;
    @DrawableRes
    public int backgroundImage;
    public boolean loading;
    @SerializedName("lat")
    public String latitude;
    @SerializedName("lgt")
    public String longitude;
    @SerializedName("eventType")
    public String eventType;
    @SerializedName("description")
    public String description;


    public Travel() {
    }


    protected Travel(Parcel in) {
        name = in.readString();
        distance = in.readString();
        telephone = in.readString();
        siteWeb = in.readString();
        address1 = in.readString();
        selected = in.readByte() != 0;
        backgroundImage = in.readInt();
        loading = in.readByte() != 0;
        latitude = in.readString();
        longitude = in.readString();
        eventType = in.readString();
        description = in.readString();
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

    public Travel setTelephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public Travel setBackground(@DrawableRes int resId) {
        this.backgroundImage = resId;
        return this;
    }

    public int getResourceId() {
        Timber.i("resource for " + name);
        switch (eventType) {
            case "resto":
                return R.drawable.vin;
            case "bar":
                return R.drawable.bar;
            case "musee":
                return R.drawable.musee;
            case "spectacle":
                return R.drawable.spectacle;
            case "nature":
                return R.drawable.nature;
            case "eglise":
                return R.drawable.eglise;
            case "chateau":
                return R.drawable.chateau;
            case "zoo":
                return R.drawable.zoo;
            case "orange":
                return R.drawable.orange;
            case "inextenso":
            case "enigma":
            case "cesr":
            case "cci":
            default:
                return R.drawable.smt_default;
        }
    }


    public String getAddress() {
        return address1;
    }

    public Travel setCoords(String lat, String lng) {
        this.latitude = lat;
        this.longitude = lng;
        return this;
    }

    public LatLng toLatLng() {
        return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(distance);
        dest.writeString(telephone);
        dest.writeString(siteWeb);
        dest.writeString(address1);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeInt(backgroundImage);
        dest.writeByte((byte) (loading ? 1 : 0));
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(eventType);
        dest.writeString(description);
    }


    public static class Coord implements Serializable {
        public String lng;
        public String lat;

        //required for jackson to unmarshall
        public Coord() {
        }

        public Coord(final String lng, final String lat) {
            this.lng = lng;
            this.lat = lat;
        }

        @Override
        public String toString() {
            return lat + "," + lng;
        }

        public LatLng toLatLng() {
            return new LatLng(Double.valueOf(lat), Double.valueOf(lng));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(lng, lat);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final Coord other = (Coord) obj;
            return Objects.equal(this.lng, other.lng) && Objects.equal(this.lat, other.lat);
        }
    }
}
