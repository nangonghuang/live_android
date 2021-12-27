package im.zego.live.model;

/**
 * Created by rocket_wang on 2021/12/27.
 */
public enum OperationActionType {
    Mic(100),
    Camera(101),
    Mute(102),
    TakeCoHostSeat(103),
    LeaveCoHostSeat(104),
    RequestToCoHost(200),
    CancelRequestCoHost(201),
    DeclineToCoHost(202),
    AgreeToCoHost(203);

    private int value;

    OperationActionType(int value) {
        this.value = value;
    }
}