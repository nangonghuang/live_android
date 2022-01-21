package im.zego.live.callback;

import java.util.List;

/**
 * Callback for get the room list
 *
 * Description: This callback will be triggered when the method call that get the room list has finished its execution.
 */
public interface ZegoRoomListCallback {
    /**
     * @param errorCode refers to the operation status code.
     *                  <h4>0: Operation successful.</h4>
     *                  <h4>600xxxx: The ZIM SDK error code. For details, refer to the error code <a href="https://docs.zegocloud.com/article/13792">documentation</a>. </h4>
     *
     * @param roomList refers to the room list.
     */
    void onRoomListCallback(int errorCode, List<Object> roomList);
}