package im.zego.live.model.enums;

/**
 * Created by rock on 2022/1/17.
 */
public enum ZegoAudioBitrate {
    AUDIO_BITRATE_16KBPS(16),
    AUDIO_BITRATE_48KBPS(48),
    AUDIO_BITRATE_56KBPS(56),
    AUDIO_BITRATE_128KBPS(128),
    AUDIO_BITRATE_192KBPS(192);

    private final int value;

    ZegoAudioBitrate(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
