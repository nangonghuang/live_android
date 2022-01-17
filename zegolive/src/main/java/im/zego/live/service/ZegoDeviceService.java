package im.zego.live.service;


import android.view.TextureView;

import im.zego.live.model.enums.ZegoAudioBitrate;
import im.zego.live.model.enums.ZegoDevicesType;
import im.zego.live.model.enums.ZegoVideoCode;
import im.zego.live.model.enums.ZegoVideoFrameRate;

/**
 * Created by rock on 2022/1/17.
 */
public class ZegoDeviceService {
    public void setFrameRate(ZegoVideoFrameRate videoFrameRate) {
    }

    public void setAudioBitrate(ZegoAudioBitrate audioBitrate) {
    }

    public void setVideoCodec(ZegoVideoCode videoCodec) {
    }

    public void setDeviceStatus(ZegoDevicesType devicesType, boolean enable) {
    }

    public void enableCamera(boolean enable) {
    }

    public void muteMic(boolean mute) {
    }

    public void useFrontCamera(boolean isFront) {
    }

    public void playVideoStream(String streamID, TextureView textureView) {
    }
}
