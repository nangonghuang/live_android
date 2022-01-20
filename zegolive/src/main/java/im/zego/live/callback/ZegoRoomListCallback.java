package im.zego.live.callback;

import java.util.List;

/**
 * Created by rocket_wang on 2021/12/14.
 */
public interface ZegoRoomListCallback {
    void onRoomListCallback(int errorCode, List<Object> roomList);
}