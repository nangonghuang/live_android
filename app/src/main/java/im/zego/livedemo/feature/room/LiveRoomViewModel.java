package im.zego.livedemo.feature.room;

import android.view.TextureView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import im.zego.live.ZegoRoomManager;
import im.zego.live.callback.ZegoRoomCallback;
import im.zego.live.model.ZegoUserInfo;
import im.zego.live.util.ZegoRTCServerAssistant;
import im.zego.livedemo.KeyCenter;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoOrientation;
import im.zego.zegoexpress.constants.ZegoViewMode;
import im.zego.zegoexpress.entity.ZegoCanvas;

/**
 * Created by rocket_wang on 2021/12/27.
 */
public class LiveRoomViewModel extends ViewModel {
    public MutableLiveData<Integer> onlineRoomUsersNum = new MutableLiveData<>();

    // FIXME: 2021/12/27 for test
    private String roomID = "489";

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

        onlineRoomUsersNum.postValue(2);
    }

    public void enableCamera(boolean enable, ZegoRoomCallback callback) {

    }

    public void enableMic(boolean enable, ZegoRoomCallback callback) {

    }

    public void destroy() {
        stopPreview();
    }
}