package im.zego.livedemo.feature.room.model;

import com.google.gson.annotations.SerializedName;

public class GetRoomListParam {

    @SerializedName("page_num")
    public int pageNumber;

    @SerializedName("from")
    public String fromIndex;

    @SerializedName("type")
    public int type;
}
