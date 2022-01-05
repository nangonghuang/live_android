package im.zego.livedemo.feature.live.dialog;

import android.content.Context;

import com.blankj.utilcode.util.StringUtils;

import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.dialog.base.BaseBottomDialog;
import im.zego.livedemo.feature.live.view.VideoSettingCellView;
import im.zego.livedemo.feature.live.viewmodel.VideoConfigViewModel;


public class MoreVideoSettingsDialog extends BaseBottomDialog {

    private VideoSettingCellView backgroundNoiseReductionCellView;
    private VideoSettingCellView echoCancellationCellView;
    private VideoSettingCellView micVolumeAutoAdjustmentCellView;
    private VideoSettingCellView videoResolutionCellView;
    private VideoSettingCellView audioBitrateCellView;

    private String[] videoResolutionStringArray = StringUtils.getStringArray(R.array.video_resolution);
    private String[] audioBitrateStringArray = StringUtils.getStringArray(R.array.audio_bitrate);

    private final VideoConfigViewModel viewModel;

    public MoreVideoSettingsDialog(Context context, VideoConfigViewModel viewModel) {
        super(context);
        this.viewModel = viewModel;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_more_video_settings;
    }

    @Override
    protected void initView() {
        super.initView();
        backgroundNoiseReductionCellView = findViewById(R.id.more_settings_background_noise_reduction);
        echoCancellationCellView = findViewById(R.id.more_settings_echo_cancellation);
        micVolumeAutoAdjustmentCellView = findViewById(R.id.more_settings_mic_volume_auto_adjustment);
        videoResolutionCellView = findViewById(R.id.more_settings_resolution_settings);
        audioBitrateCellView = findViewById(R.id.more_settings_audio_bitrate);
    }

    @Override
    protected void initData() {
        super.initData();
        videoResolutionCellView.setContent(viewModel.getSettingConfig().getVideoResolution());
        audioBitrateCellView.setContent(viewModel.getSettingConfig().getAudioBitrate());
    }

    @Override
    protected void initListener() {
        super.initListener();

        backgroundNoiseReductionCellView.setListener(isChecked -> viewModel.getSettingConfig().setBackgroundNoiseReduction(isChecked));

        echoCancellationCellView.setListener(isChecked -> viewModel.getSettingConfig().setEchoCancellation(isChecked));

        micVolumeAutoAdjustmentCellView.setListener(isChecked -> viewModel.getSettingConfig().setMicVolumeAutoAdjustment(isChecked));

        videoResolutionCellView.setListener(isChecked -> {
            CommonStringArrayDialog dialog = new CommonStringArrayDialog(
                    getContext(),
                    StringUtils.getString(R.string.room_page_settings_video_resolution),
                    viewModel.getSettingConfig().getVideoResolution(),
                    videoResolutionStringArray,
                    checkedString -> {
                        videoResolutionCellView.setContent(checkedString);
                        viewModel.getSettingConfig().setVideoResolution(checkedString);
                    }
            );
            dialog.show();
        });

        audioBitrateCellView.setListener(isChecked -> {
            CommonStringArrayDialog dialog = new CommonStringArrayDialog(
                    getContext(),
                    StringUtils.getString(R.string.room_page_settings_audio_bitrate),
                    viewModel.getSettingConfig().getAudioBitrate(),
                    audioBitrateStringArray,
                    checkedString -> {
                        audioBitrateCellView.setContent(checkedString);
                        viewModel.getSettingConfig().setAudioBitrate(checkedString);
                    }
            );
            dialog.show();
        });
    }

    @Override
    public void dismiss() {
        viewModel.updateVideoConfig();
        super.dismiss();
    }
}
