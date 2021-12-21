package im.zego.livedemo;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import im.zego.live.ZegoRoomManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        LogUtils.getConfig().setLogHeadSwitch(false);
        LogUtils.getConfig().setBorderSwitch(false);
        // init Live SDK
        ZegoRoomManager.getInstance().init(KeyCenter.appID(), KeyCenter.appExpressSign(), this);
    }
}