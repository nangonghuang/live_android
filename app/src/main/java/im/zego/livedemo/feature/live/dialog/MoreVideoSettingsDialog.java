package im.zego.livedemo.feature.live.dialog;

import android.content.Context;

import com.blankj.utilcode.util.StringUtils;

import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.dialog.base.BaseBottomDialog;
import im.zego.livedemo.feature.live.view.VideoSettingCellView;
import im.zego.livedemo.feature.live.viewmodel.VideoConfigViewModel;

public class MoreVideoSettingsDialog extends BaseBottomDialog {

    private VideoSettingCellView videoResolutionCellView;
    private VideoSettingCellView audioBitrateCellView;

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

        videoResolutionCellView.setListener(isChecked -> {
            CommonStringArrayDialog dialog = new CommonStringArrayDialog(
                    getContext(),
                    StringUtils.getString(R.string.room_settings_page_video_resolution),
                    viewModel.getSettingConfig().getVideoResolution(),
                    viewModel.videoResolutionStringArray,
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
                    StringUtils.getString(R.string.room_settings_page_audio_bitrate),
                    viewModel.getSettingConfig().getAudioBitrate(),
                    viewModel.audioBitrateStringArray,
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
