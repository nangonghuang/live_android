package im.zego.live.service;

import im.zego.zegoexpress.ZegoAudioEffectPlayer;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoReverbPreset;
import im.zego.zegoexpress.constants.ZegoVoiceChangerPreset;
import im.zego.zegoexpress.entity.ZegoAudioEffectPlayConfig;

/**
 * Created by rocket_wang on 2022/1/6.
 */
public class ZegoSoundEffectService {
    public static final int DEFAULT_MUSIC_VOLUME = 50;
    public static final int DEFAULT_VOICE_VOLUME = 50;

    private final ZegoAudioEffectPlayer effectPlayer;

    public ZegoSoundEffectService(ZegoExpressEngine engine) {
        effectPlayer = engine.createAudioEffectPlayer();
    }

    public void reset() {
        effectPlayer.stopAll();
        setBGMVolume(DEFAULT_MUSIC_VOLUME);
        setVoiceVolume(DEFAULT_VOICE_VOLUME);
        ZegoExpressEngine.getEngine().setReverbPreset(ZegoReverbPreset.NONE);
        ZegoExpressEngine.getEngine().setVoiceChangerPreset(ZegoVoiceChangerPreset.NONE);
    }

    public void setBGM(int audioEffectID, String path, boolean stop) {
        if (stop) {
            effectPlayer.stop(audioEffectID);
        } else {
            ZegoAudioEffectPlayConfig config = new ZegoAudioEffectPlayConfig();
            config.playCount = 0;
            config.isPublishOut = true;
            effectPlayer.start(audioEffectID, path, config);
        }
    }

    public void setBGMVolume(int volume) {
        effectPlayer.setVolumeAll(volume * 2);
    }

    public void setVoiceVolume(int volume) {
        ZegoExpressEngine.getEngine().setCaptureVolume(volume * 2);
    }

    public void setVoiceChangerPreset(String voicePreset) {
        switch (voicePreset) {
            case "NONE":
                ZegoExpressEngine.getEngine().setVoiceChangerPreset(ZegoVoiceChangerPreset.NONE);
                break;
            case "WOMEN_TO_CHILD":
                ZegoExpressEngine.getEngine().setVoiceChangerPreset(ZegoVoiceChangerPreset.WOMEN_TO_CHILD);
                break;
            case "ANDROID":
                ZegoExpressEngine.getEngine().setVoiceChangerPreset(ZegoVoiceChangerPreset.ANDROID);
                break;
            case "ETHEREAL":
                ZegoExpressEngine.getEngine().setVoiceChangerPreset(ZegoVoiceChangerPreset.ETHEREAL);
                break;
        }
    }

    public void setReverbPreset(String reverbPreset) {
        switch (reverbPreset) {
            case "NONE":
                ZegoExpressEngine.getEngine().setReverbPreset(ZegoReverbPreset.NONE);
                break;
            case "KTV":
                ZegoExpressEngine.getEngine().setReverbPreset(ZegoReverbPreset.KTV);
                break;
            case "CONCERT_HALL":
                ZegoExpressEngine.getEngine().setReverbPreset(ZegoReverbPreset.CONCERT_HALL);
                break;
            case "VOCAL_CONCERT":
                ZegoExpressEngine.getEngine().setReverbPreset(ZegoReverbPreset.VOCAL_CONCERT);
                break;
            case "ROCK":
                ZegoExpressEngine.getEngine().setReverbPreset(ZegoReverbPreset.ROCK);
                break;
        }
    }

    public void destroyPlayer() {
        ZegoExpressEngine.getEngine().destroyAudioEffectPlayer(effectPlayer);
    }
}