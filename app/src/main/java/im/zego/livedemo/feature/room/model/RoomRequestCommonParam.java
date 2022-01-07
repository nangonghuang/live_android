package im.zego.livedemo.feature.room.model;

import com.google.gson.annotations.SerializedName;

public class RoomRequestCommonParam {

    @SerializedName("name")
    public String name;
    @SerializedName("user_id")
    public String userID;
    @SerializedName("host_id")
    public String hostID;
    @SerializedName("id")
    public String roomID;
    @SerializedName("type")
    public int type;
    @SerializedName("keep_room")
    public boolean keepRoom;
}
