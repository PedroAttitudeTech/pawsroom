package com.attitudetech.pawsroom.socketio.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SocketIOEvent implements Parcelable {
    public static final int OWNER_REMOVED_PET = 1;
    public static final int OWNER_ADDED_USER = 2;
    public static final int OWNER_REMOVED_GUEST = 3;
    public static final int GUEST_LEFT_PET = 4;

    @SerializedName("eventid")
    public int id;

    public PetEventHeader pet;
    public UserEventHeader owner;
    public UserEventHeader guest;

    protected SocketIOEvent(Parcel in) {
        id = in.readInt();
        pet = in.readParcelable(PetEventHeader.class.getClassLoader());
        owner = in.readParcelable(UserEventHeader.class.getClassLoader());
        guest = in.readParcelable(UserEventHeader.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(pet, flags);
        dest.writeParcelable(owner, flags);
        dest.writeParcelable(guest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SocketIOEvent> CREATOR = new Creator<SocketIOEvent>() {
        @Override
        public SocketIOEvent createFromParcel(Parcel in) {
            return new SocketIOEvent(in);
        }

        @Override
        public SocketIOEvent[] newArray(int size) {
            return new SocketIOEvent[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SocketIOEvent)) return false;

        SocketIOEvent that = (SocketIOEvent) o;

        if (id != that.id) return false;
        if (pet != null ? !pet.equals(that.pet) : that.pet != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        return guest != null ? guest.equals(that.guest) : that.guest == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (pet != null ? pet.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (guest != null ? guest.hashCode() : 0);
        return result;
    }
}
