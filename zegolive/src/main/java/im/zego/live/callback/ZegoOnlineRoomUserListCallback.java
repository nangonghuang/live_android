package im.zego.live.callback;

import java.util.List;

import im.zego.live.model.ZegoUserInfo;

/**
 * Callback for get the user list
 * <p>
 * Description: This callback will be triggered when the method call that get the user list has finished its execution.
 */
public interface ZegoOnlineRoomUserListCallback {

    /**
     * @param errorCode refers to the operation status code.
     *                  <h4>0: Operation successful.</h4>
     *                  <h4>600xxxx: The ZIM SDK error code. For details, refer to the error code <a href="https://docs.zegocloud.com/article/13792">documentation</a>. </h4>
     *                  <p>
     * @param userList  refers to the in-room user list.
     * @param nextFlag  can be used to query the flags of the next page's user list.
     *                  Passing this parameter when calling the 'getOnlineRoomUsers' will continue the query from where it was last queried.
     */
    void onUserListCallback(int errorCode, List<ZegoUserInfo> userList, String nextFlag);
}