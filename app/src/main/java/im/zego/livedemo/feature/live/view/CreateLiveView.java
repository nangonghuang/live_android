package im.zego.livedemo.feature.live.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.StringUtils;

import im.zego.livedemo.R;
import im.zego.livedemo.constants.Constants;
import im.zego.livedemo.databinding.LayoutCreateLiveBinding;
import im.zego.livedemo.helper.ToastHelper;

public class CreateLiveView extends ConstraintLayout {

    private final LayoutCreateLiveBinding binding;
    private CreateViewListener listener;
    private boolean isCameraFront = true;

    public CreateLiveView(@NonNull Context context) {
        this(context, null);
    }

    public CreateLiveView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CreateLiveView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = LayoutCreateLiveBinding.inflate(LayoutInflater.from(context), this, true);
        initView();
    }

    private void initView() {
        binding.etRoomName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableStartLiveBtn(editable.length() > 0);
            }
        });
        ClickUtils.applySingleDebouncing(binding.ivBack, Constants.DEBOUNCING_DEFAULT_VALUE, v -> listener.onBackClick());
        ClickUtils.applySingleDebouncing(binding.ivFlipCamera, Constants.DEBOUNCING_DEFAULT_VALUE, v -> {
            isCameraFront = !isCameraFront;
            listener.onCameraFlip(isCameraFront);
        });
        ClickUtils.applySingleDebouncing(binding.ivBeauty, Constants.DEBOUNCING_DEFAULT_VALUE, v -> listener.onBeautyClick());
        ClickUtils.applySingleDebouncing(binding.ivSettings, Constants.DEBOUNCING_DEFAULT_VALUE, v -> listener.onSettingsClick());
        ClickUtils.applySingleDebouncing(binding.tvStartLive, Constants.DEBOUNCING_DEFAULT_VALUE, v -> {
            String roomName = binding.etRoomName.getText().toString();
            if (TextUtils.isEmpty(roomName)) {
                ToastHelper.showWarnToast(StringUtils.getString(R.string.create_page_room_name));
                return;
            }
            listener.onStartLiveClick(roomName);
        });
    }

    private void enableStartLiveBtn(boolean enable) {
        binding.tvStartLive.setAlpha(enable ? 1 : 0.5f);
        binding.tvStartLive.setEnabled(enable);
    }

    public void setListener(CreateViewListener listener) {
        this.listener = listener;
    }

    public interface CreateViewListener {
        void onBackClick();

        void onCameraFlip(boolean isCameraFront);

        void onBeautyClick();

        void onSettingsClick();

        void onStartLiveClick(String roomName);
    }
}
