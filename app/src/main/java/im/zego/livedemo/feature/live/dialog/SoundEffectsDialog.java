package im.zego.livedemo.feature.live.dialog;

import android.content.Context;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.zego.live.ZegoRoomManager;
import im.zego.live.service.ZegoSoundEffectService;
import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.adapter.BackgroundSoundAdapter;
import im.zego.livedemo.feature.live.adapter.ReverbPresetAdapter;
import im.zego.livedemo.feature.live.adapter.VoiceChangeAdapter;
import im.zego.livedemo.feature.live.dialog.base.BaseBottomDialog;
import im.zego.livedemo.feature.live.model.ReverbPresetInfo;
import im.zego.livedemo.feature.live.model.VoiceChangeInfo;
import im.zego.livedemo.helper.SoundEffectsHelper;
import im.zego.livedemo.view.SoundEffectsSeekBar;

public class SoundEffectsDialog extends BaseBottomDialog {

    private RecyclerView rvBgSound;
    private SoundEffectsSeekBar musicVolumeSeekbar;
    private SoundEffectsSeekBar voiceVolumeSeekbar;
    private RecyclerView rvVoiceChange;
    private RecyclerView rvReverb;

    private final ZegoSoundEffectService soundEffectService = ZegoRoomManager.getInstance().soundEffectService;
    private int lastBGMPosition = -1;

    public SoundEffectsDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_sound_effects;
    }

    @Override
    public void initView() {
        rvBgSound = findViewById(R.id.rv_bg_sound);
        musicVolumeSeekbar = findViewById(R.id.music_volume_seekbar);
        voiceVolumeSeekbar = findViewById(R.id.voice_volume_seekbar);
        rvVoiceChange = findViewById(R.id.rv_voice_change);
        rvReverb = findViewById(R.id.rv_reverb);
    }

    @Override
    protected void initData() {
        soundEffectService.reset();

        initBackgroundSound();
        initVoiceChange();
        initReverb();
        initMusicVolume();
        initVoiceVolume();
    }

    @NonNull
    private LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    }

    private void initBackgroundSound() {
        rvBgSound.setLayoutManager(getLayoutManager());
        BackgroundSoundAdapter adapter = new BackgroundSoundAdapter();
        adapter.setList(createBackgroundSoundList());
        adapter.setListener(position -> {
            String songPath = SoundEffectsHelper.songFileMap.get(position);
            String lastSongPath = SoundEffectsHelper.songFileMap.get(lastBGMPosition);

            soundEffectService.setBGM(lastBGMPosition, lastSongPath, true);
            if (StringUtils.isTrimEmpty(songPath) || position == null) {
                lastBGMPosition = -1;
            } else {
                soundEffectService.setBGM(position, songPath, false);
                lastBGMPosition = position;
            }
        });
        rvBgSound.setAdapter(adapter);
    }

    private void initVoiceChange() {
        rvVoiceChange.setLayoutManager(getLayoutManager());
        VoiceChangeAdapter adapter = new VoiceChangeAdapter();
        adapter.setList(createVoiceChangeList());
        adapter.setListener(position -> {
            String[] options = StringUtils.getStringArray(R.array.voicePreset);
            soundEffectService.setVoiceChangerPreset(options[position]);
        });
        rvVoiceChange.setAdapter(adapter);
    }

    private void initReverb() {
        rvReverb.setLayoutManager(getLayoutManager());
        ReverbPresetAdapter adapter = new ReverbPresetAdapter();
        adapter.setList(createReverbPresetList());
        adapter.setListener(position -> {
            String[] options = StringUtils.getStringArray(R.array.reverbPreset);
            soundEffectService.setReverbPreset(options[position]);
        });
        rvReverb.setAdapter(adapter);
    }

    private void initMusicVolume() {
        musicVolumeSeekbar.setProgress(ZegoSoundEffectService.DEFAULT_MUSIC_VOLUME);
        musicVolumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musicVolumeSeekbar.setProgress(progress);
                soundEffectService.setBGMVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initVoiceVolume() {
        voiceVolumeSeekbar.setProgress(ZegoSoundEffectService.DEFAULT_VOICE_VOLUME);
        voiceVolumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                voiceVolumeSeekbar.setProgress(progress);
                soundEffectService.setVoiceVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private List<String> createBackgroundSoundList() {
        return Arrays.asList(StringUtils.getStringArray(R.array.background_sound_list));
    }

    private List<VoiceChangeInfo> createVoiceChangeList() {
        List<VoiceChangeInfo> list = new ArrayList<>();
        String[] voiceList = StringUtils.getStringArray(R.array.voice_change_list);
        for (int i = 0; i < voiceList.length; ++i) {
            String voiceName = voiceList[i];
            int voiceIconUnselect = VoiceChangeInfo.voiceChangeUnselect[i];
            int voiceIconSelect = VoiceChangeInfo.voiceChangeSelect[i];
            list.add(new VoiceChangeInfo().setName(voiceName).setIconUnSelect(voiceIconUnselect).setIconSelect(voiceIconSelect));
        }
        return list;
    }

    private List<ReverbPresetInfo> createReverbPresetList() {
        List<ReverbPresetInfo> list = new ArrayList<>();
        String[] reverbPresetList = StringUtils.getStringArray(R.array.reverb_preset_list);
        for (int i = 0; i < reverbPresetList.length; ++i) {
            String rpName = reverbPresetList[i];
            int rpIcon = ReverbPresetInfo.reverbPresetIcon[i];
            list.add(new ReverbPresetInfo().setName(rpName).setIcon(rpIcon));
        }
        return list;
    }
}
