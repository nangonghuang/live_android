package im.zego.livedemo.feature.live.viewmodel;

import androidx.lifecycle.ViewModel;

import com.blankj.utilcode.util.StringUtils;

import im.zego.live.ZegoRoomManager;
import im.zego.live.model.enums.ZegoAudioBitrate;
import im.zego.live.model.enums.ZegoDevicesType;
import im.zego.live.model.enums.ZegoVideoCode;
import im.zego.live.model.enums.ZegoVideoResolution;
import im.zego.live.service.ZegoDeviceService;
import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.model.VideoSettingConfig;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoVideoCodecID;
import im.zego.zegoexpress.constants.ZegoVideoConfigPreset;

/**
 * Created by rocket_wang on 2022/1/5.
 */
public class VideoConfigViewModel extends ViewModel {

    public final String[] encodingTypeStringArray = StringUtils.getStringArray(R.array.encoding_type);
    public final String[] videoResolutionStringArray = StringUtils.getStringArray(R.array.video_resolution);
    public final String[] audioBitrateStringArray = StringUtils.getStringArray(R.array.audio_bitrate);

    private final VideoSettingConfig settingConfig = new VideoSettingConfig();

    public VideoSettingConfig getSettingConfig() {
        return settingConfig;
    }

    public void init() {
        settingConfig.setEncodeType(encodingTypeStringArray[0]);
        settingConfig.setLayeredCoding(false);
        settingConfig.setHardwareEncode(true);
        settingConfig.setHardwareDecode(false);
        settingConfig.setBackgroundNoiseReduction(false);
        settingConfig.setEchoCancellation(false);
        settingConfig.setMicVolumeAutoAdjustment(false);
        settingConfig.setVideoResolution(videoResolutionStringArray[1]);
        settingConfig.setAudioBitrate(audioBitrateStringArray[0]);
    }

    public void updateVideoConfig() {
        ZegoDeviceService deviceService = ZegoRoomManager.getInstance().deviceService;

        int index = 0;
        for (int i = 0; i < ZegoVideoConfigPreset.values().length; i++) {
            String enumName = ZegoVideoConfigPreset.values()[i].name();
            if (settingConfig.getVideoResolution().contains(enumName.replaceAll("\\D+", ""))) {
                index = i;
                break;
            }
        }
        deviceService.setVideoResolution(ZegoVideoResolution.getVideoResolution(index));

        if (settingConfig.isLayeredCoding()) {
            deviceService.setDeviceStatus(ZegoDevicesType.LAYERED_CODING, true);
        } else if (VideoSettingConfig.isH265(settingConfig.getEncodeType())) {
            deviceService.setVideoCodec(ZegoVideoCode.H265);
        } else {
            deviceService.setVideoCodec(ZegoVideoCode.H264);
        }

        int audioBitrate = VideoSettingConfig.calculateAudioBitrate(settingConfig.getAudioBitrate());
        deviceService.setAudioBitrate(ZegoAudioBitrate.getAudioBitrate(audioBitrate));

        deviceService.setDeviceStatus(ZegoDevicesType.HARDWARE_ENCODER, settingConfig.isHardwareEncode());
        deviceService.setDeviceStatus(ZegoDevicesType.HARDWARE_DECODER, settingConfig.isHardwareDecode());

        deviceService.setDeviceStatus(ZegoDevicesType.NOISE_SUPPRESSION, settingConfig.isBackgroundNoiseReduction());

        deviceService.setDeviceStatus(ZegoDevicesType.ECHO_CANCELLATION, settingConfig.isEchoCancellation());

        deviceService.setDeviceStatus(ZegoDevicesType.VOLUME_ADJUSTMENT, settingConfig.isMicVolumeAutoAdjustment());
    }

    public boolean isDeviceSupportH265() {
        boolean tempBool = settingConfig.isHardwareEncode();
        ZegoExpressEngine.getEngine().enableHardwareEncoder(true);
        boolean isVideoEncoderSupported = ZegoExpressEngine.getEngine().isVideoEncoderSupported(ZegoVideoCodecID.H265);
        ZegoExpressEngine.getEngine().enableHardwareEncoder(tempBool);
        return isVideoEncoderSupported;
    }
}