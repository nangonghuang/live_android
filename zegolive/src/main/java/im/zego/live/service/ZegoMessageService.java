package im.zego.live.service;

import im.zego.live.ZegoRoomManager;
import im.zego.live.ZegoZIMManager;
import im.zego.live.callback.ZegoRoomCallback;
import im.zego.live.listener.ZegoMessageServiceListener;
import im.zego.live.model.ZegoTextMessage;
import im.zego.live.model.ZegoUserInfo;
import im.zego.zim.ZIM;
import im.zego.zim.entity.ZIMMessage;
import im.zego.zim.entity.ZIMTextMessage;
import im.zego.zim.enums.ZIMErrorCode;
import im.zego.zim.enums.ZIMMessageType;
import java.util.ArrayList;
import java.util.List;

/**
 * manage room text message.
 */
public class ZegoMessageService {

    private ZegoMessageServiceListener messageServiceListener;
    private List<ZegoTextMessage> messageList;

    public ZegoMessageService() {
        messageList = new ArrayList<>();
    }

    /**
     * send text message to room.
     *
     * @param text     message text
     * @param callback operation result callback
     */
    public void sendTextMessage(String text, ZegoRoomCallback callback) {
        ZegoUserInfo localUserInfo = ZegoRoomManager
            .getInstance().userService.localUserInfo;
        ZegoTextMessage textMessage = new ZegoTextMessage();
        textMessage.message = text;
        textMessage.userID = localUserInfo.getUserID();
        String roomID = ZegoRoomManager.getInstance().roomService.roomInfo.getRoomID();
        ZegoZIMManager.getInstance().zim.sendRoomMessage(textMessage, roomID, (message, errorInfo) -> {
            if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                messageList.add(textMessage);
            }
            if (callback != null) {
                callback.roomCallback(errorInfo.code.value());
            }
        });
    }

    public void onReceiveRoomMessage(ZIM zim, ArrayList<ZIMMessage> messageList, String fromRoomID) {
        for (ZIMMessage zimMessage : messageList) {
            if (zimMessage.type == ZIMMessageType.TEXT) {
                ZIMTextMessage zimTextMessage = (ZIMTextMessage) zimMessage;
                ZegoTextMessage textMessage = new ZegoTextMessage();
                textMessage.message = zimTextMessage.message;
                textMessage.userID = zimTextMessage.userID;
                textMessage.messageID = zimTextMessage.messageID;
                textMessage.type = zimTextMessage.type;
                textMessage.priority = zimTextMessage.priority;
                textMessage.timestamp = zimTextMessage.timestamp;
                messageList.add(textMessage);
                if (messageServiceListener != null) {
                    messageServiceListener.onReceiveTextMessage(textMessage, fromRoomID);
                }
            }
        }
    }

    void reset() {
        messageList.clear();
        messageServiceListener = null;
    }

    public void setListener(ZegoMessageServiceListener listener) {
        this.messageServiceListener = listener;
    }

    public List<ZegoTextMessage> getMessageList() {
        return messageList;
    }
}
