package im.zego.livedemo.feature.live.viewmodel;

import java.util.List;

import im.zego.live.model.ZegoRoomInfo;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;

/**
 * Created by rocket_wang on 2021/12/29.
 */
public interface ILiveRoomViewModelListener {
    void onReceiveRoomInfoUpdate(ZegoRoomInfo roomInfo);

    void onConnectionStateChanged(ZIMConnectionState state, ZIMConnectionEvent event);

    void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, List<ZegoStream> streamList);

    // receive add co-host invitation
    void onReceiveAddCoHostInvitation(String operateUserID);

    // receive add co-host invitation respond
    void onReceiveAddCoHostRespond(String userID, boolean accept);

    // receive request to co-host request
    void onReceiveToCoHostRequest(String requestUserID);

    // receive cancel  request to co-host
    void onReceiveCancelToCoHostRequest(String requestUserID);

    // receive response to  request to co-host
    void onReceiveToCoHostRespond(boolean agree);
}
