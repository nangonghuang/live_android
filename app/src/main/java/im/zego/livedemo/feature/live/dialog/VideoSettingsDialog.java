package im.zego.livedemo.feature.live.dialog;

import android.content.Context;

import com.blankj.utilcode.util.StringUtils;

import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.dialog.base.BaseBottomDialog;
import im.zego.livedemo.feature.live.view.VideoSettingCellView;
import im.zego.livedemo.feature.live.viewmodel.VideoConfigViewModel;


public class VideoSettingsDialog extends BaseBottomDialog {

    private VideoSettingCellView settingsEncodeType;
    private VideoSettingCellView settingsLayeredCoding;
    private VideoSettingCellView settingsHardwareCoding;
    private VideoSettingCellView settingsHardwareDecoding;
    private VideoSettingCellView settingsVideoResolution;
    private VideoSettingCellView settingsAudioBitrate;

    private String[] encodingTypeStringArray = StringUtils.getStringArray(R.array.encoding_type);
    private String[] videoResolutionStringArray = StringUtils.getStringArray(R.array.video_resolution);
    private String[] audioBitrateStringArray = StringUtils.getStringArray(R.array.audio_bitrate);

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
        settingsVideoResolution = findViewById(R.id.settings_resolution_settings);
        settingsAudioBitrate = findViewById(R.id.settings_audio_bitrate);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel.getSettingConfig().setEncodeType(encodingTypeStringArray[0]);
        viewModel.getSettingConfig().setVideoResolution(videoResolutionStringArray[0]);
        viewModel.getSettingConfig().setAudioBitrate(audioBitrateStringArray[0]);

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
                    encodingTypeStringArray,
                    checkedString -> {
                        settingsEncodeType.setContent(checkedString);
                        viewModel.getSettingConfig().setEncodeType(checkedString);
                    }
            );
            dialog.show();
        });

        settingsLayeredCoding.setListener(isChecked -> viewModel.getSettingConfig().setLayeredCoding(isChecked));

        settingsHardwareCoding.setListener(isChecked -> viewModel.getSettingConfig().setHardwareEncode(isChecked));

        settingsHardwareDecoding.setListener(isChecked -> viewModel.getSettingConfig().setHardwareDecode(isChecked));

        settingsVideoResolution.setListener(isChecked -> {
            CommonStringArrayDialog dialog = new CommonStringArrayDialog(
                    getContext(),
                    StringUtils.getString(R.string.room_page_settings_video_resolution),
                    viewModel.getSettingConfig().getVideoResolution(),
                    videoResolutionStringArray,
                    checkedString -> {
                        settingsVideoResolution.setContent(checkedString);
                        viewModel.getSettingConfig().setVideoResolution(checkedString);
                    }
            );
            dialog.show();
        });

        settingsAudioBitrate.setListener(isChecked -> {
            CommonStringArrayDialog dialog = new CommonStringArrayDialog(
                    getContext(),
                    StringUtils.getString(R.string.room_page_settings_audio_bitrate),
                    viewModel.getSettingConfig().getAudioBitrate(),
                    audioBitrateStringArray,
                    checkedString -> {
                        settingsAudioBitrate.setContent(checkedString);
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
