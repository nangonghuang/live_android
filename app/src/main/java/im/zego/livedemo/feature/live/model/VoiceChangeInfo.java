package im.zego.livedemo.feature.live.model;


import im.zego.livedemo.R;

public class VoiceChangeInfo {

    public static final int[] voiceChangeUnselect = new int[]{
            R.drawable.liveshow_voice_none_unselect,
            R.drawable.liveshow_loli_unselect,
            R.drawable.liveshow_uncle_unselect,
            R.drawable.liveshow_robot_unselect,
            R.drawable.liveshow_music_ethereal_unselect
    };

    public static final int[] voiceChangeSelect = new int[]{
            R.drawable.liveshow_voice_none_select,
            R.drawable.liveshow_loli_select,
            R.drawable.liveshow_uncle_select,
            R.drawable.liveshow_robot_select,
            R.drawable.liveshow_music_ethereal_select
    };

    private String name;
    private int iconSelect;
    private int iconUnSelect;

    public String getName() {
        return name;
    }

    public VoiceChangeInfo setName(String name) {
        this.name = name;
        return this;
    }

    public int getIconSelect() {
        return iconSelect;
    }

    public VoiceChangeInfo setIconSelect(int iconSelect) {
        this.iconSelect = iconSelect;
        return this;
    }

    public int getIconUnSelect() {
        return iconUnSelect;
    }

    public VoiceChangeInfo setIconUnSelect(int iconUnSelect) {
        this.iconUnSelect = iconUnSelect;
        return this;
    }
}
