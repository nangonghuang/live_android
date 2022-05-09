package im.zego.live.service;

import java.util.Objects;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.ZegoMediaPlayer;
import im.zego.zegoexpress.constants.ZegoMediaPlayerState;
import im.zego.zegoexpress.constants.ZegoReverbPreset;
import im.zego.zegoexpress.constants.ZegoVoiceChangerPreset;

/**
 * Class sound effects
 * <p>
 * Description: This class contains the sound effects logic.
 */
public class ZegoSoundEffectsService {
    public static final int DEFAULT_MUSIC_VOLUME = 50;
    public static final int DEFAULT_VOICE_VOLUME = 50;

    private final ZegoMediaPlayer mediaPlayer;
    private String currentBgmPath;

    public ZegoSoundEffectsService() {
        mediaPlayer = ZegoExpressEngine.getEngine().createMediaPlayer();
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

    /**
     * Load the background music file
     * <p>
     * Description: This method can be used to load the background music by setting the file path and the music will be automatically played.
     * <p>
     * Call this method at: After joining a room
     *
     * @param path indicates the path of the music resource.
     */
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

    /**
     * Start playing the background music
     * <p>
     * Description: This method can be used to restart the music that be stopped by calling the stopBGM method.
     * <p>
     * Call this method at: After joining a room and calling the loadBGM method
     */
    public void startBGM() {
        if (mediaPlayer.getCurrentState() != ZegoMediaPlayerState.PLAYING) {
            mediaPlayer.start();
        }
    }

    /**
     * Stop the background music
     * <p>
     * Description: This method can be used to stop playing the background music. And you can restart playing the music by calling the startBGM method.
     * <p>
     * Call this method at: After joining a room and calling the loadBGM method
     */
    public void stopBGM() {
        if (mediaPlayer.getCurrentState() == ZegoMediaPlayerState.PLAYING) {
            mediaPlayer.stop();
        }
    }

    /**
     * Set the background music volume
     * <p>
     * Description: The music volume range is [0, 100]. The default value is 50.
     * <p>
     * Call this method at: After joining a room
     *
     * @param volume refers to the music volume
     */
    public void setBGMVolume(int volume) {
        mediaPlayer.setVolume(volume);
    }

    /**
     * Set the voice volume
     * <p>
     * Description: The voice volume range is [0, 100]. The default value is 50.
     * <p>
     * Call this method at: After joining a room
     *
     * @param volume refers to the voice volume
     */
    public void setVoiceVolume(int volume) {
        ZegoExpressEngine.getEngine().setCaptureVolume(volume * 2);
    }

    /**
     * Set voice changing
     * <p>
     * Description: This method can be used to change the voice with voice effects.
     * <p>
     * Call this method at: After joining a room
     *
     * @param voicePreset refers to the voice type you want to changed to.
     */
    public void setVoiceChangeType(ZegoVoiceChangerPreset voicePreset) {
        ZegoExpressEngine.getEngine().setVoiceChangerPreset(voicePreset);
    }

    /**
     * Set reverb
     * <p>
     * Description: This method can be used to use the reverb effect in the room.
     * <p>
     * Call this method at: After joining a room
     *
     * @param reverbPreset refers to the reverb type you want to select.
     */
    public void setReverbPreset(ZegoReverbPreset reverbPreset) {
        ZegoExpressEngine.getEngine().setReverbPreset(reverbPreset);
    }
}