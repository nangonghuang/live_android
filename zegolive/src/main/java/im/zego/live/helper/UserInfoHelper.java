package im.zego.live.helper;

import androidx.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Objects;

import im.zego.live.ZegoRoomManager;
import im.zego.live.model.ZegoCoHostSeatModel;
import im.zego.live.model.ZegoRoomInfo;
import im.zego.live.model.ZegoUserInfo;

public final class UserInfoHelper {

    public static boolean isSelfHost() {
        String hostID = ZegoRoomManager.getInstance().roomService.roomInfo.getHostID();
        String userID = ZegoRoomManager.getInstance().userService.localUserInfo.getUserID();
        return Objects.equals(hostID, userID) && StringUtils.isNotEmpty(hostID);
    }

    public static boolean isSelfCoHost() {
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        return isUserIDCoHost(selfUser.getUserID());
    }

    public static boolean isSelfInRequestedCoHost() {
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        return isUserIDInRequestedCoHost(selfUser.getUserID());
    }

    public static boolean isUserIDSelf(String userID) {
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        return Objects.equals(selfUser.getUserID(), userID) && StringUtils.isNotEmpty(userID);
    }

    public static boolean isUserIDHost(String userID) {
        ZegoRoomInfo roomInfo = ZegoRoomManager.getInstance().roomService.roomInfo;
        return Objects.equals(roomInfo.getHostID(), userID) && StringUtils.isNotEmpty(userID);
    }

    public static boolean isUserIDCoHost(String userID) {
        List<ZegoCoHostSeatModel> coHostList = ZegoRoomManager.getInstance().userService.coHostList;
        boolean isCoHost = false;
        for (ZegoCoHostSeatModel model : coHostList) {
            if (Objects.equals(model.getUserID(), userID) && StringUtils.isNotEmpty(userID)) {
                isCoHost = true;
                break;
            }
        }
        return isCoHost;
    }

    public static boolean isUserIDInRequestedCoHost(String userID) {
        boolean hasRequested = false;
        List<ZegoUserInfo> userInfoList = ZegoRoomManager.getInstance().userService.getUserList();
        for (ZegoUserInfo zegoUserInfo : userInfoList) {
            if (Objects.equals(zegoUserInfo.getUserID(), userID) && StringUtils.isNotEmpty(userID)) {
                hasRequested = zegoUserInfo.isHasRequestedCoHost();
                break;
            }
        }
        return hasRequested;
    }

    public static ZegoCoHostSeatModel getSelfCoHost() {
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        String userID = selfUser.getUserID();
        List<ZegoCoHostSeatModel> coHostList = ZegoRoomManager.getInstance().userService.coHostList;
        ZegoCoHostSeatModel selfModel = null;
        for (ZegoCoHostSeatModel model : coHostList) {
            if (Objects.equals(model.getUserID(), userID) && StringUtils.isNotEmpty(userID)) {
                selfModel = model;
                break;
            }
        }
        return selfModel;
    }

    @Nullable
    public static ZegoCoHostSeatModel getSeatModel(List<ZegoCoHostSeatModel> seatList, String targetID) {
        ZegoCoHostSeatModel seatModel = null;
        for (ZegoCoHostSeatModel model : seatList) {
            if (Objects.equals(model.getUserID(), targetID)) {
                seatModel = model;
                break;
            }
        }
        return seatModel;
    }

    public static String getUserName(String userID) {
        return ZegoRoomManager.getInstance().userService.getUserName(userID);
    }

    public static String getUserNameShort(String userName) {
        return StringUtils.abbreviate(userName, 11);
    }
}