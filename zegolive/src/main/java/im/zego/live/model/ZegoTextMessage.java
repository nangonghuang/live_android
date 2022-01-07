package im.zego.live.model;

import im.zego.zim.entity.ZIMTextMessage;

public class ZegoTextMessage extends ZIMTextMessage implements Comparable<ZegoTextMessage> {
    public boolean isRoomUserInfoMessage = false;

    @Override
    public int compareTo(ZegoTextMessage o) {
        return (int) (timestamp - o.timestamp);
    }
}
