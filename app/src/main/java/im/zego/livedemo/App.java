package im.zego.livedemo;

import android.app.Application;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import im.zego.live.ZegoRoomManager;
import im.zego.livedemo.helper.AuthInfoManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        LogUtils.getConfig().setLogHeadSwitch(false);
        LogUtils.getConfig().setBorderSwitch(false);

        AuthInfoManager.getInstance().init(this);
        // init LiveAudioRoom SDK
        long appID = AuthInfoManager.getInstance().getAppID();
        String appSign = AuthInfoManager.getInstance().getAppSign();
        ZegoRoomManager.getInstance().init(appID, appSign, this);
    }
}