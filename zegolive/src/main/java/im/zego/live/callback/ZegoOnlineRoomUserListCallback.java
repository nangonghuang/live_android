package im.zego.live.callback;

import java.util.List;

import im.zego.live.model.ZegoUserInfo;

/**
 * Created by rocket_wang on 2021/12/14.
 */
public interface ZegoOnlineRoomUserListCallback {
    void onUserListCallback(int errorCode, List<ZegoUserInfo> userList);
}