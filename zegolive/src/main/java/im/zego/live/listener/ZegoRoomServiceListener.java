package im.zego.live.listener;

import im.zego.live.model.OperationAction;
import im.zego.live.model.ZegoRoomInfo;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;

/**
 * The delegate related to room status callbacks
 * <p>
 * Description: Callbacks that be triggered when room status changes.
 */
public interface ZegoRoomServiceListener {

    /**
     * Callback for the room status update
     * <p>
     * Description: This callback will be triggered when the text chat is disabled or there is a speaker seat be closed in the room. And all uses in the room receive a notification through this callback.
     *
     * @param roomInfo refers to the updated room information.
     */
    void onReceiveRoomInfoUpdate(ZegoRoomInfo roomInfo);

    /**
     * Callback for the co-host list update
     * <p>
     * Description: This callback will be triggered when the co-host status change, like take seat, leave seat.
     *
     * @param action operation action
     */
    void onReceiveCoHostListUpdate(OperationAction action);

    /**
     * Callbacks related to the user connection status
     * <p>
     * Description: This callback will be triggered when user gets disconnected due to network error, or gets offline due to the operations in other clients.
     *
     * @param state refers to the current connection state.
     * @param event refers to the the event that causes the connection status changes.
     */
    void onConnectionStateChanged(ZIMConnectionState state, ZIMConnectionEvent event);

    /**
     * Callback notification that Token authentication is about to expire.
     * <p>
     * Description:The callback notification that the Token authentication is about to expire, please use [renewToken] to update the Token authentication.
     *
     * @param remainTimeInSecond The remaining time before the token expires.
     * @param roomID             Room ID where the user is logged in, a string of up to 128 bytes in length.
     */
    void onRoomTokenWillExpire(int remainTimeInSecond, String roomID);
}