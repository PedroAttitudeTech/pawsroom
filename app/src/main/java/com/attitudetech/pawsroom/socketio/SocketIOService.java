package com.attitudetech.pawsroom.socketio;

import android.util.Log;

import com.attitudetech.pawsroom.socketio.listener.OnGpsDataReceivedListener;
import com.attitudetech.pawsroom.socketio.model.SocketIoPetInfo;
import com.attitudetech.pawsroom.socketio.model.SocketState;
import com.attitudetech.pawsroom.util.RxUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by phrc on 7/20/17.
 */

public class SocketIOService{

    //Key clientsName Value rooms
    private Map<String, List<String>> clientsByRoom;
    //Key room Value Disposable
    private Map<String, Disposable> disposables;
    private static SocketIOService INSTANCE;

    private SocketIOService() {
        clientsByRoom = new HashMap<>();
        disposables = new HashMap<>();
    }

    public static SocketIOService getInstance(){
        if (INSTANCE == null){
            INSTANCE = new SocketIOService();
        }
        return INSTANCE;
    }

    public void startListenSocketIO(String clientName, List<String> rooms){
        Log.e("SocketIO", "startListenSocketIO");
        if (clientsByRoom.containsKey(clientName)){
            List<String> clientRoom  = clientsByRoom.get(clientName);
            for (String room : rooms) {
                if (!clientRoom.contains(room)){
                    addRoom(clientName, room);
                    clientRoom.remove(room);
                }
            }
            if (!clientRoom.isEmpty()){
                for (String room : clientRoom){
                    removeRoom(clientName, room);
                }
            }
            return;
        }

        clientsByRoom.put(clientName, rooms);
        for (String room: rooms) {
            addRoom(clientName, room);
        }
    }

    public void stopListenSocketIO(String clientName){
        if (clientsByRoom.containsKey(clientName)){
            for (String room : clientsByRoom.get(clientName)) {
                removeRoom(clientName, room);
            }
        }
    }

    private void addRoom(String clientName, String room){
        Log.e("SocketIO", "AddRoom");
        if (isRoomAvailableForAnotherClient(clientName, room)){
            return;
        }
        Flowable<String> flowable;
        if (!SocketManager.instance().isAuth()){
            flowable = authenticate()
                    .toFlowable(BackpressureStrategy.BUFFER)
                    .map(socketState -> room);

        }
        else {
            flowable = Flowable.just(room);
        }

        Disposable disposable= flowable
                .compose(applyGetSocketIOFlowableTranformerForOnePet())
                .subscribe(socketIoPetInfo -> {
                    Log.e("Update", socketIoPetInfo.id);
                });

        disposables.put(room, disposable);

        Log.e("SocketIO", "disposableCreated");

    }

    private void removeRoom(String clientName, String room){
        Log.e("SocketIO", "removeRoom");
        if (disposables.containsKey(room)
                && !isRoomAvailableForAnotherClient(clientName, room)){
            disposables.get(room).dispose();
            Log.e("SocketIO", "room removed");
        }
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
