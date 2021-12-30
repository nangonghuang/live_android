package im.zego.live.helper;

import androidx.annotation.NonNull;

import im.zego.live.ZegoRoomManager;

/**
 * Created by rocket_wang on 2021/12/30.
 */
public class ZegoLiveHelper {
    @NonNull
    public static String getSelfStreamID() {
        String selfUserID = ZegoRoomManager.getInstance().userService.localUserInfo.getUserID();
        return getStreamID(selfUserID);
    }
    @NonNull
    public static String getStreamID(String userID) {
        String roomID = ZegoRoomManager.getInstance().roomService.roomInfo.getRoomID();
        return String.format("%s_%s_%s", roomID, userID, "main");
    }
}