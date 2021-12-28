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
    private boolean isMicOpen = true;
    private boolean isCamOpen = true;

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

        setMic(isMicOpen);
        setCamara(isCamOpen);
    }

    public void setMic(boolean isMicOpen) {
        mSettingMic.setMic(isMicOpen);
    }

    public void setCamara(boolean isCamOpen) {
        mSettingCamera.setCamara(isCamOpen);
    }

    public void setMicAndCamara(boolean isMicOpen, boolean isCamOpen) {
        this.isCamOpen = isCamOpen;
        this.isMicOpen = isMicOpen;
    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        int id = v.getId();
        if (id == R.id.setting_flip) {
            listener.onClickFlip();
        } else if (id == R.id.setting_camera) {
            listener.onClickCamera();
        } else if (id == R.id.setting_mic) {
            listener.onClickMic();
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
        void onClickFlip();

        void onClickCamera();

        void onClickMic();

        void onClickData();

        void onClickSettings();
    }
}
