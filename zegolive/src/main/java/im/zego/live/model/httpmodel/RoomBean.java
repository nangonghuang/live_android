package im.zego.live.model.httpmodel;

import com.google.gson.annotations.SerializedName;

public class RoomBean {

    @SerializedName("id")
    private String roomID;
    @SerializedName("name")
    private String name;
    @SerializedName("host_id")
    private String hostID;
    @SerializedName("create_time")
    private Long createTime;
    @SerializedName("user_num")
    private Integer userNum;

    public String getRoomID() {
        return roomID;
    }

    public String getName() {
        return name;
    }

    public String getHostID() {
        return hostID;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public Integer getUserNum() {
        return userNum;
    }

    @Override
    public String toString() {
        return "RoomListBean{" +
            "id='" + roomID + '\'' +
            ", name='" + name + '\'' +
            ", hostId='" + hostID + '\'' +
            ", createTime=" + createTime +
            ", userNum=" + userNum +
            '}';
    }
}
