package im.zego.livedemo.feature.live.view;


import im.zego.livedemo.R;

public class BeautyInfo {

    public static final int[] beautyUnselect = new int[]{R.drawable.liveshow_white_unselect, R.drawable.liveshow_smooth_unselect, R.drawable.liveshow_sharpen_unselect,
            R.drawable.liveshow_rosy_unselect};

    public static final int[] beautySelect = new int[]{R.drawable.liveshow_white_select, R.drawable.liveshow_smooth_select, R.drawable.liveshow_sharpen_selec,
            R.drawable.liveshow_rosy_select};

    public static final int[] beautyTypeUnselect = new int[]{R.drawable.liveshow_big_eyes_unselect, R.drawable.liveshow_face_lifting_unselect, R.drawable.liveshow_small_mouth_unselect,
            R.drawable.liveshow_eyes_brightening_unselect,R.drawable.liveshow_nose_narrowing_unselect,R.drawable.liveshow_teeth_whitening_unselect,R.drawable.liveshow_long_chin_unselect};

    public static final int[] beautyTypeSelect = new int[]{R.drawable.liveshow_big_eyes_select, R.drawable.liveshow_face_lifting_select, R.drawable.liveshow_small_mouth_select,
            R.drawable.liveshow_eyes_brightening_select,R.drawable.liveshow_nose_narrowing_select,R.drawable.liveshow_teeth_whitening_select,R.drawable.liveshow_long_chin_select};

    private String beautyName;
    private int beautyIconSelect;
    private int beautyIconUnSelect;

    public String getBeautyName() {
        return beautyName;
    }

    public BeautyInfo setBeautyName(String beautyName) {
        this.beautyName = beautyName;
        return this;
    }

    public int getBeautyIconSelect() {
        return beautyIconSelect;
    }

    public BeautyInfo setBeautyIconSelect(int beautyIconSelect) {
        this.beautyIconSelect = beautyIconSelect;
        return this;
    }

    public int getBeautyIconUnSelect() {
        return beautyIconUnSelect;
    }

    public BeautyInfo setBeautyIconUnSelect(int beautyIconUnSelect) {
        this.beautyIconUnSelect = beautyIconUnSelect;
        return this;
    }
}
