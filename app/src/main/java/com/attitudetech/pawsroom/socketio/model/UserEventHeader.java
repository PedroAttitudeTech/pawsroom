package com.attitudetech.pawsroom.socketio.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserEventHeader implements Parcelable {
    public int id;
    public String name;
    public String surname;

    @SerializedName("image_url")
    public String imageUrl;
    public String email;//optional

    protected UserEventHeader(Parcel in) {
        id = in.readInt();
        name = in.readString();
        surname = in.readString();
        imageUrl = in.readString();
        email = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(imageUrl);
        dest.writeString(email);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserEventHeader> CREATOR = new Creator<UserEventHeader>() {
        @Override
        public UserEventHeader createFromParcel(Parcel in) {
            return new UserEventHeader(in);
        }

        @Override
        public UserEventHeader[] newArray(int size) {
            return new UserEventHeader[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEventHeader)) return false;

        UserEventHeader guest = (UserEventHeader) o;

        if (id != guest.id) return false;
        if (name != null ? !name.equals(guest.name) : guest.name != null) return false;
        if (surname != null ? !surname.equals(guest.surname) : guest.surname != null) return false;
        if (imageUrl != null ? !imageUrl.equals(guest.imageUrl) : guest.imageUrl != null)
            return false;
        return email != null ? email.equals(guest.email) : guest.email == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}