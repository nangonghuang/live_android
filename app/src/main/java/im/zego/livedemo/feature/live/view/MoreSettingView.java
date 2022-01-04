package im.zego.livedemo.feature.live.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import im.zego.livedemo.R;


public class MoreSettingView extends ConstraintLayout {

    private final ImageView ivSettingBg;
    private final TextView tvSettingName;


    public MoreSettingView(@NonNull Context context) {
        this(context, null);
    }

    public MoreSettingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoreSettingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MoreSettingView);
        View view = inflate(context, R.layout.layout_live_more_view, this);
        String name = typedArray.getString(R.styleable.MoreSettingView_moreSettingName);
        Drawable bg = typedArray.getDrawable(R.styleable.MoreSettingView_moreSettingSrc);
        typedArray.recycle();

        ivSettingBg = view.findViewById(R.id.iv_setting_bg);
        tvSettingName = view.findViewById(R.id.tv_setting_name);

        ivSettingBg.setImageDrawable(bg);
        tvSettingName.setText(name);

    }

    public void enableCamaraView(boolean enable) {
        ivSettingBg.setImageResource(enable ? R.drawable.setting_cam_on : R.drawable.setting_cam_off);
    }

    public void enableMicView(boolean enable) {
        ivSettingBg.setImageResource(enable ? R.drawable.setting_mic_on : R.drawable.setting_mic_off);
    }
}
