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

    public void setVoiceChangeType(ZegoVoiceChangerPreset voicePreset) {
        ZegoExpressEngine.getEngine().setVoiceChangerPreset(voicePreset);
    }

    public void setReverbPreset(ZegoReverbPreset reverbPreset) {
        ZegoExpressEngine.getEngine().setReverbPreset(reverbPreset);
    }
}