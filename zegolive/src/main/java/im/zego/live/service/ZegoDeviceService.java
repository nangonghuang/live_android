package im.zego.live.service;


import android.view.TextureView;

import im.zego.live.helper.UserInfoHelper;
import im.zego.live.helper.ZegoLiveHelper;
import im.zego.live.model.enums.ZegoAudioBitrate;
import im.zego.live.model.enums.ZegoDevicesType;
import im.zego.live.model.enums.ZegoVideoCode;
import im.zego.live.model.enums.ZegoVideoResolution;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoOrientation;
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

    public void setVideoResolution(ZegoVideoResolution videoResolution) {
        ZegoVideoConfigPreset configPreset = ZegoVideoConfigPreset.getZegoVideoConfigPreset(videoResolution.value());
        ZegoVideoConfig videoConfig = new ZegoVideoConfig(configPreset);
        ZegoExpressEngine.getEngine().setVideoConfig(videoConfig);
    }

    public void setAudioBitrate(ZegoAudioBitrate audioBitrate) {
        ZegoAudioConfig audioConfig = new ZegoAudioConfig();
        audioConfig.bitrate = audioBitrate.value();
        ZegoExpressEngine.getEngine().setAudioConfig(audioConfig);
    }

    public void setVideoCodec(ZegoVideoCode videoCodec) {
        ZegoVideoConfig videoConfig = ZegoExpressEngine.getEngine().getVideoConfig();
        if (videoCodec == ZegoVideoCode.H265) {
            videoConfig.setCodecID(ZegoVideoCodecID.H265);
        } else {
            videoConfig.setCodecID(ZegoVideoCodecID.DEFAULT);
        }
        ZegoExpressEngine.getEngine().setVideoConfig(videoConfig);
    }

    public void setDeviceStatus(ZegoDevicesType devicesType, boolean enable) {
        switch (devicesType) {
            case LAYERED_CODING:
                if (enable) {
                    ZegoVideoConfig videoConfig = ZegoExpressEngine.getEngine().getVideoConfig();
                    videoConfig.setCodecID(ZegoVideoCodecID.SVC);
                    ZegoExpressEngine.getEngine().setVideoConfig(videoConfig);
                }
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

    public void playVideoStream(String userID, TextureView textureView) {
        ZegoCanvas zegoCanvas = new ZegoCanvas(textureView);
        zegoCanvas.viewMode = ZegoViewMode.ASPECT_FILL;

        if (UserInfoHelper.isUserIDSelf(userID)) {
            ZegoExpressEngine.getEngine().setAppOrientation(ZegoOrientation.ORIENTATION_0);
            ZegoExpressEngine.getEngine().startPreview(zegoCanvas);
        } else {
            String streamID = ZegoLiveHelper.getStreamID(userID);
            ZegoExpressEngine.getEngine().startPlayingStream(streamID, zegoCanvas);
        }
    }

    public void stopPlayStream(String userID) {
        if (UserInfoHelper.isUserIDSelf(userID)) {
            ZegoExpressEngine.getEngine().stopPreview();
        } else {
            String streamID = ZegoLiveHelper.getStreamID(userID);
            ZegoExpressEngine.getEngine().stopPlayingStream(streamID);
        }
    }
}
