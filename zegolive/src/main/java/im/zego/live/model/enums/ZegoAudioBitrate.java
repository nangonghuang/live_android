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

    public static ZegoAudioBitrate getAudioBitrate(int value) {
        if (AUDIO_BITRATE_16KBPS.value == value) {
            return AUDIO_BITRATE_16KBPS;
        } else if (AUDIO_BITRATE_48KBPS.value == value) {
            return AUDIO_BITRATE_48KBPS;
        } else if (AUDIO_BITRATE_56KBPS.value == value) {
            return AUDIO_BITRATE_56KBPS;
        } else if (AUDIO_BITRATE_128KBPS.value == value) {
            return AUDIO_BITRATE_128KBPS;
        } else {
            return AUDIO_BITRATE_192KBPS.value == value ? AUDIO_BITRATE_192KBPS : null;
        }
    }
}
