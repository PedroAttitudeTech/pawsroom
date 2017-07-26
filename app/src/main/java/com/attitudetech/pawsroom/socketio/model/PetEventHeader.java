package com.attitudetech.pawsroom.socketio.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PetEventHeader implements Parcelable {

    public int id;
    public String name;
    @SerializedName("img_url")
    public String imgUrl;

    public PetEventHeader() {
    }

    protected PetEventHeader(Parcel in) {
        id = in.readInt();
        name = in.readString();
        imgUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(imgUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PetEventHeader> CREATOR = new Creator<PetEventHeader>() {
        @Override
        public PetEventHeader createFromParcel(Parcel in) {
            return new PetEventHeader(in);
        }

        @Override
        public PetEventHeader[] newArray(int size) {
            return new PetEventHeader[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetEventHeader)) return false;

        PetEventHeader petEventHeader = (PetEventHeader) o;

        if (id != petEventHeader.id) return false;
        if (name != null ? !name.equals(petEventHeader.name) : petEventHeader.name != null) return false;
        return imgUrl != null ? imgUrl.equals(petEventHeader.imgUrl) : petEventHeader.imgUrl == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (imgUrl != null ? imgUrl.hashCode() : 0);
        return result;
    }
}
