package im.zego.livedemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.StringUtils;

import im.zego.livedemo.R;
import im.zego.livedemo.databinding.CommonTitleLayoutBinding;

/**
 * Created by rocket_wang on 2021/12/22.
 */
public class CommonTitleView extends ConstraintLayout {

    private final CommonTitleLayoutBinding binding;
    private String title;
    private boolean backBtnVisible;
    private boolean settingsBtnVisible;

    public CommonTitleView(@NonNull Context context) {
        this(context, null);
    }

    public CommonTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonTitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = CommonTitleLayoutBinding.inflate(LayoutInflater.from(context), this, true);
        init(context, attrs);
        updateUI();
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CommonTitleView, 0, 0);
        try {
            title = typedArray.getString(R.styleable.CommonTitleView_common_title);
            if (StringUtils.isTrimEmpty(title)) {
                title = StringUtils.getString(R.string.app_name);
            }
            backBtnVisible = typedArray.getBoolean(R.styleable.CommonTitleView_common_back_btn_visible, false);
            settingsBtnVisible = typedArray.getBoolean(R.styleable.CommonTitleView_common_settings_btn_visible, false);
        } finally {
            typedArray.recycle();
        }
    }

    private void updateUI() {
        binding.tvTitle.setText(title);
        binding.ivBack.setVisibility(backBtnVisible ? VISIBLE : GONE);
        binding.ivSettings.setVisibility(settingsBtnVisible ? VISIBLE : GONE);
    }

    public void setTitle(String title) {
        this.title = title;
        updateUI();
    }

    public void setBackBtnVisible(boolean visible) {
        this.backBtnVisible = visible;
        updateUI();
    }

    public void setSettingsBtnVisible(boolean visible) {
        this.settingsBtnVisible = visible;
        updateUI();
    }

    public void setBackBtnClickListener(OnClickListener listener) {
        binding.ivBack.setOnClickListener(listener);
    }

    public void setSettingsBtnClickListener(OnClickListener listener) {
        binding.ivSettings.setOnClickListener(listener);
    }
}