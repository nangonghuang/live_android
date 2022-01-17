package im.zego.livedemo.feature.live.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ClickUtils;

import im.zego.livedemo.R;
import im.zego.livedemo.constants.Constants;
import im.zego.livedemo.feature.live.dialog.base.BaseBottomDialog;
import im.zego.livedemo.feature.live.view.MoreSettingView;

public class MoreSettingDialog extends BaseBottomDialog {

    private MoreSettingView moreViewFlip;
    private MoreSettingView moreViewCamera;
    private MoreSettingView moreViewMic;
    //    private MoreSettingView moreViewData;
    private MoreSettingView moreViewSettings;

    private ISettingMoreListener listener;
    private boolean isMicEnable = true;
    private boolean isCameraEnable = true;
    private boolean isCameraFront = true;

    public MoreSettingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_more_setting;
    }

    @Override
    public void initView() {
        moreViewFlip = findViewById(R.id.setting_flip);
        moreViewCamera = findViewById(R.id.setting_camera);
        moreViewMic = findViewById(R.id.setting_mic);
//        moreViewData = findViewById(R.id.setting_data);
        moreViewSettings = findViewById(R.id.settings);

        ClickUtils.applySingleDebouncing(moreViewFlip, Constants.DEBOUNCING_DEFAULT_VALUE, v -> {
            isCameraFront = !isCameraFront;
            listener.onCameraFlip(isCameraFront);
        });
        ClickUtils.applySingleDebouncing(moreViewCamera, Constants.DEBOUNCING_DEFAULT_VALUE, v -> {
            boolean enable = !isCameraEnable;
            enableCamaraView(enable);
            listener.onCameraEnable(enable);
        });
        ClickUtils.applySingleDebouncing(moreViewMic, Constants.DEBOUNCING_DEFAULT_VALUE, v -> {
            boolean enable = !isMicEnable;
            enableMicView(enable);
            listener.onMicEnable(enable);
        });
//        ClickUtils.applySingleDebouncing(moreViewData, Constants.DEBOUNCING_DEFAULT_VALUE, v -> {
//            listener.onClickData();
//        });
        ClickUtils.applySingleDebouncing(moreViewSettings, Constants.DEBOUNCING_DEFAULT_VALUE, v -> {
            listener.onClickSettings();
        });
    }

    public void enableMicView(boolean enable) {
        isMicEnable = enable;
        if (moreViewMic != null) {
            moreViewMic.enableMicView(enable);
        }
    }

    public void enableCamaraView(boolean enable) {
        isCameraEnable = enable;
        if (moreViewCamera != null) {
            moreViewCamera.enableCamaraView(enable);
        }
    }

    public void setListener(ISettingMoreListener listener) {
        this.listener = listener;
    }

    public interface ISettingMoreListener {

        void onCameraFlip(boolean isCameraFront);

        void onCameraEnable(boolean isCameraEnable);

        void onMicEnable(boolean isMicEnable);

//        void onClickData();

        void onClickSettings();
    }
}
