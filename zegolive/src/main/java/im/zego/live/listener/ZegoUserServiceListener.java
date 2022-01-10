package im.zego.live.listener;

import java.util.List;

import im.zego.live.model.ZegoUserInfo;

/**
 * Created by rocket_wang on 2021/12/14.
 */
public interface ZegoUserServiceListener {

    // receive user join room command
    void onRoomUserJoin(List<ZegoUserInfo> memberList);

    // receive user leave room command
    void onRoomUserLeave(List<ZegoUserInfo> memberList);

    // receive add co-host invitation
    void onReceiveAddCoHostInvitation(String operateUserID);

    // receive add co-host invitation respond
    void onReceiveAddCoHostRespond(boolean accept);

    // receive request to co-host request
    void onReceiveToCoHostRequest(String requestUserID);

    // receive cancel  request to co-host
    void onReceiveCancelToCoHostRequest(String requestUserID);

    // receive response to  request to co-host
    void onReceiveToCoHostRespond(boolean agree);
}