package im.zego.live.service;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.zego.live.ZegoRoomManager;
import im.zego.live.ZegoZIMManager;
import im.zego.live.callback.ZegoOnlineRoomUserListCallback;
import im.zego.live.callback.ZegoOnlineRoomUsersNumCallback;
import im.zego.live.callback.ZegoRoomCallback;
import im.zego.live.listener.ZegoUserServiceListener;
import im.zego.live.model.ZegoRoomInfo;
import im.zego.live.model.ZegoRoomUserRole;
import im.zego.live.model.ZegoUserInfo;
import im.zego.zim.ZIM;
import im.zego.zim.entity.ZIMUserInfo;
import im.zego.zim.enums.ZIMErrorCode;

/**
 * Created by rocket_wang on 2021/12/14.
 */
public class ZegoUserService {

    private static final String TAG = "ZegoUserService";

    public ZegoUserInfo localUserInfo;
    private ZegoUserServiceListener listener;
    // local login user info
    // room member list
    private final List<ZegoUserInfo> userList = new ArrayList<>();
    private final Map<String, ZegoUserInfo> userMap = new HashMap<>();

    // user login
    public void login(ZegoUserInfo userInfo, String token, final ZegoRoomCallback callback) {
        ZIMUserInfo zimUserInfo = new ZIMUserInfo();
        zimUserInfo.userID = userInfo.getUserID();
        zimUserInfo.userName = userInfo.getUserName();
        ZegoZIMManager.getInstance().zim.login(zimUserInfo, token, errorInfo -> {
            Log.d(TAG, "onLoggedIn() called with: errorInfo = [" + errorInfo.code + ", "
                + errorInfo.message + "]");
            if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                localUserInfo = new ZegoUserInfo();
                localUserInfo.setUserID(userInfo.getUserID());
                localUserInfo.setUserName(userInfo.getUserName());
            }
            if (callback != null) {
                callback.onRoomCallback(errorInfo.code.value());
            }
        });
    }

    // user logout
    public void logout() {
        Log.d(TAG, "logout() called");
        ZegoZIMManager.getInstance().zim.logout();
        leaveRoom();
    }

    void leaveRoom() {
        userList.clear();
        userMap.clear();
    }

    // get online room users list
    public void getOnlineRoomUsers(int page, ZegoOnlineRoomUserListCallback callback) {

    }

    // get online room users num
    public void getOnlineRoomUsersNum(ZegoOnlineRoomUsersNumCallback callback) {

    }

    // send an invitation message to add Co-Host
    public void addCoHostWithUserID(String userID, ZegoRoomCallback callback) {

    }

    // Respond to the co-host invitation
    public void respondCoHostInvitation(boolean accept, ZegoRoomCallback callback) {

    }

    // Request to co-host
    public void requestToCoHostWithCallback(ZegoRoomCallback callback) {

    }

    public void cancelRequestToCoHostWithCallback(ZegoRoomCallback callback) {

    }

    // Respond to the co-host request
    public void respondCoHostRequest(boolean accept, ZegoRoomCallback callback) {

    }

    // Prohibit turning on the camera microphone
    public void mute(String userID, ZegoRoomCallback callback) {

    }

    // Microphone operation
    public void micOperation(boolean open) {

    }

    // Camera operation
    public void cameraOperation(boolean open) {

    }

    // Remove co-host
    public void removeCoHostWithCallBack(ZegoRoomCallback callback) {

    }

    public void setListener(ZegoUserServiceListener listener) {
        this.listener = listener;
    }

    public void onRoomMemberJoined(ZIM zim, ArrayList<ZIMUserInfo> memberList, String roomID) {
        List<ZegoUserInfo> joinUsers = generateRoomUsers(memberList);
        userList.addAll(joinUsers);
        for (ZegoUserInfo joinUser : joinUsers) {
            userMap.put(joinUser.getUserID(), joinUser);
        }
        if (listener != null) {
            listener.onRoomUserJoin(joinUsers);
        }
    }

    public void onRoomMemberLeft(ZIM zim, ArrayList<ZIMUserInfo> memberList, String roomID) {
        List<ZegoUserInfo> leaveUsers = generateRoomUsers(memberList);
        userList.removeAll(leaveUsers);
        for (ZegoUserInfo leaveUser : leaveUsers) {
            userMap.remove(leaveUser.getUserID());
        }
        if (listener != null) {
            listener.onRoomUserLeave(leaveUsers);
        }
    }

    private List<ZegoUserInfo> generateRoomUsers(List<ZIMUserInfo> memberList) {
        ZegoRoomInfo roomInfo = ZegoRoomManager.getInstance().roomService.roomInfo;

        List<ZegoUserInfo> roomUsers = new ArrayList<>();
        for (ZIMUserInfo userInfo : memberList) {
            ZegoUserInfo roomUser = new ZegoUserInfo();
            roomUser.setUserID(userInfo.userID);
            roomUser.setUserName(userInfo.userName);

            if (userInfo.userID.equals(roomInfo.getHostID())) {
                roomUser.setRole(ZegoRoomUserRole.Host);
            } else {
                roomUser.setRole(ZegoRoomUserRole.Participant);
            }
            roomUsers.add(roomUser);
        }
        return roomUsers;
    }

    public List<ZegoUserInfo> getUserList() {
        return userList;
    }

    public String getUserName(String userID) {
        ZegoUserInfo zegoUserInfo = userMap.get(userID);
        if (zegoUserInfo != null) {
            return zegoUserInfo.getUserName();
        } else {
            return "";
        }
    }
}