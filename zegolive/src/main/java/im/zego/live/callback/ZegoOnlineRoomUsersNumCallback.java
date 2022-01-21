package im.zego.live.callback;

/**
 * Callback for get the user list
 *
 * Description: This callback will be triggered when the method call that get the user list has finished its execution.
 */
public interface ZegoOnlineRoomUsersNumCallback {

    /**
     * @param errorCode refers to the operation status code.
     *                  <h4>0: Operation successful.</h4>
     *                  <h4>600xxxx: The ZIM SDK error code. For details, refer to the error code <a href="https://docs.zegocloud.com/article/13792">documentation</a>. </h4>
     * <p>
     * @param count     refers to the in-room user list.
     */
    void onUserCountCallback(int errorCode, int count);
}