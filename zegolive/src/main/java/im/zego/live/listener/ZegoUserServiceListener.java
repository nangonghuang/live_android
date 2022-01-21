package im.zego.live.listener;

import java.util.List;

import im.zego.live.model.ZegoUserInfo;

/**
 * The delegate related to the user status callbacks
 * <p>
 * Description: Callbacks that be triggered when in-room user status change.
 */
public interface ZegoUserServiceListener {

    /**
     * Callback for user list update
     * <p>
     * Description: This callback will be triggered when user status changed.
     *
     * @param userList refers to the latest user list.
     */
    void onRoomUserInfoUpdate(List<ZegoUserInfo> userList);

    /**
     * Callback for new user joins the room
     * <p>
     * Description: This callback will be triggered when a new user joins the room,
     * and all users in the room will receive a notification.
     * The in-room user list data will be updated automatically.
     *
     * @param userList refers to the latest new-comer user list. Existing users are not included.
     */
    void onRoomUserJoin(List<ZegoUserInfo> userList);

    /**
     * Callback for existing user leaves the room
     * <p>
     * Description: This callback will be triggered when an existing user leaves the room,
     * and all users in the room will receive a notification.
     * The in-room user list data will be updated automatically.
     *
     * @param userList refers to the list of users who left the room.
     */
    void onRoomUserLeave(List<ZegoUserInfo> userList);

    /**
     * Callback for receive a co-host invitation
     * <p>
     * Description: This callback will be triggered when a participant in the room was invited to co-host by the host.
     */
    void onReceiveAddCoHostInvitation(String operateUserID);

    /**
     * Callback for receive the response of a co-host invitation
     * <p>
     * Description: This callback will be triggered when the host receives the participant's response of the co-host invitation.
     *
     * @param accept indicates whether the invited participant accept or decline the invitation.
     */
    void onReceiveAddCoHostRespond(String userID, boolean accept);

    /**
     * Callback for receive a co-host request
     *
     * Description: This callback will be triggered when the host receive a co-host request sent by a participant in the room.
     */
    void onReceiveToCoHostRequest(String requestUserID);

    /**
     * Callback for a co-host request has been canceled
     *
     * Description: This callback will be triggered and the host will receive a notification through this callback when a participant cancel his co-host request.
     */
    void onReceiveCancelToCoHostRequest(String requestUserID);

    /**
     * Callback for receive the response of a co-host request
     *
     * Description: This callback will be triggered and the participant who requested to co-host will receive a notification through this callback when the host responds to the co-host request.
     */
    void onReceiveToCoHostRespond(boolean agree);
}