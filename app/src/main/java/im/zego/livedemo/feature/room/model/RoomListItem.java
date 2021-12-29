package im.zego.livedemo.feature.room.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rocket_wang on 2021/12/22.
 */
public class RoomListItem {
    @SerializedName("subject")
    private String title;
    @SerializedName("online")
    private int num;
    @SerializedName("cover_img")
    private String coverImg;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }
}