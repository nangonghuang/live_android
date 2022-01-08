package im.zego.livedemo.feature.live.dialog;

import android.content.Context;

import com.blankj.utilcode.util.StringUtils;

import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.dialog.base.BaseBottomDialog;
import im.zego.livedemo.feature.live.model.VideoSettingConfig;
import im.zego.livedemo.feature.live.view.VideoSettingCellView;
import im.zego.livedemo.feature.live.viewmodel.VideoConfigViewModel;
import im.zego.livedemo.helper.ToastHelper;


public class VideoSettingsDialog extends BaseBottomDialog {

    private VideoSettingCellView settingsEncodeType;
    private VideoSettingCellView settingsLayeredCoding;
    private VideoSettingCellView settingsHardwareCoding;
    private VideoSettingCellView settingsHardwareDecoding;
    private VideoSettingCellView backgroundNoiseReduction;
    private VideoSettingCellView echoCancellation;
    private VideoSettingCellView micVolumeAutoAdjustment;
    private VideoSettingCellView settingsVideoResolution;
    private VideoSettingCellView settingsAudioBitrate;

    private final VideoConfigViewModel viewModel;

    public VideoSettingsDialog(Context context, VideoConfigViewModel viewModel) {
        super(context);
        this.viewModel = viewModel;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_video_settings;
    }

    @Override
    protected void initView() {
        super.initView();
        settingsEncodeType = findViewById(R.id.settings_encode_type);
        settingsLayeredCoding = findViewById(R.id.settings_layered_coding);
        settingsHardwareCoding = findViewById(R.id.settings_hardware_coding);
        settingsHardwareDecoding = findViewById(R.id.settings_hardware_decoding);
        backgroundNoiseReduction = findViewById(R.id.settings_background_noise_reduction);
        echoCancellation = findViewById(R.id.settings_echo_cancellation);
        micVolumeAutoAdjustment = findViewById(R.id.settings_mic_volume_auto_adjustment);
        settingsVideoResolution = findViewById(R.id.settings_resolution_settings);
        settingsAudioBitrate = findViewById(R.id.settings_audio_bitrate);
    }

    @Override
    protected void initData() {
        super.initData();
        settingsEncodeType.setContent(viewModel.getSettingConfig().getEncodeType());
        settingsVideoResolution.setContent(viewModel.getSettingConfig().getVideoResolution());
        settingsAudioBitrate.setContent(viewModel.getSettingConfig().getAudioBitrate());
    }

    @Override
    protected void initListener() {
        super.initListener();

        settingsEncodeType.setListener(isChecked -> {
            CommonStringArrayDialog dialog = new CommonStringArrayDialog(
                    getContext(),
                    StringUtils.getString(R.string.room_page_settings_encode_type),
                    viewModel.getSettingConfig().getEncodeType(),
                    viewModel.encodingTypeStringArray,
                    encodeType -> {
                        settingsEncodeType.setContent(encodeType);
                        viewModel.getSettingConfig().setEncodeType(encodeType);

                        if (VideoSettingConfig.isH265(viewModel.getSettingConfig().getEncodeType())) {
                            settingsLayeredCoding.setEnabled(false);
                        } else {
                            settingsLayeredCoding.setEnabled(true);
                        }
                        settingsHardwareCoding.setChecked(true);
                    }
            );
            dialog.setOnDismissListener(d -> this.show());
            this.hide();
            dialog.show();
        });

        settingsLayeredCoding.setListener(isChecked -> viewModel.getSettingConfig().setLayeredCoding(isChecked));

        settingsHardwareCoding.setListener(isChecked -> {
            viewModel.getSettingConfig().setHardwareEncode(isChecked);

            if (VideoSettingConfig.isH265(viewModel.getSettingConfig().getEncodeType()) && !isChecked) {
                ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_room_page_settings_h265_error));
                settingsHardwareCoding.setChecked(true);
            }
        });

        settingsHardwareDecoding.setListener(isChecked -> viewModel.getSettingConfig().setHardwareDecode(isChecked));

        backgroundNoiseReduction.setListener(isChecked -> viewModel.getSettingConfig().setBackgroundNoiseReduction(isChecked));

        echoCancellation.setListener(isChecked -> viewModel.getSettingConfig().setEchoCancellation(isChecked));

        micVolumeAutoAdjustment.setListener(isChecked -> viewModel.getSettingConfig().setMicVolumeAutoAdjustment(isChecked));

        settingsVideoResolution.setListener(isChecked -> {
            CommonStringArrayDialog dialog = new CommonStringArrayDialog(
                    getContext(),
                    StringUtils.getString(R.string.room_page_settings_video_resolution),
                    viewModel.getSettingConfig().getVideoResolution(),
                    viewModel.videoResolutionStringArray,
                    checkedString -> {
                        settingsVideoResolution.setContent(checkedString);
                        viewModel.getSettingConfig().setVideoResolution(checkedString);
                    }
            );
            dialog.setOnDismissListener(d -> this.show());
            this.hide();
            dialog.show();
        });

        settingsAudioBitrate.setListener(isChecked -> {
            CommonStringArrayDialog dialog = new CommonStringArrayDialog(
                    getContext(),
                    StringUtils.getString(R.string.room_page_settings_audio_bitrate),
                    viewModel.getSettingConfig().getAudioBitrate(),
                    viewModel.audioBitrateStringArray,
                    checkedString -> {
                        settingsAudioBitrate.setContent(checkedString);
                        viewModel.getSettingConfig().setAudioBitrate(checkedString);
                    }
            );
            dialog.setOnDismissListener(d -> this.show());
            this.hide();
            dialog.show();
        });
    }

    @Override
    public void dismiss() {
        viewModel.updateVideoConfig();
        super.dismiss();
    }
}
