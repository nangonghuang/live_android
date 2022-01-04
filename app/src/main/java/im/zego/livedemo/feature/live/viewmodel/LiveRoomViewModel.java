package im.zego.livedemo.feature.live.viewmodel;

import android.text.TextUtils;
import android.view.TextureView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import im.zego.live.ZegoRoomManager;
import im.zego.live.callback.ZegoRoomCallback;
import im.zego.live.constants.ZegoRoomErrorCode;
import im.zego.live.listener.ZegoRoomServiceListener;
import im.zego.live.listener.ZegoUserServiceListener;
import im.zego.live.model.OperationAction;
import im.zego.live.model.ZegoCoHostSeatModel;
import im.zego.live.model.ZegoRoomInfo;
import im.zego.live.model.ZegoTextMessage;
import im.zego.live.model.ZegoUserInfo;
import im.zego.live.service.ZegoMessageService;
import im.zego.live.service.ZegoUserService;
import im.zego.livedemo.R;
import im.zego.livedemo.helper.AuthInfoManager;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoOrientation;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.constants.ZegoViewMode;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;

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
            }

            @Override
            public void onConnectionStateChanged(ZIMConnectionState state, ZIMConnectionEvent event) {
                listener.onConnectionStateChanged(state, event);
            }

            @Override
            public void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, List<ZegoStream> streamList) {
                listener.onRoomStreamUpdate(roomID, updateType, streamList);
            }
        });

        ZegoRoomManager.getInstance().userService.setListener(new ZegoUserServiceListener() {
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
                    textMessage.message = StringUtils
                            .getString(R.string.room_page_joined_the_room, localUserInfo.getUserName());
                    textMessage.timestamp = System.currentTimeMillis();
                    joinLeaveMessages.add(textMessage);
                } else {
                    for (ZegoUserInfo user : memberList) {
                        ZegoTextMessage textMessage = new ZegoTextMessage();
                        textMessage.timestamp = System.currentTimeMillis();
                        textMessage.message = StringUtils
                                .getString(R.string.room_page_joined_the_room, user.getUserName());
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
                    textMessage.message = StringUtils
                            .getString(R.string.room_page_has_left_the_room, user.getUserName());
                    textMessage.timestamp = System.currentTimeMillis();
                    joinLeaveMessages.add(textMessage);
                    updateMessageLiveData();
                }
                updateUserList();
            }

            @Override
            public void onReceiveAddCoHostInvitation() {
                listener.onReceiveAddCoHostInvitation();
            }

            @Override
            public void onReceiveAddCoHostRespond(boolean accept) {
                if (!accept) {
                    updateUserList();
                }
                listener.onReceiveAddCoHostRespond(accept);
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
        ZegoCanvas zegoCanvas = new ZegoCanvas(view);
        zegoCanvas.viewMode = ZegoViewMode.ASPECT_FILL;
        ZegoExpressEngine.getEngine().startPlayingStream(streamID, zegoCanvas);
    }

    public void useFrontCamera(boolean enable) {
        ZegoExpressEngine.getEngine().useFrontCamera(enable);
    }

    public void createRoom(String roomID, String roomName, ZegoRoomCallback callback) {
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        String token = AuthInfoManager.getInstance().generateCreateRoomToken(roomID, selfUser.getUserID());
        ZegoRoomManager.getInstance().roomService.createRoom(roomID, roomName, token, errorCode -> {
            if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                takeCoHostSeat(callback);
                return;
            }
            if (callback != null) {
                callback.onRoomCallback(errorCode);
            }
        });
    }

    public void joinRoom(String roomID, ZegoRoomCallback callback) {
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        String token = AuthInfoManager.getInstance().generateJoinRoomToken(selfUser.getUserID());
        if (!TextUtils.isEmpty(token)) {
            ZegoRoomManager.getInstance().roomService.joinRoom(roomID, token, errorCode -> {
                if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                    ZegoRoomManager.getInstance().userService.getOnlineRoomUsers((errorCode1, userList1) -> {
                        updateUserList();
                    });
                }
                if (callback != null) {
                    callback.onRoomCallback(errorCode);
                }
            });
        }
    }

    public void leaveRoom(ZegoRoomCallback callback) {
        ZegoRoomManager.getInstance().roomService.leaveRoom(callback);
    }

    public void enableCamera(boolean enable) {
        isCameraEnable.postValue(enable);
        ZegoRoomManager.getInstance().userService.cameraOperate(enable, errorCode -> {
            if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                isCameraEnable.postValue(!enable);
                ToastUtils.showShort(R.string.toast_operate_camera_fail, errorCode);
            }
        });
    }

    public void enableMic(boolean enable) {
        isMicEnable.postValue(enable);
        ZegoRoomManager.getInstance().userService.micOperate(enable, errorCode -> {
            if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                isMicEnable.postValue(!enable);
                ToastUtils.showShort(R.string.toast_operate_mic_fail, errorCode);
            }
        });
    }

    public void sendTextMessage(String imText) {
        ZegoMessageService service = ZegoRoomManager.getInstance().messageService;
        service.sendTextMessage(imText, errorCode -> {
            if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                updateMessageLiveData();
            } else {
                ToastUtils.showShort(R.string.toast_send_message_error, errorCode);
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

    public void respondCoHostInvitation(boolean accept, ZegoRoomCallback callback) {
        ZegoRoomManager.getInstance().userService.respondCoHostInvitation(accept, errorCode -> {
            if (errorCode == ZegoRoomErrorCode.SUCCESS && accept) {
                takeCoHostSeat(callback);
                return;
            }
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

    public void takeCoHostSeat(ZegoRoomCallback callback) {
        ZegoRoomManager.getInstance().userService.takeCoHostSeat(errorCode -> {
            if (callback != null) {
                callback.onRoomCallback(errorCode);
            }
        });
    }

    public void leaveCoHostSeat(String userID, ZegoRoomCallback callback) {
        ZegoRoomManager.getInstance().userService.leaveCoHostSeat(userID, callback);
    }

    public boolean isCoHostMax() {
        return ZegoRoomManager.getInstance().userService.coHostList.size() >= 3;
    }

    private void updateMessageLiveData() {
        List<ZegoTextMessage> messages = ZegoRoomManager.getInstance().messageService.getMessageList();
        List<ZegoTextMessage> fullMessages = (ArrayList<ZegoTextMessage>) CollectionUtils.union(messages, joinLeaveMessages);
        Collections.sort(fullMessages);
        textMessageList.postValue(fullMessages);
    }

    private void updateUserList() {
        userList.postValue(ZegoRoomManager.getInstance().userService.getUserList());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        destroy();
    }

    private void destroy() {
        stopPreview();
    }
}