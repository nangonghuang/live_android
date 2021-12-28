package im.zego.live.helper;

import com.google.gson.Gson;

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
    public static Gson gson = new Gson();

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
        operation.getAction().setTargetID(hostID);

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

    public static Triple<HashMap<String, String>, String, ZIMRoomAttributesSetConfig> getRespondCoHostRequestParameters(boolean isAgree, String targetUserID) {
        ZegoRoomInfo roomInfo = ZegoRoomManager.getInstance().roomService.roomInfo;
        String roomID = roomInfo.getRoomID();
        String hostID = roomInfo.getHostID();

        OperationCommand operation = ZegoRoomManager.getInstance().roomService.operation.copy();
        operation.getAction().setSeq(operation.getAction().getSeq() + 1);
        operation.getAction().setOperatorID(hostID);
        operation.getAction().setTargetID(targetUserID);

        ZegoCoHostSeatModel model = new ZegoCoHostSeatModel();
        model.setUserID(targetUserID);
        model.setMic(true);
        model.setCamera(true);
        if (isAgree) {
            operation.getAction().setType(OperationActionType.AgreeToCoHost);
            operation.getSeatList().add(model);
        } else {
            operation.getAction().setType(OperationActionType.DeclineToCoHost);
            operation.getSeatList().remove(model);
        }
        operation.getRequestCoHostList().remove(targetUserID);

        return Triple.create(operation.getAttributes(OperationCommand.OperationAttributeTypeSeat),
                roomID,
                getAttributesSetConfig());
    }

    public static Triple<HashMap<String, String>, String, ZIMRoomAttributesSetConfig> getTakeOrLeaveSeatParameters(boolean isTake) {
        ZegoRoomInfo roomInfo = ZegoRoomManager.getInstance().roomService.roomInfo;
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        String roomID = roomInfo.getRoomID();
        String myUserID = selfUser.getUserID();

        OperationCommand operation = ZegoRoomManager.getInstance().roomService.operation.copy();
        operation.getAction().setSeq(operation.getAction().getSeq() + 1);
        operation.getAction().setOperatorID(myUserID);
        operation.getAction().setTargetID(myUserID);

        if (isTake) {
            operation.getAction().setType(OperationActionType.TakeCoHostSeat);
            ZegoCoHostSeatModel model = new ZegoCoHostSeatModel();
            model.setUserID(myUserID);
            operation.getSeatList().add(model);
        } else {
            operation.getAction().setType(OperationActionType.LeaveCoHostSeat);
            ZegoCoHostSeatModel model = new ZegoCoHostSeatModel();
            model.setUserID(myUserID);
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
            if (Objects.equals(model.getUserID(), myUserID)) {
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
            if (callback != null) {
                callback.onRoomCallback(errorInfo.code.value());
            }
        });
    }
}