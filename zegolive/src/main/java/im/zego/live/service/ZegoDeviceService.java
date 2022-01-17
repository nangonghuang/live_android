package im.zego.live.service;


import android.view.TextureView;

import im.zego.live.model.enums.ZegoAudioBitrate;
import im.zego.live.model.enums.ZegoDevicesType;
import im.zego.live.model.enums.ZegoVideoCode;
import im.zego.live.model.enums.ZegoVideoFrameRate;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoVideoCodecID;
import im.zego.zegoexpress.constants.ZegoVideoConfigPreset;
import im.zego.zegoexpress.constants.ZegoViewMode;
import im.zego.zegoexpress.entity.ZegoAudioConfig;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zegoexpress.entity.ZegoVideoConfig;

/**
 * Created by rock on 2022/1/17.
 */
public class ZegoDeviceService {
    private ZegoVideoFrameRate videoFrameRate;
    private ZegoAudioBitrate audioBitrate;
    private ZegoVideoCode videoCodec;

    public void setFrameRate(ZegoVideoFrameRate videoFrameRate) {
        this.videoFrameRate = videoFrameRate;
    }

    public void setAudioBitrate(ZegoAudioBitrate audioBitrate) {
        this.audioBitrate = audioBitrate;

        ZegoAudioConfig audioConfig = new ZegoAudioConfig();
        audioConfig.bitrate = audioBitrate.value();
        ZegoExpressEngine.getEngine().setAudioConfig(audioConfig);
    }

    public void setVideoCodec(ZegoVideoCode videoCodec) {
        this.videoCodec = videoCodec;
    }

    public void setDeviceStatus(ZegoDevicesType devicesType, boolean enable) {
        ZegoVideoConfigPreset configPreset = ZegoVideoConfigPreset.getZegoVideoConfigPreset(this.videoFrameRate.value());
        ZegoVideoConfig videoConfig = new ZegoVideoConfig(configPreset);
        if (this.videoCodec == ZegoVideoCode.H265) {
            videoConfig.setCodecID(ZegoVideoCodecID.H265);
        } else {
            videoConfig.setCodecID(ZegoVideoCodecID.DEFAULT);
        }
        switch (devicesType) {
            case LAYERED_CODING:
                videoConfig.setCodecID(ZegoVideoCodecID.SVC);
                ZegoExpressEngine.getEngine().setVideoConfig(videoConfig);
                break;
            case HARDWARE_ENCODER:
                ZegoExpressEngine.getEngine().enableHardwareEncoder(enable);
                break;
            case HARDWARE_DECODER:
                ZegoExpressEngine.getEngine().enableHardwareDecoder(enable);
                break;
            case NOISE_SUPPRESSION:
                ZegoExpressEngine.getEngine().enableANS(enable);
                ZegoExpressEngine.getEngine().enableTransientANS(enable);
                break;
            case ECHO_CANCELLATION:
                ZegoExpressEngine.getEngine().enableAEC(enable);
                break;
            case VOLUME_ADJUSTMENT:
                ZegoExpressEngine.getEngine().enableAGC(enable);
                break;
        }
    }

    public void enableCamera(boolean enable) {
        ZegoExpressEngine.getEngine().enableCamera(enable);
    }

    public void muteMic(boolean mute) {
        ZegoExpressEngine.getEngine().muteMicrophone(mute);
    }

    public void useFrontCamera(boolean isFront) {
        ZegoExpressEngine.getEngine().useFrontCamera(isFront);
    }

    public void playVideoStream(String streamID, TextureView textureView) {
        ZegoCanvas zegoCanvas = new ZegoCanvas(textureView);
        zegoCanvas.viewMode = ZegoViewMode.ASPECT_FILL;
        ZegoExpressEngine.getEngine().startPlayingStream(streamID, zegoCanvas);
    }
}
