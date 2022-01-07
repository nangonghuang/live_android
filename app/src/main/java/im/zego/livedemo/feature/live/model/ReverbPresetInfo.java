package im.zego.livedemo.feature.live.model;


import im.zego.livedemo.R;

public class ReverbPresetInfo {

    public static final int[] reverbPresetIcon = new int[]{
            R.drawable.liveshow_rp_none,
            R.drawable.liveshow_rp_ktv,
            R.drawable.liveshow_rp_musichall,
            R.drawable.liveshow_rp_vocal_concert,
            R.drawable.liveshow_rp_rock
    };

    private String name;
    private int icon;

    public String getName() {
        return name;
    }

    public ReverbPresetInfo setName(String name) {
        this.name = name;
        return this;
    }

    public int getIcon() {
        return icon;
    }

    public ReverbPresetInfo setIcon(int icon) {
        this.icon = icon;
        return this;
    }
}
