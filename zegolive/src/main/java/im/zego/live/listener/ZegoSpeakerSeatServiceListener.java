package im.zego.live.listener;

import im.zego.live.model.ZegoSpeakerSeatModel;

/**
 * notify speaker seat update.
 */
public interface ZegoSpeakerSeatServiceListener {

    void onSpeakerSeatUpdate(ZegoSpeakerSeatModel speakerSeatModel);
}
