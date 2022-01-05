package im.zego.livedemo.feature.live.viewmodel;

import androidx.lifecycle.ViewModel;

import java.util.Objects;

import im.zego.livedemo.feature.live.model.VideoSettingConfig;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoVideoCodecID;
import im.zego.zegoexpress.constants.ZegoVideoConfigPreset;
import im.zego.zegoexpress.entity.ZegoAudioConfig;
import im.zego.zegoexpress.entity.ZegoVideoConfig;

/**
 * Created by rocket_wang on 2022/1/5.
 */
public class VideoConfigViewModel extends ViewModel {

    private VideoSettingConfig settingConfig = new VideoSettingConfig();

    public VideoSettingConfig getSettingConfig() {
        return settingConfig;
    }

    public void updateVideoConfig() {
        int index = 0;
        for (int i = 0; i < ZegoVideoConfigPreset.values().length; i++) {
            String enumName = ZegoVideoConfigPreset.values()[i].name();
            if (settingConfig.getVideoResolution().contains(enumName.replaceAll("\\D+", ""))) {
                index = i;
                break;
            }
        }
        ZegoVideoConfigPreset configPreset = ZegoVideoConfigPreset.getZegoVideoConfigPreset(index);
        ZegoVideoConfig videoConfig = new ZegoVideoConfig(configPreset);
        if (settingConfig.isLayeredCoding()) {
            videoConfig.setCodecID(ZegoVideoCodecID.SVC);
        } else if (Objects.equals(settingConfig.getEncodeType(), "H.265")) {
            videoConfig.setCodecID(ZegoVideoCodecID.H265);
        } else {
            videoConfig.setCodecID(ZegoVideoCodecID.DEFAULT);
        }
        ZegoExpressEngine.getEngine().setVideoConfig(videoConfig);

        ZegoAudioConfig audioConfig = new ZegoAudioConfig();
        audioConfig.bitrate = VideoSettingConfig.calculateAudioBitrate(settingConfig.getAudioBitrate());
        ZegoExpressEngine.getEngine().setAudioConfig(audioConfig);

        ZegoExpressEngine.getEngine().enableHardwareEncoder(settingConfig.isHardwareEncode());
        ZegoExpressEngine.getEngine().enableHardwareDecoder(settingConfig.isHardwareDecode());

        ZegoExpressEngine.getEngine().enableANS(settingConfig.isBackgroundNoiseReduction());
        ZegoExpressEngine.getEngine().enableTransientANS(settingConfig.isBackgroundNoiseReduction());

        ZegoExpressEngine.getEngine().enableAEC(settingConfig.isEchoCancellation());

        ZegoExpressEngine.getEngine().enableAGC(settingConfig.isMicVolumeAutoAdjustment());
    }
}