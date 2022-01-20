package im.zego.live.listener;

import im.zego.live.model.OperationAction;
import im.zego.live.model.ZegoRoomInfo;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;

/**
 * Created by rocket_wang on 2021/12/14.
 */
public interface ZegoRoomServiceListener {
    // room info update
    void onReceiveRoomInfoUpdate(ZegoRoomInfo roomInfo);

    void onReceiveCoHostListUpdate(OperationAction action);

    void onConnectionStateChanged(ZIMConnectionState state, ZIMConnectionEvent event);

}