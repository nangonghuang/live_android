package im.zego.live.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import im.zego.live.ZegoRoomManager;
import im.zego.live.ZegoZIMManager;
import im.zego.live.callback.ZegoOnlineRoomUserListCallback;
import im.zego.live.callback.ZegoOnlineRoomUsersNumCallback;
import im.zego.live.callback.ZegoRoomCallback;
import im.zego.live.constants.ZegoRoomErrorCode;
import im.zego.live.helper.UserInfoHelper;
import im.zego.live.helper.ZegoLiveHelper;
import im.zego.live.helper.ZegoRoomAttributesHelper;
import im.zego.live.listener.ZegoUserServiceListener;
import im.zego.live.model.OperationAction;
import im.zego.live.model.OperationActionType;
import im.zego.live.model.OperationCommand;
import im.zego.live.model.ZegoCoHostSeatModel;
import im.zego.live.model.ZegoCustomCommand;
import im.zego.live.model.ZegoRoomInfo;
import im.zego.live.model.ZegoRoomUserRole;
import im.zego.live.model.ZegoUserInfo;
import im.zego.live.util.Triple;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMMessageSentCallback;
import im.zego.zim.callback.ZIMRoomMemberQueriedCallback;
import im.zego.zim.entity.ZIMCommandMessage;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMMessage;
import im.zego.zim.entity.ZIMMessageSendConfig;
import im.zego.zim.entity.ZIMRoomAttributesSetConfig;
import im.zego.zim.entity.ZIMRoomMemberQueryConfig;
import im.zego.zim.entity.ZIMUserInfo;
import im.zego.zim.enums.ZIMErrorCode;
import im.zego.zim.enums.ZIMMessageType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Class user information management
 * <p>
 * Description: This class contains the user information management logic,
 * such as the logic of log in, log out, get the logged-in user info, get the in-room user list, and add co-hosts, etc.
 */
public class ZegoUserService {
    private static final String TAG = "ZegoUserService";
    private static final long RESET_INVITED_DELAY_TIME = 60 * 1000L;

    // The listener related to user status
    private ZegoUserServiceListener listener;

    // The local logged-in user information.
    public ZegoUserInfo localUserInfo;
    // In-room user list, can be used when displaying the user list in the room.
    private final List<ZegoUserInfo> userList = new ArrayList<>();
    // Co-host list, can be used when display the co-host list in the room.
    public List<ZegoCoHostSeatModel> coHostList = new ArrayList<>();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Map<String, ZegoUserInfo> userMap = new HashMap<>();

    /**
     * User to log in
     * <p>
     * Description: Call this method with user ID and username to log in to the ZEGO Live service.
     * <p>
     * Call this method at: After the SDK initialization
     *
     * @param userInfo refers to the user information. You only need to enter the user ID and username.
     * @param token    refers to the authentication token. To get this, refer to the documentation: https://docs.zegocloud.com/article/11648
     * @param callback refers to the callback for log in.
     */
    public void login(ZegoUserInfo userInfo, String token, final ZegoRoomCallback callback) {
        ZIMUserInfo zimUserInfo = new ZIMUserInfo();
        zimUserInfo.userID = userInfo.getUserID();
        zimUserInfo.userName = userInfo.getUserName();
        ZegoZIMManager.getInstance().zim.login(zimUserInfo, token, errorInfo -> {
            Log.d(TAG, "onLoggedIn() called with: errorInfo = [" + errorInfo.code + ", "
                    + errorInfo.message + "]");
            if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                localUserInfo = new ZegoUserInfo();
                localUserInfo.setUserID(userInfo.getUserID());
                localUserInfo.setUserName(userInfo.getUserName());
            }
            if (callback != null) {
                callback.onRoomCallback(errorInfo.code.value());
            }
        });
    }

    /**
     * User to log out
     * <p>
     * Description: This method can be used to log out from the current user account.
     * <p>
     * Call this method at: After the user login
     */
    public void logout() {
        Log.d(TAG, "logout() called");
        ZegoZIMManager.getInstance().zim.logout();
        leaveRoom();
    }

    void leaveRoom() {
        userList.clear();
        userMap.clear();
        coHostList.clear();
    }

    /**
     * Get the in-room user list
     * <p>
     * Description: This method can be called to get the in-room user list.
     * <p>
     * Call this method at:  After joining the room
     *
     * @param nextFlag Passing a null value to nextFlag will get the 100 people who recently joined the room.
     *                 Passing in the nextFlag value returned from the last query retrieves the list of users since the last query.
     * @param callback refers to the callback for get the in-room user list.
     */
    public void getOnlineRoomUsers(String nextFlag, ZegoOnlineRoomUserListCallback callback) {
        ZegoRoomInfo roomInfo = ZegoRoomManager.getInstance().roomService.roomInfo;
        ZIMRoomMemberQueryConfig config = new ZIMRoomMemberQueryConfig();
        config.count = 1000;
        config.nextFlag = nextFlag;
        ZegoZIMManager.getInstance().zim.queryRoomMemberList(roomInfo.getRoomID(), config, new ZIMRoomMemberQueriedCallback() {
            @Override
            public void onRoomMemberQueried(String roomID, ArrayList<ZIMUserInfo> memberList, String nextFlag,
                ZIMError errorInfo) {
                if (callback != null) {
                    List<ZegoUserInfo> userList = generateRoomUsers(memberList);
                    callback.onUserListCallback(errorInfo.code.value(), userList, nextFlag);
                }
            }
        });
    }

    /**
     * Get the total number of in-room users
     * <p>
     * Description: This method can be called to get the total number of the in-room users.
     * <p>
     * Call this method at: After joining a room
     *
     * @param callback refers to the callback for get the total number of in-room users.
     */
    public void getOnlineRoomUsersNum(ZegoOnlineRoomUsersNumCallback callback) {
        ZegoRoomInfo roomInfo = ZegoRoomManager.getInstance().roomService.roomInfo;
        ZegoZIMManager.getInstance().zim.queryRoomOnlineMemberCount(roomInfo.getRoomID(),
            (roomID, count, errorInfo) -> {
                if (callback != null) {
                    callback.onUserCountCallback(errorInfo.code.value(), count);
                }
            });
    }

    /**
     * Make co-hosts
     * <p>
     * Description: This method can be called to invite an existing participant to co-host,
     * the invited participant will receive a invitation.
     * <p>
     * Call this method at:  After joining a room
     *
     * @param userID   refers to the ID of the user that you want to invite to be a co-host.
     * @param callback refers to the callback for make a co-host.
     */
    public void addCoHost(String userID, ZegoRoomCallback callback) {
        ZegoCustomCommand command = new ZegoCustomCommand();
        command.actionType = ZegoCustomCommand.CustomCommandType.Invitation;
        command.senderUserID = localUserInfo.getUserID();
        command.targetUserIDs = Collections.singletonList(userID);
        command.toJson();
        ZegoZIMManager.getInstance().zim.sendPeerMessage(command, userID, new ZIMMessageSendConfig(),
            (message, errorInfo) -> {
                if (errorInfo.code.value() == ZegoRoomErrorCode.SUCCESS) {
                    List<ZegoUserInfo> userInfoList = ZegoRoomManager.getInstance().userService.getUserList();
                    for (ZegoUserInfo zegoUserInfo : userInfoList) {
                        if (Objects.equals(userID, zegoUserInfo.getUserID())) {
                            zegoUserInfo.setHasInvited(true);
                            handler.postDelayed(() -> {
                                zegoUserInfo.setHasInvited(false);
                                if (listener != null) {
                                    listener.onRoomUserInfoUpdate(userInfoList);
                                }
                            }, RESET_INVITED_DELAY_TIME);
                            break;
                        }
                    }
                }

                if (callback != null) {
                    callback.onRoomCallback(errorInfo.code.value());
                }
            });
    }

    /**
     * Respond to the co-host invitation
     * <p>
     * Description: This method can be used to accept or decline the co-host invitation sent by the host.
     * <p>
     * Call this method at: After joining a room
     *
     * @param accept:  Pass true or false to accept or decline the invitation.
     * @param callback refers to the callback for respond to the co-host invitation.
     */
    public void respondCoHostInvitation(boolean accept, String operateUserID, ZegoRoomCallback callback) {
        ZegoCustomCommand command = new ZegoCustomCommand();
        command.actionType = ZegoCustomCommand.CustomCommandType.RespondInvitation;
        command.senderUserID = localUserInfo.getUserID();
        command.targetUserIDs = Collections.singletonList(operateUserID);
        command.content = new ZegoCustomCommand.CustomCommandContent(accept);
        command.toJson();
        ZegoZIMManager.getInstance().zim.sendPeerMessage(command, operateUserID, new ZIMMessageSendConfig(),
            new ZIMMessageSentCallback() {
                @Override
                public void onMessageSent(ZIMMessage message, ZIMError errorInfo) {
                    if (callback != null) {
                        callback.onRoomCallback(errorInfo.code.value());
                    }
                }
            });
    }

    /**
     * Request to co-host
     * <p>
     * Description: This method can be used to send a co-host request to the host.
     * <p>
     * Call this method at:  After joining a room
     *
     * @param callback refers to the callback for request to co-host.
     */
    public void requestToCoHost(ZegoRoomCallback callback) {
        Triple<HashMap<String, String>, String, ZIMRoomAttributesSetConfig> triple
                = ZegoRoomAttributesHelper.getRequestOrCancelToHostParameters(true);
        ZegoRoomAttributesHelper.setRoomAttributes(triple.first, triple.second, triple.third, callback);
    }

    /**
     * Cancel the co-host request
     * <p>
     * Description: This method can be used when a participant wants to cancel the co-host request.
     * <p>
     * Call this method at:  After joining a room
     *
     * @param callback refers to the callback for cancel the co-host request.
     */
    public void cancelRequestToCoHost(ZegoRoomCallback callback) {
        Triple<HashMap<String, String>, String, ZIMRoomAttributesSetConfig> triple
                = ZegoRoomAttributesHelper.getRequestOrCancelToHostParameters(false);
        ZegoRoomAttributesHelper.setRoomAttributes(triple.first, triple.second, triple.third, callback);
    }

    /**
     * Respond to the co-host request
     * <p>
     * Description: This method can be called when the host responds to the co-host request sent by participants.
     * The participants can call the takeSeat to be a co-host when the co-host request has been accept.
     * <p>
     * Call this method at:  After joining a room
     *
     * @param callback refers to the callback for respond to the co-host request.
     */
    public void respondCoHostRequest(boolean agree, String userID, ZegoRoomCallback callback) {
        Triple<HashMap<String, String>, String, ZIMRoomAttributesSetConfig> triple
                = ZegoRoomAttributesHelper.getRespondCoHostParameters(agree, userID);
        if (triple != null) {
            ZegoRoomAttributesHelper.setRoomAttributes(triple.first, triple.second, triple.third, callback);
        }
    }

    /**
     * Mute co-hosts
     * <p>
     * Description: This method can be used to mute or unmute a co-host. Once a co-host is muted by the host,
     * he can only speak again until the host's next unmute operation.
     * <p>
     * Call this method at:  After joining a room
     *
     * @param isMuted  determines whether to mute or unmute a co-host.  true: mute. false: unmute.
     * @param userID   refers to the ID of the co-host that the host want to mute.
     * @param callback refers to the callback for mute a co-host.
     */
    public void muteUser(boolean isMuted, String userID, ZegoRoomCallback callback) {
        Triple<HashMap<String, String>, String, ZIMRoomAttributesSetConfig> triple
                = ZegoRoomAttributesHelper.getSeatChangeParameters(userID, isMuted, 2);
        if (triple != null) {
            ZegoRoomAttributesHelper.setRoomAttributes(triple.first, triple.second, triple.third, callback);
        }
    }

    /**
     * Microphone related operations
     * <p>
     * Description: This method can be used to turn on/off the microphone.
     * The audio streams will be published to remote users when the microphone is on, and the audio stream publishing stops when the microphone is off.
     * This method will failed to be called when you have been muted.
     * <p>
     * Call this method at:  After joining a room
     *
     * @param open     determines whether to turn on or turn off the microphone. true: turn on. false: turn off.
     * @param callback refers to the callback for turn on or turn off the microphone.
     */
    public void micOperate(boolean open, ZegoRoomCallback callback) {
        Triple<HashMap<String, String>, String, ZIMRoomAttributesSetConfig> triple
                = ZegoRoomAttributesHelper.getSeatChangeParameters(localUserInfo.getUserID(), open, 0);
        if (triple != null) {
            ZegoRoomAttributesHelper.setRoomAttributes(triple.first, triple.second, triple.third, errorCode -> {
                if (callback != null) {
                    callback.onRoomCallback(errorCode);
                }
            });
        }
    }

    /**
     * Camera related operations
     * <p>
     * Description: This method can be used to turn on/off the camera.
     * The video streams will be published to remote users, and the video stream publishing stops when the camera is turned off.
     * <p>
     * Call this method at:  After joining a room
     *
     * @param open     determines whether to turn on or turn off the camera. true: turn on. false: turn off.
     * @param callback refers to the callback for turn on or turn off the camera.
     */
    public void cameraOperate(boolean open, ZegoRoomCallback callback) {
        Triple<HashMap<String, String>, String, ZIMRoomAttributesSetConfig> triple
                = ZegoRoomAttributesHelper.getSeatChangeParameters(localUserInfo.getUserID(), open, 1);
        if (triple != null) {
            ZegoRoomAttributesHelper.setRoomAttributes(triple.first, triple.second, triple.third, errorCode -> {
                if (callback != null) {
                    callback.onRoomCallback(errorCode);
                }
            });
        }
    }

    /**
     * Take a seat
     * <p>
     * Description: This method can be used to take a co-host seat.
     * All participants in the room receive a notification when this gets called.
     * And the number of co-hosts changes, the streams of the participant who just take the seat will be played.
     * <p>
     * Call this method at:  After joining a room
     *
     * @param callback refers to the callback for take a co-host seat.
     */
    public void takeSeat(ZegoRoomCallback callback) {
        Triple<HashMap<String, String>, String, ZIMRoomAttributesSetConfig> triple
                = ZegoRoomAttributesHelper.getTakeOrLeaveSeatParameters(null, true);
        ZegoRoomAttributesHelper.setRoomAttributes(triple.first, triple.second, triple.third, errorCode -> {
            if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                ZegoDeviceService deviceService = ZegoRoomManager.getInstance().deviceService;
                ZegoExpressEngine.getEngine().startPublishingStream(ZegoLiveHelper.getSelfStreamID());
                deviceService.enableCamera(true);
                deviceService.muteMic(false);
            }
            if (callback != null) {
                callback.onRoomCallback(errorCode);
            }
        });
    }

    /**
     * Leave a seat
     * <p>
     * Description: This method can be used to leave the current seat.
     * All participants in the room receive a notification when this gets called,
     * and the UI shows a notification, the streams of the participant who just left the seat will not be played.
     * <p>
     * Call this method at: After joining a room
     *
     * @param callback refers to the callback for leave a co-host seat.
     */
    public void leaveSeat(String userID, ZegoRoomCallback callback) {
        Triple<HashMap<String, String>, String, ZIMRoomAttributesSetConfig> triple
                = ZegoRoomAttributesHelper.getTakeOrLeaveSeatParameters(userID, false);
        ZegoRoomAttributesHelper.setRoomAttributes(triple.first, triple.second, triple.third, errorCode -> {
            if (errorCode == ZegoRoomErrorCode.SUCCESS && UserInfoHelper.isUserIDSelf(userID)) {
                ZegoExpressEngine.getEngine().stopPublishingStream();
            }
            if (callback != null) {
                callback.onRoomCallback(errorCode);
            }
        });
    }

    public void setListener(ZegoUserServiceListener listener) {
        this.listener = listener;
    }

    public void onRoomMemberJoined(ZIM zim, ArrayList<ZIMUserInfo> memberList, String roomID) {
        List<ZegoUserInfo> joinUsers = generateRoomUsers(memberList);
        Iterator<ZegoUserInfo> iterator = joinUsers.iterator();
        while (iterator.hasNext()) {
            ZegoUserInfo next = iterator.next();
            if (!userMap.containsKey(next.getUserID())) {
                userList.add(next); // avoid duplicate
                userMap.put(next.getUserID(), next);
            } else {
                // if duplicate,don't notify outside
                iterator.remove();
            }
        }
        if (joinUsers.size() > 0 && listener != null) {
            listener.onRoomUserJoin(joinUsers);
        }
    }

    public void onRoomMemberLeft(ZIM zim, ArrayList<ZIMUserInfo> memberList, String roomID) {
        List<ZegoUserInfo> leaveUsers = generateRoomUsers(memberList);
        userList.removeAll(leaveUsers);
        for (ZegoUserInfo leaveUser : leaveUsers) {
            userMap.remove(leaveUser.getUserID());
            if (UserInfoHelper.isSelfHost() && UserInfoHelper.isUserIDCoHost(leaveUser.getUserID())) {
                leaveSeat(leaveUser.getUserID(), null);
            }
        }
        if (listener != null) {
            listener.onRoomUserLeave(leaveUsers);
        }
    }

    private List<ZegoUserInfo> generateRoomUsers(List<ZIMUserInfo> memberList) {
        ZegoRoomInfo roomInfo = ZegoRoomManager.getInstance().roomService.roomInfo;

        List<ZegoUserInfo> roomUsers = new ArrayList<>();
        for (ZIMUserInfo userInfo : memberList) {
            ZegoUserInfo roomUser = new ZegoUserInfo();
            roomUser.setUserID(userInfo.userID);
            roomUser.setUserName(userInfo.userName);

            if (userInfo.userID.equals(roomInfo.getHostID())) {
                roomUser.setRole(ZegoRoomUserRole.Host);
            } else {
                roomUser.setRole(ZegoRoomUserRole.Participant);
            }
            roomUsers.add(roomUser);
        }
        return roomUsers;
    }

    public List<ZegoUserInfo> getUserList() {
        return userList;
    }

    public ZegoUserInfo getUserInfo(String userID) {
        return userMap.get(userID);
    }

    public String getUserName(String userID) {
        ZegoUserInfo zegoUserInfo = userMap.get(userID);
        if (zegoUserInfo != null) {
            return zegoUserInfo.getUserName();
        } else {
            return "";
        }
    }

    public void onRoomAttributesUpdated(HashMap<String, String> roomAttributes, OperationCommand command) {
        String myUserID = localUserInfo.getUserID();
        OperationAction action = command.getAction();

        switch (action.getType()) {
            case RequestToCoHost:
                if (UserInfoHelper.isSelfHost()) {
                    if (listener != null) {
                        listener.onReceiveToCoHostRequest(action.getTargetID());
                    }
                }
                break;
            case CancelRequestCoHost:
                if (UserInfoHelper.isSelfHost()) {
                    if (listener != null) {
                        listener.onReceiveCancelToCoHostRequest(action.getTargetID());
                    }
                }
                break;
            case AgreeToCoHost:
                if (Objects.equals(myUserID, action.getTargetID())) {
                    if (listener != null) {
                        listener.onReceiveToCoHostRespond(true);
                    }
                }
                break;
            case DeclineToCoHost:
                if (Objects.equals(myUserID, action.getTargetID())) {
                    if (listener != null) {
                        listener.onReceiveToCoHostRespond(false);
                    }
                }
                break;
        }

        ZegoCoHostSeatModel seat = null;
        for (ZegoCoHostSeatModel model : command.getSeatList()) {
            if (Objects.equals(model.getUserID(), myUserID)) {
                seat = model;
            }
        }

        if (seat == null) {
            return;
        }

        if (!Objects.equals(myUserID, action.getTargetID())) {
            return;
        }

        ZegoDeviceService deviceService = ZegoRoomManager.getInstance().deviceService;
        if (action.getType() == OperationActionType.Mic) {
            deviceService.muteMic(!seat.isMicEnable());
        }

        if (action.getType() == OperationActionType.Camera) {
            deviceService.enableCamera(seat.isCameraEnable());
        }

        if (action.getType() == OperationActionType.Mute) {
            deviceService.muteMic(seat.isMuted());
            micOperate(!seat.isMuted(), null);
        }
    }

    public void onReceivePeerMessage(ZIM zim, ArrayList<ZIMMessage> messageList, String fromUserID) {
        for (ZIMMessage zimMessage : messageList) {
            if (zimMessage.type == ZIMMessageType.COMMAND) {
                ZIMCommandMessage zimCustomMessage = (ZIMCommandMessage) zimMessage;
                ZegoCustomCommand command = new ZegoCustomCommand();
                command.type = zimCustomMessage.type;
                command.senderUserID = zimCustomMessage.senderUserID;
                command.fromJson(zimCustomMessage.message);
                if (command.actionType == ZegoCustomCommand.CustomCommandType.Invitation) {
                    if (listener != null) {
                        listener.onReceiveAddCoHostInvitation(zimCustomMessage.senderUserID);
                    }
                } else {
                    ZegoCustomCommand.CustomCommandContent content = command.content;
                    if (content == null) {
                        continue;
                    }
                    List<ZegoUserInfo> userInfoList = ZegoRoomManager.getInstance().userService.getUserList();
                    for (ZegoUserInfo zegoUserInfo : userInfoList) {
                        if (Objects.equals(command.senderUserID, zegoUserInfo.getUserID())) {
                            zegoUserInfo.setHasInvited(false);
                            break;
                        }
                    }

                    if (listener != null) {
                        listener.onReceiveAddCoHostRespond(command.senderUserID, content.accept);
                    }
                }
            }
        }
    }
}