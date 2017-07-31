package com.attitudetech.pawsroom.socketio;

import android.util.Log;

import com.attitudetech.pawsroom.socketio.listener.OnGpsDataReceivedListener;
import com.attitudetech.pawsroom.socketio.model.SocketIoPetInfo;
import com.attitudetech.pawsroom.socketio.model.SocketState;
import com.attitudetech.pawsroom.util.RxUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;

/**
 * Created by phrc on 7/20/17.
 */

public class SocketIOService{

    //Key clientsName Value rooms
    private Map<String, List<String>> clientsByRoom;
    private static SocketIOService INSTANCE;

    private SocketIOService() {
        clientsByRoom = new HashMap<>();
    }

    public static SocketIOService getInstance(){
        if (INSTANCE == null){
            INSTANCE = new SocketIOService();
        }
        return INSTANCE;
    }

    public Flowable<SocketIoPetInfo> startListenSocketIO(String clientName, List<String> rooms){
        Log.e("SocketIO", "Start Listen");

        return  authenticate()
                .toFlowable(BackpressureStrategy.BUFFER)
                .map(socketState -> rooms)
                .flatMapIterable(strings -> strings)
                .flatMap(s -> addRoom(clientName, s))
                .compose(RxUtil.applyFlowableSchedulers());

    }

    public Completable stopListenSocketIO(String clientName){
        Log.e("SocketIO", "Stop Listen");

        return Observable
                    .fromIterable(
                            (clientsByRoom.containsKey(clientName)) ?
                                    clientsByRoom.get(clientName) :
                                    new ArrayList<>())
                    .filter(s -> !isRoomAvailableForAnotherClient(clientName, s))
                    .flatMapCompletable(this::removeRoom)
                    .doOnComplete(() -> clientsByRoom.remove(clientName))
                    .compose(RxUtil.applyCompletableSchedulers());
    }

    private Flowable<SocketIoPetInfo> addRoom(String clientName, String room){
        Log.e("SocketIO", "addRoom "+room);
        return authenticate()
                .map(socketState -> room)
                .filter(s -> !isRoomAlreadyAvailable(s))
                .map(s -> registerRoomOnClient(clientName, room))
                .toFlowable(BackpressureStrategy.BUFFER)
                .compose(applyGetSocketIOFlowableTranformerForOnePet());
    }

    private String registerRoomOnClient(String client, String room){
        if (!clientsByRoom.containsKey(client)){
            List<String> rooms = new ArrayList<>();
            rooms.add(room);
            clientsByRoom.put(client, rooms);
        }
        else {
            clientsByRoom.get(client).add(room);
        }
        return room;
    }

    private Completable removeRoom(String room){
        return  SocketManager.instance().off(SocketManager.GPS_UPDATES + room);
    }

    private boolean isRoomAlreadyAvailable(String room){
        for (String key : clientsByRoom.keySet()){
            if (clientsByRoom.get(key).contains(room)){
                Log.e("SocketIO", "room available");
                return true;
            }
        }
        Log.e("SocketIO", "room not available");
        return false;
    }

    private boolean isRoomAvailableForAnotherClient(String clientName, String room){
        for (String key : clientsByRoom.keySet()){
            if (!key.equals(clientName) &&
                    clientsByRoom.get(key).contains(room)){
                Log.e("SocketIO", "room available");
                return true;
            }
        }
        Log.e("SocketIO", "room not available");
        return false;
    }

    /**
     * call this method to create an Observable which will first try to connect to socket IO server
     * and then authenticate. Use this before any other call to Socket IO
     *
     * @return an observable which only emits a value if SocketState == AUTHENTICATED
     */
    private Observable<SocketState> authenticate() {
        return SocketManager
                .instance()
                .connect()
                .filter(SocketState.AUTHENTICATED::equals)
                .compose(RxUtil.applyObservableSchedulers());
    }

    private FlowableTransformer<String, SocketIoPetInfo> applyGetSocketIOFlowableTranformerForOnePet() {
        return upstream -> upstream
                .map(this::joinRoomFor)
                .flatMap(this::getSocketPetInfoFlowable)
                .filter(SocketIoPetInfo::hasNoError)
                .compose(RxUtil.applyFlowableSchedulers());
    }

    private String joinRoomFor(String petId) {
        SocketManager
                .instance()
                .emit(SocketManager.ROOMJOIN, petId);
        return petId;
    }

    private Flowable<SocketIoPetInfo> getSocketPetInfoFlowable(String petId) {
        return SocketManager.instance().on(SocketManager.GPS_UPDATES + petId, new OnGpsDataReceivedListener());
    }


}
