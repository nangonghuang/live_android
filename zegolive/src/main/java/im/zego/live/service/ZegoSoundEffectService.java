package im.zego.live.service;

import java.util.Objects;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.ZegoMediaPlayer;
import im.zego.zegoexpress.constants.ZegoMediaPlayerState;
import im.zego.zegoexpress.constants.ZegoReverbPreset;
import im.zego.zegoexpress.constants.ZegoVoiceChangerPreset;

public class ZegoSoundEffectService {
    public static final int DEFAULT_MUSIC_VOLUME = 50;
    public static final int DEFAULT_VOICE_VOLUME = 50;

    private final ZegoMediaPlayer mediaPlayer;
    private String currentBgmPath;

    public ZegoSoundEffectService(ZegoExpressEngine engine) {
        mediaPlayer = engine.createMediaPlayer();
        mediaPlayer.enableAux(true);
        mediaPlayer.enableRepeat(true);
    }

    public void reset() {
        currentBgmPath = "";
        setBGMVolume(DEFAULT_MUSIC_VOLUME);
        setVoiceVolume(DEFAULT_VOICE_VOLUME);
        ZegoExpressEngine.getEngine().setReverbPreset(ZegoReverbPreset.NONE);
        ZegoExpressEngine.getEngine().setVoiceChangerPreset(ZegoVoiceChangerPreset.NONE);
        stopBGM();
    }

    public void loadBGM(String path) {
        if (mediaPlayer.getCurrentState() == ZegoMediaPlayerState.PLAYING) {
            if (!Objects.equals(currentBgmPath, path)) {
                stopBGM();
                loadResource(path);
            }
        } else {
            loadResource(path);
        }
    }

    private void loadResource(String path) {
        mediaPlayer.loadResource(path, errorCode -> {
            if (errorCode == 0) {
                currentBgmPath = path;
                startBGM();
            }
        });
    }

    public void startBGM() {
        if (mediaPlayer.getCurrentState() != ZegoMediaPlayerState.PLAYING) {
            mediaPlayer.start();
        }
    }

    public void stopBGM() {
        if (mediaPlayer.getCurrentState() == ZegoMediaPlayerState.PLAYING) {
            mediaPlayer.stop();
        }
    }

    public void setBGMVolume(int volume) {
        mediaPlayer.setVolume(volume);
    }

    public void setVoiceVolume(int volume) {
        ZegoExpressEngine.getEngine().setCaptureVolume(volume * 2);
    }

    public void setVoiceChangeType(String voicePreset) {
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
}