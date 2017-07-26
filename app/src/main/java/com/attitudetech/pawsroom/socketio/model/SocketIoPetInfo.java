package com.attitudetech.pawsroom.socketio.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.attitudetech.pawsroom.dataBase.entity.CurrentPositionEntity;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class SocketIoPetInfo implements Parcelable {

    public int error;

    @SerializedName("petId")
    public String id;

    public int batteryLvl;
    public int networkLvl;

    public long time;
    public boolean isConnected;

    @SerializedName("location")
    public CurrentPositionEntity currentPositionEntity;

    protected SocketIoPetInfo(Parcel in) {
        error = in.readInt();
        id = in.readString();
        batteryLvl = in.readInt();
        networkLvl = in.readInt();
        time = in.readLong();
        isConnected = in.readByte() != 0;
        currentPositionEntity = in.readParcelable(CurrentPositionEntity.class.getClassLoader());
    }

    public SocketIoPetInfo() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(error);
        dest.writeString(id);
        dest.writeInt(batteryLvl);
        dest.writeInt(networkLvl);
        dest.writeLong(time);
        dest.writeByte((byte) (isConnected ? 1 : 0));
        dest.writeParcelable(currentPositionEntity, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SocketIoPetInfo> CREATOR = new Creator<SocketIoPetInfo>() {
        @Override
        public SocketIoPetInfo createFromParcel(Parcel in) {
            return new SocketIoPetInfo(in);
        }

        @Override
        public SocketIoPetInfo[] newArray(int size) {
            return new SocketIoPetInfo[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SocketIoPetInfo that = (SocketIoPetInfo) o;

        if (error != that.error) return false;
        if (batteryLvl != that.batteryLvl) return false;
        if (networkLvl != that.networkLvl) return false;
        if (time != that.time) return false;
        if (isConnected != that.isConnected) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return currentPositionEntity != null ? currentPositionEntity.equals(that.currentPositionEntity) : that.currentPositionEntity == null;

    }

    @Override
    public int hashCode() {
        int result = error;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + batteryLvl;
        result = 31 * result + networkLvl;
        result = 31 * result + (int) (time ^ (time >>> 32));
        result = 31 * result + (isConnected ? 1 : 0);
        result = 31 * result + (currentPositionEntity != null ? currentPositionEntity.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SocketIoPetInfo{" +
                "error=" + error +
                ", id='" + id + '\'' +
                ", batteryLvl=" + batteryLvl +
                ", networkLvl=" + networkLvl +
                ", time='" + time + '\'' +
                ", isConnected=" + isConnected +
                ", currentPosition=" + currentPositionEntity +
                '}';
    }

    public boolean hasNoError() {
        return error == 0;
    }

    public static LatLng getLatLngFromCurrentPosition(CurrentPositionEntity currentPositionEntity) {
        return new LatLng(currentPositionEntity.lat, currentPositionEntity.lon);
    }
}
