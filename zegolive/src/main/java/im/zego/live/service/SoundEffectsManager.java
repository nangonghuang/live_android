package im.zego.live.service;

import im.zego.zegoexpress.ZegoAudioEffectPlayer;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoReverbPreset;
import im.zego.zegoexpress.constants.ZegoVoiceChangerPreset;
import im.zego.zegoexpress.entity.ZegoAudioEffectPlayConfig;

public class SoundEffectsManager {

    private static volatile SoundEffectsManager singleton = null;
    private ZegoExpressEngine engine;
    private ZegoAudioEffectPlayer mAudioEffectPlayer;

    private int backgroundSoundPosition = -1;
    private int voiceChangePosition = 0;
    private int reverbPresetPosition = 0;

    private int musicVolume = 50;
    private int vocalVolume = 50;

    public static SoundEffectsManager getInstance() {
        if (singleton == null) {
            synchronized (SoundEffectsManager.class) {
                if (singleton == null) {
                    singleton = new SoundEffectsManager();
                }
            }
        }
        return singleton;
    }

    public SoundEffectsManager() {
        engine = ZegoExpressEngine.getEngine();
        if (engine != null) {
            mAudioEffectPlayer = engine.createAudioEffectPlayer();
        }
    }

    public void setVoiceChangerPreset(String voicePreset) {
        switch (voicePreset) {
            case "NONE":
                engine.setVoiceChangerPreset(ZegoVoiceChangerPreset.NONE);
                break;
            case "WOMEN_TO_CHILD":
                engine.setVoiceChangerPreset(ZegoVoiceChangerPreset.WOMEN_TO_CHILD);
                break;
            case "ANDROID":
                engine.setVoiceChangerPreset(ZegoVoiceChangerPreset.ANDROID);
                break;
            case "ETHEREAL":
                engine.setVoiceChangerPreset(ZegoVoiceChangerPreset.ETHEREAL);
                break;
        }
    }

    public void setReverbPreset(String reverbPreset) {
        switch (reverbPreset) {
            case "NONE":
                engine.setReverbPreset(ZegoReverbPreset.NONE);
                break;
            case "KTV":
                engine.setReverbPreset(ZegoReverbPreset.KTV);
                break;
            case "CONCERT_HALL":
                engine.setReverbPreset(ZegoReverbPreset.CONCERT_HALL);
                break;
            case "VOCAL_CONCERT":
                engine.setReverbPreset(ZegoReverbPreset.VOCAL_CONCERT);
                break;
            case "ROCK":
                engine.setReverbPreset(ZegoReverbPreset.ROCK);
                break;
        }
    }

    public void setVolume(int volume) {
        mAudioEffectPlayer.setVolumeAll(volume * 2);
    }

    public void setCaptureVolume(int volume) {
        engine.setCaptureVolume(volume * 2);
    }

    public void audioEffectPlayer(int audioEffectID, String path) {
        ZegoAudioEffectPlayConfig config = new ZegoAudioEffectPlayConfig();
        config.playCount = 0;
        config.isPublishOut = true;
        mAudioEffectPlayer.start(audioEffectID, path, config);
    }

    public void audioEffectStop(int audioEffectID) {
        mAudioEffectPlayer.stop(audioEffectID);
    }

    public void clearSoundEffectsData() {

        if (mAudioEffectPlayer != null) {
            setVolume(50);
            mAudioEffectPlayer.stopAll();
        }
        backgroundSoundPosition = -1;
        voiceChangePosition = 0;
        reverbPresetPosition = 0;
        musicVolume = 50;
        vocalVolume = 50;
        if (engine != null) {
            setCaptureVolume(50);
            engine.setReverbPreset(ZegoReverbPreset.NONE);
            engine.setVoiceChangerPreset(ZegoVoiceChangerPreset.NONE);
        }
    }

    public void destroyAudioEffect() {
        if (engine != null) {
            engine.destroyAudioEffectPlayer(mAudioEffectPlayer);
        }
        singleton = null;
    }

    public int getBackgroundSoundPosition() {
        return backgroundSoundPosition;
    }

    public void setBackgroundSoundPosition(int backgroundSoundPosition) {
        this.backgroundSoundPosition = backgroundSoundPosition;
    }

    public int getVoiceChangePosition() {
        return voiceChangePosition;
    }

    public void setVoiceChangePosition(int voiceChangePosition) {
        this.voiceChangePosition = voiceChangePosition;
    }

    public int getReverbPresetPosition() {
        return reverbPresetPosition;
    }

    public void setReverbPresetPosition(int reverbPresetPosition) {
        this.reverbPresetPosition = reverbPresetPosition;
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(int musicVolume) {
        this.musicVolume = musicVolume;
    }

    public int getVocalVolume() {
        return vocalVolume;
    }

    public void setVocalVolume(int vocalVolume) {
        this.vocalVolume = vocalVolume;
    }
}
