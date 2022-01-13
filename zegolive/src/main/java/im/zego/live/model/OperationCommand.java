package im.zego.live.model;

import androidx.annotation.IntDef;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import im.zego.live.constants.ZegoRoomConstants;
import im.zego.live.helper.UserInfoHelper;
import im.zego.live.helper.ZegoRoomAttributesHelper;

/**
 * Created by rocket_wang on 2021/12/27.
 */
public class OperationCommand {
    @SerializedName(ZegoRoomConstants.KEY_SEAT)
    private List<ZegoCoHostSeatModel> seatList = new ArrayList<>();
    @SerializedName(ZegoRoomConstants.KEY_REQUEST_CO_HOST)
    private List<String> requestCoHostList = new ArrayList<>();
    @SerializedName(ZegoRoomConstants.KEY_ACTION)
    private OperationAction action = new OperationAction();

    public List<ZegoCoHostSeatModel> getSeatList() {
        return seatList;
    }

    public void setSeatList(List<ZegoCoHostSeatModel> seatList) {
        this.seatList = seatList;
    }

    public List<String> getRequestCoHostList() {
        return requestCoHostList;
    }

    public void setRequestCoHostList(List<String> requestCoHostList) {
        this.requestCoHostList = requestCoHostList;
    }

    public OperationAction getAction() {
        return action;
    }

    public void setAction(OperationAction action) {
        this.action = action;
    }

    public boolean isSeqValid(int seq) {
        if (seq == 0 && action.getSeq() == 0) {
            return true;
        }
        return seq > 0 && seq > action.getSeq();
    }

    public void addSeq(int requestSeq) {
        if (requestSeq > action.getSeq()) {
            action.setSeq(requestSeq + 1);
        } else {
            action.setSeq(action.getSeq() + 1);
        }
    }

    public OperationCommand copy() {
        OperationCommand command = new OperationCommand();
        command.seatList = new ArrayList<>(seatList);
        command.requestCoHostList = new ArrayList<>(requestCoHostList);
        command.action = new OperationAction();
        command.action.setSeq(action.getSeq());
        return command;
    }

    public void updateForResend(HashMap<String, String> map) {
        Gson gson = ZegoRoomAttributesHelper.gson;
        Set<String> keys = map.keySet();
        // according to the action
        // Modify the values of various member properties of the operation
        for (String key : keys) {
            switch (key) {
                case ZegoRoomConstants.KEY_SEAT:
                    List<ZegoCoHostSeatModel> seatList = gson.fromJson(map.get(key), new TypeToken<ArrayList<ZegoCoHostSeatModel>>() {}.getType());
                    if (seatList != null) {
                        if (action.getType() == OperationActionType.TakeCoHostSeat) {
                            // If the action is take seat,
                            // we need to filter duplicate requests to avoid simultaneous operations
                            this.seatList.addAll(seatList);
                            this.seatList = removeDuplicateItem(this.seatList);
                        } else if (action.getType() == OperationActionType.LeaveCoHostSeat) {
                            ZegoCoHostSeatModel seatModel = UserInfoHelper.getSeatModel(this.seatList, action.getTargetID());
                            this.seatList.remove(seatModel);
                        } else {
                            this.seatList = removeDuplicateItem(seatList);
                        }
                    }
                    break;
                case ZegoRoomConstants.KEY_REQUEST_CO_HOST:
                    List<String> requestCoHostList = gson.fromJson(map.get(key), new TypeToken<ArrayList<String>>() {}.getType());
                    if (requestCoHostList != null) {
                        if (action.getType() == OperationActionType.RequestToCoHost) {
                            this.requestCoHostList.addAll(requestCoHostList);
                            this.requestCoHostList = removeDuplicateItem(this.requestCoHostList);
                        } else if (action.getType() == OperationActionType.CancelRequestCoHost) {
                            this.requestCoHostList.remove(action.getOperatorID());
                        } else {
                            this.requestCoHostList = removeDuplicateItem(requestCoHostList);
                        }
                    }
                    break;
            }
        }
    }

    public void update(HashMap<String, String> map) {
        Gson gson = ZegoRoomAttributesHelper.gson;
        Set<String> keys = map.keySet();
        // according to the action
        // Modify the values of various member properties of the operation
        for (String key : keys) {
            switch (key) {
                case ZegoRoomConstants.KEY_SEAT:
                    List<ZegoCoHostSeatModel> seatList = gson.fromJson(map.get(key), new TypeToken<ArrayList<ZegoCoHostSeatModel>>() {}.getType());
                    if (seatList != null) {
                        this.seatList = removeDuplicateItem(seatList);
                    }
                    break;
                case ZegoRoomConstants.KEY_REQUEST_CO_HOST:
                    List<String> requestCoHostList = gson.fromJson(map.get(key), new TypeToken<ArrayList<String>>() {}.getType());
                    if (requestCoHostList != null) {
                        this.requestCoHostList = removeDuplicateItem(requestCoHostList);
                    }
                    break;
            }
        }
    }

    private <T> ArrayList<T> removeDuplicateItem(List<T> listWithDuplicates) {
        return new ArrayList<>(new LinkedHashSet<>(listWithDuplicates));
    }

    public HashMap<String, String> getAttributes(@OperationAttributeType int type) {
        Gson gson = ZegoRoomAttributesHelper.gson;
        HashMap<String, String> map = new HashMap<>();
        map.put(ZegoRoomConstants.KEY_ACTION, gson.toJson(action));

        if ((type & OperationAttributeTypeSeat) != 0) {
            map.put(ZegoRoomConstants.KEY_SEAT, gson.toJson(seatList));
        }

        if ((type & OperationAttributeTypeRequestCoHost) != 0) {
            map.put(ZegoRoomConstants.KEY_REQUEST_CO_HOST, gson.toJson(requestCoHostList));
        }

        return map;
    }

    public static final int OperationAttributeTypeSeat = 1;
    public static final int OperationAttributeTypeRequestCoHost = 1 << 1;
    public static final int OperationAttributeTypeAll = OperationAttributeTypeSeat | OperationAttributeTypeRequestCoHost;

    @IntDef(flag = true, value = {
            OperationAttributeTypeSeat,
            OperationAttributeTypeRequestCoHost,
            OperationAttributeTypeAll
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface OperationAttributeType {
    }
}