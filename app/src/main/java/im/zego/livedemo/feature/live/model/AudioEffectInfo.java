package im.zego.livedemo.feature.live.model;

public class AudioEffectInfo {

    private int id;
    private String fileAssetsPath;

    public int getId() {
        return id;
    }

    public AudioEffectInfo setId(int id) {
        this.id = id;
        return this;
    }

    public String getFileAssetsPath() {
        return fileAssetsPath;
    }

    public AudioEffectInfo setFileAssetsPath(String fileAssetsPath) {
        this.fileAssetsPath = fileAssetsPath;
        return this;
    }
}
