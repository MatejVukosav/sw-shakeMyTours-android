package com.codetroopers.shakemytours.core.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Travel implements Parcelable {

    @SerializedName("SyndicObjectName")
    public String name;
    @SerializedName("ObjectTypeName")
    public String distance;
    @SerializedName("DetailTel")
    public String telephone;
    @SerializedName("DetailSiteweb")
    public String siteWeb;
    @SerializedName("DetailAdresse1")
    public String address1;
    @SerializedName("DetailAdresse2")
    public String address2;
    @SerializedName("DetailCommune")
    public String city;
    @SerializedName("DetailCodePostal")
    public String zipCode;
    public boolean selected;
    @DrawableRes
    public int backgroundImage;
    public boolean loading;
    public String latitude;
    public String longitude;


    public Travel() {
    }


    protected Travel(Parcel in) {
        name = in.readString();
        distance = in.readString();
        telephone = in.readString();
        siteWeb = in.readString();
        address1 = in.readString();
        address2 = in.readString();
        city = in.readString();
        zipCode = in.readString();
        selected = in.readByte() != 0;
        backgroundImage = in.readInt();
        loading = in.readByte() != 0;
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
        dest.writeString(address2);
        dest.writeString(city);
        dest.writeString(zipCode);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeInt(backgroundImage);
        dest.writeByte((byte) (loading ? 1 : 0));
    }

    public String getAddress() {
        return address1 + "\n" + address2 + "\n" + zipCode + "\n" + city;
    }

    public Travel setCoords(String lat, String lng) {
        this.latitude = lat;
        this.longitude = lng;
        return this;
    }

    public LatLng toLatLng() {
        return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
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
