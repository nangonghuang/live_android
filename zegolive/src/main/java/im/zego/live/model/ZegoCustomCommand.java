package im.zego.live.model;

import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import im.zego.live.helper.ZegoRoomAttributesHelper;
import im.zego.zim.entity.ZIMCustomMessage;

public class ZegoCustomCommand extends ZIMCustomMessage {

    @SerializedName("actionType")
    public CustomCommandType actionType;

    @SerializedName("target")
    public List<String> targetUserIDs = new ArrayList<>();

    @SerializedName("content")
    public CustomCommandContent content;

    public void toJson() {
        message = ZegoRoomAttributesHelper.gson.toJson(this).getBytes(StandardCharsets.UTF_8);
    }

    public void fromJson(byte[] message) {
        ZegoCustomCommand command = ZegoRoomAttributesHelper.gson.fromJson(new String(message), ZegoCustomCommand.class);
        this.message = command.message;
        actionType = command.actionType;
        targetUserIDs = command.targetUserIDs;
        content = command.content;
    }

    public static class CustomCommandContent {
        public boolean accept;

        public CustomCommandContent(boolean accept) {
            this.accept = accept;
        }
    }

    public enum CustomCommandType {
        Invitation(1),
        RespondInvitation(2);

        private final int value;

        CustomCommandType(int value) {
            this.value = value;
        }
    }
}