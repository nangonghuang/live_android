package im.zego.livedemo.feature.live.model;

import java.util.Objects;

/**
 * Created by rocket_wang on 2022/1/4.
 */
public class VideoSettingConfig {
    private String encodeType;
    private boolean layeredCoding;
    private boolean hardwareEncode = true;
    private boolean hardwareDecode;
    private String videoResolution;
    private String audioBitrate;
    private boolean backgroundNoiseReduction;
    private boolean echoCancellation;
    private boolean micVolumeAutoAdjustment;

    public String getEncodeType() {
        return encodeType;
    }

    public void setEncodeType(String encodeType) {
        this.encodeType = encodeType;
    }

    public boolean isLayeredCoding() {
        return layeredCoding;
    }

    public void setLayeredCoding(boolean layeredCoding) {
        this.layeredCoding = layeredCoding;
    }

    public boolean isHardwareEncode() {
        return hardwareEncode;
    }

    public void setHardwareEncode(boolean hardwareEncode) {
        this.hardwareEncode = hardwareEncode;
    }

    public boolean isHardwareDecode() {
        return hardwareDecode;
    }

    public void setHardwareDecode(boolean hardwareDecode) {
        this.hardwareDecode = hardwareDecode;
    }

    public String getVideoResolution() {
        return videoResolution;
    }

    public void setVideoResolution(String videoResolution) {
        this.videoResolution = videoResolution;
    }

    public String getAudioBitrate() {
        return audioBitrate;
    }

    public void setAudioBitrate(String audioBitrate) {
        this.audioBitrate = audioBitrate;
    }

    public static int calculateAudioBitrate(String audioBitrate) {
        return Integer.parseInt(audioBitrate.replace("kbps", ""));
    }

    public boolean isBackgroundNoiseReduction() {
        return backgroundNoiseReduction;
    }

    public void setBackgroundNoiseReduction(boolean backgroundNoiseReduction) {
        this.backgroundNoiseReduction = backgroundNoiseReduction;
    }

    public boolean isEchoCancellation() {
        return echoCancellation;
    }

    public void setEchoCancellation(boolean echoCancellation) {
        this.echoCancellation = echoCancellation;
    }

    public boolean isMicVolumeAutoAdjustment() {
        return micVolumeAutoAdjustment;
    }

    public void setMicVolumeAutoAdjustment(boolean micVolumeAutoAdjustment) {
        this.micVolumeAutoAdjustment = micVolumeAutoAdjustment;
    }

    public static boolean isH265(String encodeType) {
        return Objects.equals(encodeType, "H.265");
    }

    @Override
    public String toString() {
        return "VideoSettingConfig{" +
                "encodeType='" + encodeType + '\'' +
                ", layeredCoding=" + layeredCoding +
                ", hardwareEncode=" + hardwareEncode +
                ", hardwareDecode=" + hardwareDecode +
                ", videoResolution='" + videoResolution + '\'' +
                ", audioBitrate='" + audioBitrate + '\'' +
                ", backgroundNoiseReduction=" + backgroundNoiseReduction +
                ", echoCancellation=" + echoCancellation +
                ", micVolumeAutoAdjustment=" + micVolumeAutoAdjustment +
                '}';
    }
}