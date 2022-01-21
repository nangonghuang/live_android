package im.zego.live;

import android.app.Application;

import im.zego.zim.ZIM;

/**
 * Class ZIM SDK management
 * Description: This class contains and manages the ZIM SDK instance objects
 */
public class ZegoZIMManager {

    private static volatile ZegoZIMManager singleton = null;

    private ZegoZIMManager() {
    }

    /**
     * Get the ZegoZIMManager singleton instance
     * <p>
     * Description: This method can be used to get the ZegoZIMManager singleton instance.
     * <p>
     * Call this method at: When you need to use the ZegoZIMManager singleton instance
     *
     * @return ZegoZIMManager singleton instance
     */
    public static ZegoZIMManager getInstance() {
        if (singleton == null) {
            synchronized (ZegoZIMManager.class) {
                if (singleton == null) {
                    singleton = new ZegoZIMManager();
                }
            }
        }
        return singleton;
    }

    // ZIM SDK instance objects
    public ZIM zim;

    /**
     * Description: You need to call this method to initialize the ZIM SDK first before you log in, create a room, join a room, send messages and other operations with ZIM SDK.
     * This method need to be used in conjunction with the [destroyZIM] method, which is to make sure that the current process is running only one ZIM SDK instance.
     * <p>
     * Call this method at: Before you calling the ZIM SDK methods. We recommend you call this method when the application starts.
     *
     * @param appID refers to the ID of your project. To get this, go to <a href="https://console.zegocloud.com/dashboard?lang=en">ZEGOCLOUD Admin Console</a>
     */
    public void createZIM(long appID, Application application) {
        zim = ZIM.create(appID, application);
    }

    /**
     * Destroy the ZIM SDK instance
     *
     * Description: This method can be used to destroy the ZIM SDK instance and release the resources it occupies.
     *
     * Call this method at: When the ZIM SDK is no longer be used. We recommend you call this method when the application exits.
     */
    public void destroyZIM() {
        if (zim != null) {
            zim.destroy();
        }
    }
}