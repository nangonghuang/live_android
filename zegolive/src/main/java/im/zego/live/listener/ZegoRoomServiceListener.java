package im.zego.live.listener;

import java.util.List;

import im.zego.live.model.ZegoRoomInfo;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;

/**
 * Created by rocket_wang on 2021/12/14.
 */
public interface ZegoRoomServiceListener {
    // room info update
    void onReceiveRoomInfoUpdate(ZegoRoomInfo roomInfo);

    void onConnectionStateChanged(ZIMConnectionState state, ZIMConnectionEvent event);

    void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, List<ZegoStream> streamList);
}