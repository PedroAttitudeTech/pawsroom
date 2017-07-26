package com.attitudetech.pawsroom.socketio.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AuthenticationCheck implements Parcelable {

    public static final int WAITING_RESULT = 2;
    public static final int AUTHENTICATED_RESULT = 1;

    public final int error;
    public final int result;

    public AuthenticationCheck(int error, int result) {
        this.error = error;
        this.result = result;
    }

    protected AuthenticationCheck(Parcel in) {
        error = in.readInt();
        result = in.readInt();
    }

    public static final Creator<AuthenticationCheck> CREATOR = new Creator<AuthenticationCheck>() {
        @Override
        public AuthenticationCheck createFromParcel(Parcel in) {
            return new AuthenticationCheck(in);
        }

        @Override
        public AuthenticationCheck[] newArray(int size) {
            return new AuthenticationCheck[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(error);
        dest.writeInt(result);
    }
}
