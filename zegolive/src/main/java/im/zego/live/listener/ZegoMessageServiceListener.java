package im.zego.live.listener;

import im.zego.live.model.ZegoTextMessage;

public interface ZegoMessageServiceListener {

    void onReceiveTextMessage(ZegoTextMessage textMessage, String roomID);
}
