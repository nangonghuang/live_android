package im.zego.livedemo.feature.room.viewmodel;

import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;

/**
 * Created by rocket_wang on 2021/12/29.
 */
public interface ILiveRoomViewModelListener {
    void onConnectionStateChanged(ZIMConnectionState state, ZIMConnectionEvent event);

    // receive add co-host invitation
    void onReceiveAddCoHostInvitation();

    // receive add co-host invitation respond
    void onReceiveAddCoHostRespond(boolean accept);

    // receive request to co-host request
    void onReceiveToCoHostRequest(String requestUserID);

    // receive cancel  request to co-host
    void onReceiveCancelToCoHostRequest(String requestUserID);

    // receive response to  request to co-host
    void onReceiveToCoHostRespond(boolean agree);
}
