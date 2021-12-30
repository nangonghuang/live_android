package im.zego.live.helper;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Objects;

import im.zego.live.ZegoRoomManager;
import im.zego.live.model.ZegoCoHostSeatModel;
import im.zego.live.model.ZegoUserInfo;

public final class UserInfoHelper {

    public static boolean isSelfOwner() {
        return ZegoRoomManager.getInstance().userService.isSelfHost();
    }

    public static boolean isSelfCoHost() {
        List<ZegoCoHostSeatModel> coHostList = ZegoRoomManager.getInstance().userService.coHostList;
        boolean isSelfCoHost = false;
        for (ZegoCoHostSeatModel model : coHostList) {
            if (UserInfoHelper.isUserIDSelf(model.getUserID())) {
                isSelfCoHost = true;
                break;
            }
        }
        return isSelfCoHost;
    }

    public static boolean isUserIDSelf(String userID) {
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        return Objects.equals(selfUser.getUserID(), userID) && StringUtils.isNotEmpty(userID);
    }
}