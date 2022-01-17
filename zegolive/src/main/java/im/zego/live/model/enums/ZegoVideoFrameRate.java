package im.zego.live.model.enums;

/**
 * Created by rock on 2022/1/17.
 */
public enum ZegoVideoFrameRate {
    VIDEO_RESOLUTION_1080P(5),
    VIDEO_RESOLUTION_720P(4),
    VIDEO_RESOLUTION_540P(3),
    VIDEO_RESOLUTION_360P(2),
    VIDEO_RESOLUTION_270P(1),
    VIDEO_RESOLUTION_180P(0);

    private final int value;

    ZegoVideoFrameRate(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
