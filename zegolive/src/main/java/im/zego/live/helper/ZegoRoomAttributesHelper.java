package im.zego.live.helper;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import im.zego.live.ZegoRoomManager;
import im.zego.live.ZegoZIMManager;
import im.zego.live.callback.ZegoRoomCallback;
import im.zego.live.model.OperationActionType;
import im.zego.live.model.OperationCommand;
import im.zego.live.model.ZegoCoHostSeatModel;
import im.zego.live.model.ZegoRoomInfo;
import im.zego.live.model.ZegoUserInfo;
import im.zego.live.util.Triple;
import im.zego.zim.entity.ZIMRoomAttributesSetConfig;

/**
 * Created by rocket_wang on 2021/12/14.
 */
public class ZegoRoomAttributesHelper {
    private static final String TAG = "ZegoRoomAttributes";

    public static Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new CustomTypeAdapterFactory())
            .create();

    public static ZIMRoomAttributesSetConfig getAttributesSetConfig() {
        ZIMRoomAttributesSetConfig setConfig = new ZIMRoomAttributesSetConfig();
        setConfig.isForce = true;
        setConfig.isDeleteAfterOwnerLeft = true;
        setConfig.isUpdateOwner = true;
        return setConfig;
    }

    public static Triple<HashMap<String, String>, String, ZIMRoomAttributesSetConfig> getRequestOrCancelToHostParameters(boolean isRequest) {
        ZegoRoomInfo roomInfo = ZegoRoomManager.getInstance().roomService.roomInfo;
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        String roomID = roomInfo.getRoomID();
        String hostID = roomInfo.getHostID();
        String myUserID = selfUser.getUserID();

        OperationCommand operation = ZegoRoomManager.getInstance().roomService.operation.copy();
        operation.getAction().setSeq(operation.getAction().getSeq() + 1);
        operation.getAction().setOperatorID(myUserID);
        operation.getAction().setTargetID(myUserID);

        if (isRequest) {
            operation.getAction().setType(OperationActionType.RequestToCoHost);
            operation.getRequestCoHostList().add(myUserID);
        } else {
            operation.getAction().setType(OperationActionType.CancelRequestCoHost);
            operation.getRequestCoHostList().remove(myUserID);
        }

        return Triple.create(operation.getAttributes(OperationCommand.OperationAttributeTypeRequestCoHost),
                roomID,
                getAttributesSetConfig());
    }

    public static Triple<HashMap<String, String>, String, ZIMRoomAttributesSetConfig> getRespondCoHostParameters(boolean isAgree, String targetUserID) {
        ZegoRoomInfo roomInfo = ZegoRoomManager.getInstance().roomService.roomInfo;
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        String roomID = roomInfo.getRoomID();
        String myUserID = selfUser.getUserID();

        OperationCommand operation = ZegoRoomManager.getInstance().roomService.operation.copy();
        operation.getAction().setSeq(operation.getAction().getSeq() + 1);
        operation.getAction().setOperatorID(myUserID);
        operation.getAction().setTargetID(targetUserID);

        if (!operation.getRequestCoHostList().contains(targetUserID)) {
            Log.e(TAG, "the user ID did not in coHost list.");
            return null;
        }

        if (isAgree) {
            operation.getAction().setType(OperationActionType.AgreeToCoHost);
        } else {
            operation.getAction().setType(OperationActionType.DeclineToCoHost);
        }
        operation.getRequestCoHostList().remove(targetUserID);

        return Triple.create(operation.getAttributes(OperationCommand.OperationAttributeTypeRequestCoHost),
                roomID,
                getAttributesSetConfig());
    }

    public static Triple<HashMap<String, String>, String, ZIMRoomAttributesSetConfig> getTakeOrLeaveSeatParameters(String userID, boolean isTake) {
        ZegoRoomInfo roomInfo = ZegoRoomManager.getInstance().roomService.roomInfo;
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        String roomID = roomInfo.getRoomID();
        String myUserID = selfUser.getUserID();
        String targetID = myUserID;
        if (StringUtils.isNotEmpty(userID)) {
            targetID = userID;
        }

        OperationCommand operation = ZegoRoomManager.getInstance().roomService.operation.copy();
        operation.getAction().setSeq(operation.getAction().getSeq() + 1);
        operation.getAction().setOperatorID(myUserID);
        operation.getAction().setTargetID(targetID);

        if (isTake) {
            operation.getAction().setType(OperationActionType.TakeCoHostSeat);
            // when user taking seat, we need set these default params actively
            ZegoCoHostSeatModel model = new ZegoCoHostSeatModel();
            model.setCamera(true);
            model.setMic(true);
            model.setUserID(targetID);
            operation.getSeatList().add(model);
        } else {
            operation.getAction().setType(OperationActionType.LeaveCoHostSeat);
            ZegoCoHostSeatModel model = new ZegoCoHostSeatModel();
            model.setUserID(targetID);
            operation.getSeatList().remove(model);
        }

        return Triple.create(operation.getAttributes(OperationCommand.OperationAttributeTypeSeat),
                roomID,
                getAttributesSetConfig());
    }

    // get the seat parameters
    // flag: 0 - mic, 1 - camera, 2 - mute
    public static Triple<HashMap<String, String>, String, ZIMRoomAttributesSetConfig> getSeatChangeParameters(String targetUserID, boolean enable, int flag) {
        ZegoRoomInfo roomInfo = ZegoRoomManager.getInstance().roomService.roomInfo;
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        String roomID = roomInfo.getRoomID();
        String myUserID = selfUser.getUserID();

        OperationCommand operation = ZegoRoomManager.getInstance().roomService.operation.copy();
        operation.getAction().setSeq(operation.getAction().getSeq() + 1);
        operation.getAction().setOperatorID(myUserID);
        operation.getAction().setTargetID(targetUserID);

        ZegoCoHostSeatModel seatModel = null;
        for (ZegoCoHostSeatModel model : operation.getSeatList()) {
            if (Objects.equals(model.getUserID(), targetUserID)) {
                seatModel = model;
            }
        }

        if (seatModel == null) {
            return null;
        }

        switch (flag) {
            case 0:
                seatModel.setMic(enable);
                operation.getAction().setType(OperationActionType.Mic);
                break;
            case 1:
                seatModel.setCamera(enable);
                operation.getAction().setType(OperationActionType.Camera);
                break;
            case 2:
                seatModel.setMuted(enable);
                operation.getAction().setType(OperationActionType.Mute);
                break;
        }

        return Triple.create(operation.getAttributes(OperationCommand.OperationAttributeTypeSeat),
                roomID,
                getAttributesSetConfig());
    }

    public static void setRoomAttributes(HashMap<String, String> roomAttributes, String roomID, ZIMRoomAttributesSetConfig config, ZegoRoomCallback callback) {
        ZegoZIMManager.getInstance().zim.setRoomAttributes(roomAttributes, roomID, config, errorInfo -> {
            Log.d(TAG, "setRoomAttributes() called with: roomAttributes = [" + Collections.singletonList(roomAttributes) + "]," +
                    " roomID = [" + roomID + "]," +
                    " config = [" + config + "]," +
                    " errorInfo = [" + errorInfo.code + "]");
            if (callback != null) {
                callback.onRoomCallback(errorInfo.code.value());
            }
        });
    }
}