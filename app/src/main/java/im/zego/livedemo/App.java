package im.zego.livedemo;

import android.app.Application;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import im.zego.live.ZegoRoomManager;
import im.zego.livedemo.helper.AuthInfoManager;
import im.zego.livedemo.helper.CrashHandler;
import im.zego.livedemo.helper.SoundEffectsHelper;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
        Utils.init(this);
        LogUtils.getConfig().setLogHeadSwitch(false);
        LogUtils.getConfig().setBorderSwitch(false);

        AuthInfoManager.getInstance().init(this);
        // init LiveAudioRoom SDK
        long appID = AuthInfoManager.getInstance().getAppID();
        ZegoRoomManager.getInstance().init(appID, this);

        SoundEffectsHelper.initLocalAudioEffectList(this);
    }
}