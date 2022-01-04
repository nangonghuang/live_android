package im.zego.livedemo.feature.live.dialog;

import android.content.Context;

import com.blankj.utilcode.util.StringUtils;

import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.dialog.base.BaseBottomDialog;
import im.zego.livedemo.feature.live.model.VideoSettingConfig;
import im.zego.livedemo.feature.live.view.VideoSettingCellView;


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

    private VideoSettingConfig config = new VideoSettingConfig();

    public VideoSettingsDialog(Context context) {
        super(context);
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
        config.setEncodeType(encodingTypeStringArray[0]);
        config.setVideoResolution(videoResolutionStringArray[0]);
        config.setAudioBitrate(audioBitrateStringArray[0]);

        settingsEncodeType.setContent(config.getEncodeType());
        settingsVideoResolution.setContent(config.getVideoResolution());
        settingsAudioBitrate.setContent(config.getAudioBitrate());
    }

    @Override
    protected void initListener() {
        super.initListener();

        settingsEncodeType.setListener(isChecked -> {
            CommonStringArrayDialog dialog = new CommonStringArrayDialog(
                    getContext(),
                    StringUtils.getString(R.string.room_page_settings_encode_type),
                    config.getEncodeType(),
                    encodingTypeStringArray,
                    checkedString -> {
                        settingsEncodeType.setContent(checkedString);
                        config.setEncodeType(checkedString);
                    }
            );
            dialog.show();
        });

        settingsLayeredCoding.setListener(isChecked -> config.setLayeredCoding(isChecked));

        settingsHardwareCoding.setListener(isChecked -> config.setHardwareCoding(isChecked));

        settingsHardwareDecoding.setListener(isChecked -> config.setHardwareDecoding(isChecked));

        settingsVideoResolution.setListener(isChecked -> {
            CommonStringArrayDialog dialog = new CommonStringArrayDialog(
                    getContext(),
                    StringUtils.getString(R.string.room_page_settings_video_resolution),
                    config.getVideoResolution(),
                    videoResolutionStringArray,
                    checkedString -> {
                        settingsVideoResolution.setContent(checkedString);
                        config.setVideoResolution(checkedString);
                    }
            );
            dialog.show();
        });

        settingsAudioBitrate.setListener(isChecked -> {
            CommonStringArrayDialog dialog = new CommonStringArrayDialog(
                    getContext(),
                    StringUtils.getString(R.string.room_page_settings_audio_bitrate),
                    config.getAudioBitrate(),
                    audioBitrateStringArray,
                    checkedString -> {
                        settingsAudioBitrate.setContent(checkedString);
                        config.setAudioBitrate(checkedString);
                    }
            );
            dialog.show();
        });
    }
}
