package com.attitudetech.pawsroom.socketio;

import com.attitudetech.pawsroom.socketio.listener.OnGpsDataReceivedListener;
import com.attitudetech.pawsroom.socketio.model.SocketIoPetInfo;
import com.attitudetech.pawsroom.socketio.model.SocketState;
import com.attitudetech.pawsroom.util.IOScheduler;
import com.attitudetech.pawsroom.util.RxUtil;

import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

import static com.attitudetech.pawsroom.socketio.SocketManager.GPS_UPDATES;
import static com.attitudetech.pawsroom.socketio.SocketManager.ROOMJOIN;

public class SocketIOService2 {

    private SocketManager socketManager;
    private static SocketIOService2 INSTANCE;

    private CompositeDisposable compositeDisposable;
    //Key clientsName Value rooms
    private Map<String, List<String>> clientsByRoom;

    private SocketIOService2() {
        socketManager = new SocketManager();
        compositeDisposable = new CompositeDisposable();
    }

    public static SocketIOService2 getInstance(){
        if (INSTANCE == null){
            INSTANCE = new SocketIOService2();
        }
        return INSTANCE;
    }

    public Flowable<SocketIoPetInfo> startListenSocketIO(String clientName, List<String> rooms){
        return addRoom(clientName, rooms.get(0));
    }

    private Flowable<SocketIoPetInfo> addRoom(String clientName, String room){
        return authenticate()
                .map(socketState -> room)
                .filter(s -> !isRoomAvailableForAnotherClient(clientName, s))
                .toFlowable(BackpressureStrategy.BUFFER)
                .compose(applyGetSocketIOFlowableTranformerForOnePet());
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
                .compose(IOScheduler.instance());
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
