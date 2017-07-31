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

import static com.attitudetech.pawsroom.socketio.SocketManager.GPS_UPDATES;
import static com.attitudetech.pawsroom.socketio.SocketManager.ROOMJOIN;

public class SocketIOService{

    //Key clientsName Value rooms
    private Map<String, List<String>> clientsByRoom;
    //Key room Value Disposable
    private Map<String, Disposable> disposables;
    private static SocketIOService INSTANCE;
    private SocketManager socketManager;

    private SocketIOService() {
        clientsByRoom = new HashMap<>();
        disposables = new HashMap<>();
        socketManager = new SocketManager();
    }

    public static SocketIOService getInstance(){
        if (INSTANCE == null){
            INSTANCE = new SocketIOService();
        }
        return INSTANCE;
    }

    public Flowable<SocketIoPetInfo> startListenSocketIO(String clientName, List<String> rooms){
        if (rooms.size() >= 1){
            return addRoom(clientName, rooms.get(0));
        }
        return Flowable.empty();

    }

    public void stopListenSocketIO(String clientName){
        if (clientsByRoom.containsKey(clientName)){
            for (String room : clientsByRoom.get(clientName)) {
                removeRoom(clientName, room);
            }
        }
    }

    private List<String> removeRooms(String clientName, List<String> rooms){
        return rooms;
    }

    private Flowable<SocketIoPetInfo> addRoom(String clientName, String room){
        return authenticate()
                .map(socketState -> room)
                .filter(s -> !isRoomAvailableForAnotherClient(clientName, s))
                .toFlowable(BackpressureStrategy.BUFFER)
                .compose(applyGetSocketIOFlowableTranformerForOnePet());
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
            if (!key.equals(client) &&
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
        return socketManager
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
        socketManager
                .emit(ROOMJOIN, petId);
        return petId;
    }

    private Flowable<SocketIoPetInfo> getSocketPetInfoFlowable(String petId) {
        return socketManager.on(GPS_UPDATES + petId, new OnGpsDataReceivedListener());
    }


}
