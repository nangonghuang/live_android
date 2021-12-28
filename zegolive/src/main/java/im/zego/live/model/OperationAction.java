package im.zego.live.model;

/**
 * Created by rocket_wang on 2021/12/27.
 */
public class OperationAction {
    private int seq;
    private OperationActionType type;
    private String targetID;
    private String operatorID;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public OperationActionType getType() {
        return type;
    }

    public void setType(OperationActionType type) {
        this.type = type;
    }

    public String getTargetID() {
        return targetID;
    }

    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }

    public String getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(String operatorID) {
        this.operatorID = operatorID;
    }

    @Override
    public String toString() {
        return "OperationAction{" +
                "seq=" + seq +
                ", type=" + type +
                ", targetID='" + targetID + '\'' +
                ", operatorID='" + operatorID + '\'' +
                '}';
    }
}