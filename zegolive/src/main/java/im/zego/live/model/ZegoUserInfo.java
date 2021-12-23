package im.zego.live.model;

import androidx.annotation.DrawableRes;

import java.util.Objects;

/**
 * Created by rocket_wang on 2021/12/14.
 */
public class ZegoUserInfo {
    // user ID
    private String userID;
    // user name
    private String userName;
    // user role
    private ZegoRoomUserRole role;

    // local property
    // user avatar
    @DrawableRes
    private int avatar;
    // user blurred avatar
    @DrawableRes
    private int blurredAvatar;
    // has request to co-host
    private boolean hasRequestedCoHost;
    // has Add co-hosts
    private boolean hasInvited;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ZegoRoomUserRole getRole() {
        return role;
    }

    public void setRole(ZegoRoomUserRole role) {
        this.role = role;
    }

    @DrawableRes
    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(@DrawableRes int avatar) {
        this.avatar = avatar;
    }

    @DrawableRes
    public int getBlurredAvatar() {
        return blurredAvatar;
    }

    public void setBlurredAvatar(@DrawableRes int blurredAvatar) {
        this.blurredAvatar = blurredAvatar;
    }

    public boolean isHasRequestedCoHost() {
        return hasRequestedCoHost;
    }

    public void setHasRequestedCoHost(boolean hasRequestedCoHost) {
        this.hasRequestedCoHost = hasRequestedCoHost;
    }

    public boolean isHasInvited() {
        return hasInvited;
    }

    public void setHasInvited(boolean hasInvited) {
        this.hasInvited = hasInvited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ZegoUserInfo that = (ZegoUserInfo) o;

        if (!Objects.equals(userID, that.userID)) return false;
        return Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        int result = userID != null ? userID.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ZegoUserInfo{" +
                "userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", role=" + role +
                ", avatar=" + avatar +
                ", blurredAvatar=" + blurredAvatar +
                ", hasRequestedCoHost=" + hasRequestedCoHost +
                ", hasInvited=" + hasInvited +
                '}';
    }
}