package im.zego.live.helper;

import im.zego.zim.entity.ZIMRoomAttributesSetConfig;

/**
 * Created by rocket_wang on 2021/12/14.
 */
public class ZegoRoomAttributesHelper {

    public static ZIMRoomAttributesSetConfig getAttributesSetConfig() {
        ZIMRoomAttributesSetConfig setConfig = new ZIMRoomAttributesSetConfig();
        setConfig.isForce = true;
        setConfig.isDeleteAfterOwnerLeft = true;
        setConfig.isUpdateOwner = false;
        return setConfig;
    }
}