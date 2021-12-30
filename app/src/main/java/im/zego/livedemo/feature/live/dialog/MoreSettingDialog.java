package im.zego.livedemo.feature.live.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.dialog.base.BaseBottomDialog;
import im.zego.livedemo.feature.live.view.MoreSettingView;

public class MoreSettingDialog extends BaseBottomDialog implements View.OnClickListener {

    private MoreSettingView moreViewFlip;
    private MoreSettingView moreViewCamera;
    private MoreSettingView moreViewMic;
    private MoreSettingView moreViewData;
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
        moreViewData = findViewById(R.id.setting_data);
        moreViewSettings = findViewById(R.id.settings);

        moreViewFlip.setOnClickListener(this);
        moreViewCamera.setOnClickListener(this);
        moreViewMic.setOnClickListener(this);
        moreViewData.setOnClickListener(this);
        moreViewSettings.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        int id = v.getId();
        if (id == R.id.setting_flip) {
            isCameraFront = !isCameraFront;
            listener.onCameraFlip(isCameraFront);
        } else if (id == R.id.setting_camera) {
            boolean enable = !isCameraEnable;
            enableCamaraView(enable);
            listener.onCameraEnable(enable);
        } else if (id == R.id.setting_mic) {
            boolean enable = !isMicEnable;
            enableMicView(enable);
            listener.onMicEnable(enable);
        } else if (id == R.id.setting_data) {
            listener.onClickData();
        } else if (id == R.id.settings) {
            listener.onClickSettings();
        }
    }

    public void setListener(ISettingMoreListener listener) {
        this.listener = listener;
    }

    public interface ISettingMoreListener {

        void onCameraFlip(boolean isCameraFront);

        void onCameraEnable(boolean isCameraEnable);

        void onMicEnable(boolean isMicEnable);

        void onClickData();

        void onClickSettings();
    }
}
