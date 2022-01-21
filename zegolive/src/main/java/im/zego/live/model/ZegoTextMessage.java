package im.zego.live.model;

import im.zego.zim.entity.ZIMTextMessage;

/**
 * Class IM message
 *
 * Description: This class contains the IM message related information.
 */
public class ZegoTextMessage extends ZIMTextMessage implements Comparable<ZegoTextMessage> {
    public String userName;

    @Override
    public int compareTo(ZegoTextMessage o) {
        return (int) (timestamp - o.timestamp);
    }
}
