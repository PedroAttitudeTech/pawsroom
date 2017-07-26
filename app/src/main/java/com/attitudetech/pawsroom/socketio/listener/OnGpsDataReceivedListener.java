package com.attitudetech.pawsroom.socketio.listener;

import android.util.Log;

import com.attitudetech.pawsroom.socketio.model.SocketIoPetInfo;
import com.google.gson.Gson;

import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class OnGpsDataReceivedListener extends SocketListener<SocketIoPetInfo> implements Emitter.Listener {

    private static final String TAG = "OnGpsDataReceivedListen";

    @Override
    public void call(Object... args) {
        Log.e(TAG, args[0].toString());
        if (args.length > 0) {
            emitter.onNext(convert((JSONObject) args[0]));
        }
    }
    private SocketIoPetInfo convert(JSONObject object){
        return new Gson().fromJson(object.toString(), SocketIoPetInfo.class);
    }
}
