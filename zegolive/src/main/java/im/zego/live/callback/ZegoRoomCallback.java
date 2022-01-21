package im.zego.live.callback;

/**
 * Callback methods
 *
 * Description: When the called method is asynchronous processing, If you are making and processing asynchronous calls,
 * the following callbacks will be triggered when a method has finished its execution and returns the execution result.
 */
public interface ZegoRoomCallback {

    /**
     * @param errorCode refers to the operation status code.
     *                  <h4>0: Operation successful.</h4>
     *                  <h4>100xxxx: The Express SDK error code. For details, refer to the error code <a href="https://docs.zegocloud.com/article/5548">documentation</a>.</h4>
     *                  <h4>600xxxx: The ZIM SDK error code. For details, refer to the error code <a href="https://docs.zegocloud.com/article/13792">documentation</a>.</h4>
     */
    void onRoomCallback(int errorCode);
}