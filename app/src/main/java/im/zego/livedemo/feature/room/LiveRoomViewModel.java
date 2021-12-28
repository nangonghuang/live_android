package im.zego.livedemo.feature.room;

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
import im.zego.live.model.ZegoRoomInfo;
import im.zego.live.model.ZegoTextMessage;
import im.zego.live.model.ZegoUserInfo;
import im.zego.live.service.ZegoMessageService;
import im.zego.live.service.ZegoUserService;
import im.zego.live.util.ZegoRTCServerAssistant;
import im.zego.livedemo.KeyCenter;
import im.zego.livedemo.R;
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
    // FIXME: 2021/12/27 for test
    private String roomID = "489";

    public MutableLiveData<Integer> onlineRoomUsersNum = new MutableLiveData<>();
    public MutableLiveData<Boolean> isCameraOpen = new MutableLiveData<>();
    public MutableLiveData<Boolean> isMicOpen = new MutableLiveData<>();
    public MutableLiveData<List<ZegoTextMessage>> textMessageList = new MutableLiveData<>();

    private List<ZegoTextMessage> joinLeaveMessages = new ArrayList<>();

    public void init() {
        ZegoRoomManager.getInstance().roomService.setListener(new ZegoRoomServiceListener() {
            @Override
            public void onReceiveRoomInfoUpdate(ZegoRoomInfo roomInfo) {

            }

            @Override
            public void onConnectionStateChanged(ZIMConnectionState state, ZIMConnectionEvent event) {

            }

            @Override
            public void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, List<ZegoStream> streamList) {

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
                    textMessage.message = StringUtils.getString(R.string.room_page_joined_the_room, localUserInfo.getUserName());
                    textMessage.timestamp = System.currentTimeMillis();
                    joinLeaveMessages.add(textMessage);
                } else {
                    for (ZegoUserInfo user : memberList) {
                        ZegoTextMessage textMessage = new ZegoTextMessage();
                        textMessage.timestamp = System.currentTimeMillis();
                        textMessage.message = StringUtils.getString(R.string.room_page_joined_the_room, user.getUserName());
                        joinLeaveMessages.add(textMessage);
                    }
                }
                updateMessageLiveData();
            }

            @Override
            public void onRoomUserLeave(List<ZegoUserInfo> memberList) {
                for (ZegoUserInfo user : memberList) {
                    ZegoTextMessage textMessage = new ZegoTextMessage();
                    textMessage.message = StringUtils.getString(R.string.room_page_has_left_the_room, user.getUserName());
                    textMessage.timestamp = System.currentTimeMillis();
                    joinLeaveMessages.add(textMessage);
                    updateMessageLiveData();
                }
            }

            @Override
            public void onReceiveAddCoHostInvitation() {

            }

            @Override
            public void onReceiveAddCoHostRespond(boolean accept) {

            }

            @Override
            public void onReceiveToCoHostRequest() {

            }

            @Override
            public void onReceiveCancelToCoHostRequest() {

            }

            @Override
            public void onReceiveToCoHostRespond(boolean agree) {

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

    public void useFrontCamera(boolean enable) {
        ZegoExpressEngine.getEngine().useFrontCamera(enable);
    }

    public void createRoom(String roomName, ZegoRoomCallback callback) {
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        ZegoRTCServerAssistant.Privileges privileges = new ZegoRTCServerAssistant.Privileges();
        privileges.canLoginRoom = true;
        privileges.canPublishStream = true;
        String token = ZegoRTCServerAssistant.generateToken(KeyCenter.appID(), roomID, selfUser.getUserID(), privileges, KeyCenter.appExpressSign(), 660).data;
        ZegoRoomManager.getInstance().roomService.createRoom(roomID, roomName, token, callback);

        onlineRoomUsersNum.postValue(1);
    }

    public void enableCamera(boolean enable) {
        isCameraOpen.postValue(enable);
        ZegoRoomManager.getInstance().userService.cameraOperate(enable, errorCode -> {
            if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                isCameraOpen.postValue(!enable);
                ToastUtils.showShort(R.string.toast_operate_camera_fail, errorCode);
            }
        });
    }

    public void enableMic(boolean enable) {
        isMicOpen.postValue(enable);
        ZegoRoomManager.getInstance().userService.micOperate(enable, errorCode -> {
            if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                isMicOpen.postValue(!enable);
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

    private void updateMessageLiveData() {
        List<ZegoTextMessage> messages = ZegoRoomManager.getInstance().messageService.getMessageList();
        List<ZegoTextMessage> fullMessages = (ArrayList<ZegoTextMessage>) CollectionUtils.union(messages, joinLeaveMessages);
        Collections.sort(fullMessages);
        textMessageList.postValue(fullMessages);
    }

    public void destroy() {
        stopPreview();
    }
}