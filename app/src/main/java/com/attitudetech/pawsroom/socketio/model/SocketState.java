package com.attitudetech.pawsroom.socketio.model;

public enum SocketState {

    INITIAL,
    CONNECTED,
    WAITING_AUTHORIZATION,
    AUTHENTICATED,
    DISCONNECTED;


    public static SocketState getSocketStateFrom(AuthenticationCheck authenticationCheck) {

        switch (authenticationCheck.result) {
            case AuthenticationCheck.WAITING_RESULT:
                return WAITING_AUTHORIZATION;
            case AuthenticationCheck.AUTHENTICATED_RESULT:
                return AUTHENTICATED;
            default:
                return INITIAL;
        }
    }
}
