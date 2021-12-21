package im.zego.live.model;

/**
 * Created by rocket_wang on 2021/12/21.
 */
public class ZegoCoHostSeatModel {
    private String userID;
    private boolean isMuted;
    private boolean mic;
    private boolean camera;

    // local property
    private float soundLevel;
    private float network;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean isMic() {
        return mic;
    }

    public void setMic(boolean mic) {
        this.mic = mic;
    }

    public boolean isCamera() {
        return camera;
    }

    public void setCamera(boolean camera) {
        this.camera = camera;
    }

    public float getSoundLevel() {
        return soundLevel;
    }

    public void setSoundLevel(float soundLevel) {
        this.soundLevel = soundLevel;
    }

    public float getNetwork() {
        return network;
    }

    public void setNetwork(float network) {
        this.network = network;
    }

    @Override
    public String toString() {
        return "ZegoCoHostSeatModel{" +
                "userID='" + userID + '\'' +
                ", isMuted=" + isMuted +
                ", mic=" + mic +
                ", camera=" + camera +
                ", soundLevel=" + soundLevel +
                ", network=" + network +
                '}';
    }
}