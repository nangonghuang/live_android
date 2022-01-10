package im.zego.live.helper;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Objects;

import im.zego.live.ZegoRoomManager;
import im.zego.live.model.ZegoCoHostSeatModel;
import im.zego.live.model.ZegoRoomInfo;
import im.zego.live.model.ZegoUserInfo;

public final class UserInfoHelper {

    public static boolean isSelfHost() {
        return ZegoRoomManager.getInstance().userService.isSelfHost();
    }

    public static boolean isSelfCoHost() {
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        return isUserIDCoHost(selfUser.getUserID());
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

    public static String getUserName(String userID) {
        return ZegoRoomManager.getInstance().userService.getUserName(userID);
    }
}