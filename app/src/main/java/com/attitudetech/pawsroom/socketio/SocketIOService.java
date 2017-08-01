package com.attitudetech.pawsroom.socketio;

import android.util.Log;

import com.attitudetech.pawsroom.repository.PetRepository;
import com.attitudetech.pawsroom.socketio.model.SocketIoPetInfo;
import com.attitudetech.pawsroom.socketio.model.SocketState;
import com.attitudetech.pawsroom.util.IOScheduler;
import com.attitudetech.pawsroom.util.RxUtil;

import org.reactivestreams.Publisher;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class SocketIOService{

    //Key clientsName Value rooms
    private static SocketIOService INSTANCE;

    private SocketIOService() {}

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
                .flatMap(new Function<SocketState, Publisher<? extends List<String>>>() {
                    @Override
                    public Publisher<? extends List<String>> apply(SocketState socketState) throws Exception {
                        return new PetRepository().getAllPetId();
                    }
                })
                .compose(applyGetSocketIOFlowableTransformer())
                .compose(RxUtil.applyFlowableSchedulers());
    }

    /**
     * call this method to transform a Flowable which emits a petList to multiple Flowables. For each
     * new Flowable we listen for gps updates
     * @return
     */
    public FlowableTransformer<List<String>, SocketIoPetInfo> applyGetSocketIOFlowableTransformer() {
        return upstream -> upstream
                .switchMap(new Function<List<String>, Publisher<? extends List<String>>>() {
                    @Override
                    public Publisher<? extends List<String>> apply(List<String> item) throws Exception {
                        return Flowable.just(item);
                    }
                })
                .flatMapIterable(petList1 -> petList1)
                .distinct(id -> id)
                .compose(applyGetSocketIOFlowableTranformerForOnePet());
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
                .compose(IOScheduler.instance());
    }

    private FlowableTransformer<String, SocketIoPetInfo> applyGetSocketIOFlowableTranformerForOnePet() {
        return upstream -> upstream
                .flatMap(this::getSocketPetInfoFlowable)
                .filter(SocketIoPetInfo::hasNoError);
    }

    private Flowable<SocketIoPetInfo> getSocketPetInfoFlowable(String petId) {
        return SocketManager.instance().on(petId);
    }
}