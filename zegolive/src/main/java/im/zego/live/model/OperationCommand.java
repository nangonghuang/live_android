package im.zego.live.model;

import androidx.annotation.IntDef;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import im.zego.live.constants.ZegoRoomConstants;
import im.zego.live.helper.ZegoRoomAttributesHelper;

/**
 * Created by rocket_wang on 2021/12/27.
 */
public class OperationCommand {
    @SerializedName("seat")
    private List<ZegoCoHostSeatModel> seatList = new ArrayList<>();
    @SerializedName("requestCoHost")
    private List<String> requestCoHostList = new ArrayList<>();
    @SerializedName("action")
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

    public OperationCommand copy() {
        OperationCommand command = new OperationCommand();
        command.seatList = new ArrayList<>(seatList);
        command.requestCoHostList = new ArrayList<>(requestCoHostList);
        command.action = new OperationAction();
        command.action.setSeq(action.getSeq());
        return command;
    }

    public void update(HashMap<String, String> map) {
        Gson gson = ZegoRoomAttributesHelper.gson;
        Set<String> keys = map.keySet();
        for (String key : keys) {
            switch (key) {
                case ZegoRoomConstants.KEY_ACTION:
                    OperationAction action = gson.fromJson(map.get(key), OperationAction.class);
                    this.action.setSeq(action.getSeq());
                    break;
                case ZegoRoomConstants.KEY_SEAT:
                    List<ZegoCoHostSeatModel> seatList = gson.fromJson(map.get(key), new TypeToken<List<ZegoCoHostSeatModel>>() {}.getType());
                    if (seatList != null && !seatList.isEmpty()) {
                        this.seatList = seatList;
                    }
                    break;
                case ZegoRoomConstants.KEY_REQUEST_CO_HOST:
                    List<String> requestCoHostList = gson.fromJson(map.get(key), new TypeToken<List<String>>() {}.getType());
                    if (requestCoHostList != null && !requestCoHostList.isEmpty()) {
                        this.requestCoHostList = requestCoHostList;
                    }
                    break;
            }
        }
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