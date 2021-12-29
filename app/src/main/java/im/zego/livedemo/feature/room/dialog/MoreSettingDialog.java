package im.zego.livedemo.feature.room.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import im.zego.livedemo.R;

public class MoreSettingDialog extends BaseBottomDialog implements View.OnClickListener {

    private MoreSettingView mSettingFlip;
    private MoreSettingView mSettingCamera;
    private MoreSettingView mSettingMic;
    private MoreSettingView mSettingData;
    private MoreSettingView mSettings;

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
        mSettingFlip = findViewById(R.id.setting_flip);
        mSettingCamera = findViewById(R.id.setting_camera);
        mSettingMic = findViewById(R.id.setting_mic);
        mSettingData = findViewById(R.id.setting_data);
        mSettings = findViewById(R.id.settings);

        mSettingFlip.setOnClickListener(this);
        mSettingCamera.setOnClickListener(this);
        mSettingMic.setOnClickListener(this);
        mSettingData.setOnClickListener(this);
        mSettings.setOnClickListener(this);

        enableMicView(isMicEnable);
        enableCamaraView(isCameraEnable);
    }

    public void enableMicView(boolean isMicOpen) {
        mSettingMic.setMic(isMicOpen);
    }

    public void enableCamaraView(boolean isCamOpen) {
        mSettingCamera.setCamara(isCamOpen);
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
            enableCamaraView(!isCameraEnable);
            listener.onCameraEnable(!isCameraEnable);
        } else if (id == R.id.setting_mic) {
            enableMicView(!isMicEnable);
            listener.onMicEnable(!isMicEnable);
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
