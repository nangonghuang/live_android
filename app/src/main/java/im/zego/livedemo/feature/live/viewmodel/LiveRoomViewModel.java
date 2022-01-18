package im.zego.livedemo.feature.live.viewmodel;

import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import im.zego.live.ZegoRoomManager;
import im.zego.live.callback.ZegoRoomCallback;
import im.zego.live.constants.ZegoRoomConstants;
import im.zego.live.constants.ZegoRoomErrorCode;
import im.zego.live.helper.UserInfoHelper;
import im.zego.live.http.IAsyncGetCallback;
import im.zego.live.listener.ZegoRoomServiceListener;
import im.zego.live.listener.ZegoUserServiceListener;
import im.zego.live.model.OperationAction;
import im.zego.live.model.ZegoCoHostSeatModel;
import im.zego.live.model.ZegoRoomInfo;
import im.zego.live.model.ZegoTextMessage;
import im.zego.live.model.ZegoUserInfo;
import im.zego.live.model.httpmodel.RoomBean;
import im.zego.live.service.ZegoDeviceService;
import im.zego.live.service.ZegoFaceBeautifyService;
import im.zego.live.service.ZegoMessageService;
import im.zego.live.service.ZegoRoomListService;
import im.zego.live.service.ZegoSoundEffectService;
import im.zego.live.service.ZegoUserService;
import im.zego.livedemo.R;
import im.zego.livedemo.helper.AuthInfoManager;
import im.zego.livedemo.helper.ToastHelper;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoOrientation;
import im.zego.zegoexpress.constants.ZegoViewMode;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import im.zego.zim.enums.ZIMErrorCode;

/**
 * Created by rocket_wang on 2021/12/27.
 */
public class LiveRoomViewModel extends ViewModel {

    public MutableLiveData<Boolean> isCameraEnable = new MutableLiveData<>();
    public MutableLiveData<Boolean> isMicEnable = new MutableLiveData<>();
    public MutableLiveData<List<ZegoTextMessage>> textMessageList = new MutableLiveData<>();
    public MutableLiveData<List<ZegoUserInfo>> userList = new MutableLiveData<>();
    public MutableLiveData<List<ZegoCoHostSeatModel>> coHostList = new MutableLiveData<>();
    public MutableLiveData<OperationAction> operationAction = new MutableLiveData<>();

    private final List<ZegoTextMessage> joinLeaveMessages = new ArrayList<>();
    private Timer timer = new Timer();

    public void init(ILiveRoomViewModelListener listener) {
        ZegoRoomManager.getInstance().roomService.setListener(new ZegoRoomServiceListener() {
            @Override
            public void onReceiveRoomInfoUpdate(ZegoRoomInfo roomInfo) {
                listener.onReceiveRoomInfoUpdate(roomInfo);
            }

            @Override
            public void onReceiveCoHostListUpdate(OperationAction action) {
                coHostList.postValue(ZegoRoomManager.getInstance().userService.coHostList);
                operationAction.postValue(action);

                ZegoCoHostSeatModel model = UserInfoHelper.getSelfCoHost();
                if (model != null) {
                    isMicEnable.postValue(model.isMicEnable());
                    isCameraEnable.postValue(model.isCameraEnable());
                }
            }

            @Override
            public void onConnectionStateChanged(ZIMConnectionState state, ZIMConnectionEvent event) {
                listener.onConnectionStateChanged(state, event);
            }
        });

        ZegoRoomManager.getInstance().userService.setListener(new ZegoUserServiceListener() {
            @Override
            public void onRoomUserInfoUpdate(List<ZegoUserInfo> memberList) {
                updateUserList();
            }

            @Override
            public void onRoomUserJoin(List<ZegoUserInfo> memberList) {
                ZegoUserService userService = ZegoRoomManager.getInstance().userService;
                boolean containsSelf = false;
                ZegoUserInfo localUserInfo = userService.localUserInfo;
                for (ZegoUserInfo userInfo : memberList) {
                    if (Objects.equals(userInfo.getUserID(), localUserInfo.getUserID())) {
                        containsSelf = true;
                        break;
                    }
                }
                if (containsSelf) {
                    ZegoTextMessage textMessage = new ZegoTextMessage();
                    textMessage.userName = UserInfoHelper.getUserName(localUserInfo.getUserID());
                    textMessage.userID = localUserInfo.getUserID();
                    textMessage.message = StringUtils.getString(R.string.room_page_joined_the_room);
                    textMessage.timestamp = System.currentTimeMillis();
                    joinLeaveMessages.add(textMessage);
                } else {
                    for (ZegoUserInfo user : memberList) {
                        ZegoTextMessage textMessage = new ZegoTextMessage();
                        textMessage.userName = user.getUserName();
                        textMessage.userID = user.getUserID();
                        textMessage.timestamp = System.currentTimeMillis();
                        textMessage.message = StringUtils.getString(R.string.room_page_joined_the_room);
                        joinLeaveMessages.add(textMessage);
                    }
                }
                updateMessageLiveData();
                updateUserList();
            }

            @Override
            public void onRoomUserLeave(List<ZegoUserInfo> memberList) {
                for (ZegoUserInfo user : memberList) {
                    ZegoTextMessage textMessage = new ZegoTextMessage();
                    textMessage.userName = user.getUserName();
                    textMessage.userID = user.getUserID();
                    textMessage.message = StringUtils.getString(R.string.room_page_has_left_the_room);
                    textMessage.timestamp = System.currentTimeMillis();
                    joinLeaveMessages.add(textMessage);
                    updateMessageLiveData();
                }
                updateUserList();
            }

            @Override
            public void onReceiveAddCoHostInvitation(String operateUserID) {
                listener.onReceiveAddCoHostInvitation(operateUserID);
            }

            @Override
            public void onReceiveAddCoHostRespond(String userID, boolean accept) {
                if (!accept) {
                    updateUserList();
                }
                listener.onReceiveAddCoHostRespond(userID, accept);
            }

            @Override
            public void onReceiveToCoHostRequest(String requestUserID) {
                listener.onReceiveToCoHostRequest(requestUserID);
            }

            @Override
            public void onReceiveCancelToCoHostRequest(String requestUserID) {
                listener.onReceiveCancelToCoHostRequest(requestUserID);
            }

            @Override
            public void onReceiveToCoHostRespond(boolean agree) {
                listener.onReceiveToCoHostRespond(agree);
            }
        });

        ZegoRoomManager.getInstance().messageService.setListener(textMessage -> {
            updateMessageLiveData();
        });
    }

    public void startPreview(TextureView view) {
        ZegoExpressEngine.getEngine().setAppOrientation(ZegoOrientation.ORIENTATION_0);
        ZegoCanvas zegoCanvas = new ZegoCanvas(view);
        zegoCanvas.viewMode = ZegoViewMode.ASPECT_FILL;
        ZegoExpressEngine.getEngine().startPreview(zegoCanvas);
    }

    public void stopPreview() {
        ZegoExpressEngine.getEngine().stopPreview();
    }

    public void startPlayingStream(String streamID, TextureView view) {
        ZegoDeviceService deviceService = ZegoRoomManager.getInstance().deviceService;
        deviceService.playVideoStream(streamID, view);
    }

    public void useFrontCamera(boolean enable) {
        ZegoDeviceService deviceService = ZegoRoomManager.getInstance().deviceService;
        deviceService.useFrontCamera(enable);
    }

    public void createRoom(String roomName, ZegoRoomCallback callback) {
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        ZegoUserInfo localUserInfo = ZegoRoomManager.getInstance().userService.localUserInfo;
        String userID = localUserInfo.getUserID();
        ZegoRoomListService.createRoom(roomName, userID, new IAsyncGetCallback<RoomBean>() {
            @Override
            public void onResponse(int errorCode, @NonNull String message, RoomBean responseJsonBean) {
                if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                    String roomID = responseJsonBean.getRoomID();
                    String token = AuthInfoManager.getInstance().generateCreateRoomToken(roomID, selfUser.getUserID());
                    ZegoRoomManager.getInstance().roomService.createRoom(roomID, roomName, token, errorCode1 -> {
                        Log.d("Room", "createRoom: " + errorCode1 + ",roomID:" + roomID);
                        if (errorCode1 == ZegoRoomErrorCode.SUCCESS) {
                            takeSeat(callback);
                            return;
                        }
                        if (callback != null) {
                            callback.onRoomCallback(errorCode);
                        }
                    });
                    ZegoRoomListService.joinServerRoom(userID, roomID, new IAsyncGetCallback<RoomBean>() {
                        @Override
                        public void onResponse(int errorCode, @NonNull String message, RoomBean responseJsonBean) {
                            TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    ZegoRoomListService.heartBeat(userID, roomID, true, null);
                                }
                            };
                            timer.schedule(task, 0, 30 * 1000);
                        }
                    });
                } else {
                    if (callback != null) {
                        callback.onRoomCallback(errorCode);
                    }
                }
            }
        });
    }

    public void joinRoom(String roomID, ZegoRoomCallback callback) {
        final ZegoUserService userService = ZegoRoomManager.getInstance().userService;
        String userID = userService.localUserInfo.getUserID();
        String token = AuthInfoManager.getInstance().generateJoinRoomToken(userID);
        ZegoRoomListService.joinServerRoom(userID, roomID, new IAsyncGetCallback<RoomBean>() {
            @Override
            public void onResponse(int errorCode, @NonNull String message, RoomBean responseJsonBean) {
                if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                    if (!TextUtils.isEmpty(token)) {
                        ZegoRoomManager.getInstance().roomService.joinRoom(roomID, token, errorCode1 -> {
                            Log.d("Room", "joinRoom: " + errorCode1 + ",roomID:" + roomID);
                            if (errorCode1 == ZegoRoomErrorCode.SUCCESS) {
                                userService.getOnlineRoomUsers((errorCode2, userList1) -> {
                                    updateUserList();
                                });
                            }
                            int tempErrorCode = errorCode;
                            if (errorCode1 == ZIMErrorCode.ROOM_NOT_EXIST.value()) {
                                tempErrorCode = ZegoRoomErrorCode.ROOM_NOT_FOUND;
                            }
                            if (callback != null) {
                                callback.onRoomCallback(tempErrorCode);
                            }
                        });
                    }
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            ZegoRoomListService.heartBeat(userID, roomID, false, null);
                        }
                    };
                    timer.schedule(task, 0,30 * 1000);
                } else {
                    int tempErrorCode = errorCode;
                    if (errorCode == ZegoRoomListService.ROOM_NOT_EXISTED) {
                        tempErrorCode = ZegoRoomErrorCode.ROOM_NOT_FOUND;
                    }
                    if (callback != null) {
                        callback.onRoomCallback(tempErrorCode);
                    }
                }
            }
        });
    }

    public void leaveRoom(ZegoRoomCallback callback) {
        boolean selfHost = UserInfoHelper.isSelfHost();
        String roomID = ZegoRoomManager.getInstance().roomService.roomInfo.getRoomID();
        ZegoRoomManager.getInstance().roomService.leaveRoom(callback);
        Log.d("leaveRoom", "leaveRoom() called with: selfHost = [" + selfHost + "]");
        if (selfHost) {
            ZegoRoomListService.endServerRoom(roomID, new IAsyncGetCallback<RoomBean>() {
                @Override
                public void onResponse(int errorCode, @NonNull String message,
                    RoomBean responseJsonBean) {

                }
            });
        } else {
            String userID = ZegoRoomManager.getInstance().userService.localUserInfo.getUserID();
            ZegoRoomListService.leaveServerRoom(userID, roomID, new IAsyncGetCallback<RoomBean>() {
                @Override
                public void onResponse(int errorCode, @NonNull String message, RoomBean responseJsonBean) {

                }
            });
        }
        stopHeartBeat();
    }

    public void stopHeartBeat() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void enableCamera(boolean enable) {
        isCameraEnable.postValue(enable);
        ZegoRoomManager.getInstance().userService.cameraOperate(enable, errorCode -> {
            if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                isCameraEnable.postValue(!enable);
                ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_room_failed_to_operate));
            }
        });
    }

    public void enableMic(boolean enable) {
        isMicEnable.postValue(enable);
        ZegoRoomManager.getInstance().userService.micOperate(enable, errorCode -> {
            if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                isMicEnable.postValue(!enable);
                ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_room_failed_to_operate));
            }
        });
    }

    public void sendTextMessage(String imText) {
        ZegoMessageService service = ZegoRoomManager.getInstance().messageService;
        service.sendTextMessage(imText, errorCode -> {
            if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                updateMessageLiveData();
            } else {
                ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_send_message_error, errorCode));
            }
        });
    }

    public void inviteToBeCoHost(String userID, ZegoRoomCallback callback) {
        ZegoRoomManager.getInstance().userService.addCoHost(userID, errorCode -> {
            if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                updateUserList();
            }
            if (callback != null) {
                callback.onRoomCallback(errorCode);
            }
        });
    }

    public void respondCoHostInvitation(boolean accept, String operateUserID, ZegoRoomCallback callback) {
        ZegoRoomManager.getInstance().userService.respondCoHostInvitation(accept, operateUserID, errorCode -> {
            if (callback != null) {
                callback.onRoomCallback(errorCode);
            }
        });
    }

    public void requestToBeCoHost(ZegoRoomCallback callback) {
        ZegoRoomManager.getInstance().userService.requestToCoHost(callback);
    }

    public void cancelRequestToBeCoHost(ZegoRoomCallback callback) {
        ZegoRoomManager.getInstance().userService.cancelRequestToCoHost(callback);
    }

    public void respondToBeCoHostRequest(boolean agree, String userID, ZegoRoomCallback callback) {
        ZegoRoomManager.getInstance().userService.respondCoHostRequest(agree, userID, callback);
    }

    public void muteUser(boolean isMuted, String userID, ZegoRoomCallback callback) {
        ZegoRoomManager.getInstance().userService.muteUser(isMuted, userID, callback);
    }

    public void takeSeat(ZegoRoomCallback callback) {
        ZegoRoomManager.getInstance().userService.takeSeat(errorCode -> {
            if (callback != null) {
                callback.onRoomCallback(errorCode);
            }
        });
    }

    public void leaveSeat(String userID, ZegoRoomCallback callback) {
        ZegoRoomManager.getInstance().userService.leaveSeat(userID, callback);
    }

    public boolean isCoHostMax() {
        return ZegoRoomManager.getInstance().userService.coHostList.size() >= ZegoRoomConstants.MAX_CO_HOST_LIST_SIZE;
    }

    private void updateMessageLiveData() {
        List<ZegoTextMessage> messages = ZegoRoomManager.getInstance().messageService.getMessageList();
        List<ZegoTextMessage> fullMessages = (ArrayList<ZegoTextMessage>) CollectionUtils
            .union(messages, joinLeaveMessages);
        Collections.sort(fullMessages);
        textMessageList.postValue(fullMessages);
    }

    private void updateUserList() {
        userList.postValue(ZegoRoomManager.getInstance().userService.getUserList());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // reset face beautify
        ZegoFaceBeautifyService beautifyService = ZegoRoomManager.getInstance().faceBeautifyService;
        beautifyService.resetBeauty();
        beautifyService.resetReSharp();

        ZegoSoundEffectService soundEffectService = ZegoRoomManager.getInstance().soundEffectService;
        soundEffectService.reset();

        stopPreview();
    }
}