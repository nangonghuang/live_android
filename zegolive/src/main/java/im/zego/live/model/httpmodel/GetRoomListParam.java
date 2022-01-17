package im.zego.live.model.httpmodel;

import com.google.gson.annotations.SerializedName;

public class GetRoomListParam {

    @SerializedName("page_num")
    public int pageNumber;

    @SerializedName("from")
    public String fromIndex;

    @SerializedName("type")
    public int type;
}
