package im.zego.live.listener;

import java.util.List;

public interface ZegoGiftServiceListener {

    void onReceiveGift(String giftID,String fromUserID, List<String> toUserList);
}
