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
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;

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

        return  authenticate()
                .toFlowable(BackpressureStrategy.BUFFER)
                .map(socketState -> rooms)
                .flatMapIterable(strings -> strings)
                .flatMap(s -> addRoom(clientName, s))
                .compose(RxUtil.applyFlowableSchedulers());

    }

    public Completable stopListenSocketIO(String clientName){
            return Observable
                    .fromIterable(
                            (clientsByRoom.containsKey(clientName)) ?
                                    clientsByRoom.get(clientName) :
                                    new ArrayList<>())
                    .flatMapCompletable(string -> removeRoom(clientName, string))
                    .compose(RxUtil.applyCompletableSchedulers());
    }

    private Flowable<SocketIoPetInfo> addRoom(String clientName, String room){
        return authenticate()
                .map(socketState -> room)
                .filter(s -> !isRoomAvailableForAnotherClient(clientName, s))
                .toFlowable(BackpressureStrategy.BUFFER)
                .compose(applyGetSocketIOFlowableTranformerForOnePet());
    }

    private Completable removeRoom(String clientName, String room){
        Log.e("SocketIO", "removeRoom");
        if (!isRoomAvailableForAnotherClient(clientName, room)){
            Log.e("SocketIO", "room removed");
            return  SocketManager.instance().off(room);
        }
        return Completable.complete();
    }

    private boolean isRoomAvailableForAnotherClient(String client, String room){
        for (String key : clientsByRoom.keySet()){
            if (key != client &&
                    clientsByRoom.get(key).contains(room)){
                return true;
            }
        }
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
        return SocketManager.instance().on(SocketManager.GPS_UDPATES + petId, new OnGpsDataReceivedListener());
    }


}
