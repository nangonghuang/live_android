package im.zego.livedemo.feature.live.model;

/**
 * Created by rocket_wang on 2022/1/4.
 */
public class VideoSettingConfig {
    private String encodeType;
    private boolean layeredCoding;
    private boolean hardwareCoding;
    private boolean hardwareDecoding;
    private String videoResolution;
    private String audioBitrate;

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

    public boolean isHardwareCoding() {
        return hardwareCoding;
    }

    public void setHardwareCoding(boolean hardwareCoding) {
        this.hardwareCoding = hardwareCoding;
    }

    public boolean isHardwareDecoding() {
        return hardwareDecoding;
    }

    public void setHardwareDecoding(boolean hardwareDecoding) {
        this.hardwareDecoding = hardwareDecoding;
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

    @Override
    public String toString() {
        return "VideoSettingConfig{" +
                "encodeType='" + encodeType + '\'' +
                ", layeredCoding=" + layeredCoding +
                ", hardwareCoding=" + hardwareCoding +
                ", hardwareDecoding=" + hardwareDecoding +
                ", videoResolution='" + videoResolution + '\'' +
                ", audioBitrate='" + audioBitrate + '\'' +
                '}';
    }
}