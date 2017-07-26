package com.attitudetech.pawsroom.dataBase.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class CurrentPositionEntity implements Parcelable {

    public String id;
    public double lat;
    public double lon;

    public CurrentPositionEntity() {
    }

    protected CurrentPositionEntity(Parcel in) {
        id = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CurrentPositionEntity> CREATOR = new Creator<CurrentPositionEntity>() {
        @Override
        public CurrentPositionEntity createFromParcel(Parcel in) {
            return new CurrentPositionEntity(in);
        }

        @Override
        public CurrentPositionEntity[] newArray(int size) {
            return new CurrentPositionEntity[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrentPositionEntity that = (CurrentPositionEntity) o;

        if (Double.compare(that.lat, lat) != 0) return false;
        if (Double.compare(that.lon, lon) != 0) return false;
        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        temp = Double.doubleToLongBits(lat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
