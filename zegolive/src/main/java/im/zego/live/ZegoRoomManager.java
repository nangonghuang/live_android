package im.zego.live;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import im.zego.live.callback.ZegoRoomCallback;
import im.zego.live.http.IGetLicenseCallback;
import im.zego.live.http.License;
import im.zego.live.service.ZegoDeviceService;
import im.zego.live.service.ZegoFaceBeautifyService;
import im.zego.live.service.ZegoMessageService;
import im.zego.live.service.ZegoRoomService;
import im.zego.live.service.ZegoSoundEffectsService;
import im.zego.live.service.ZegoUserService;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoCustomVideoProcessHandler;
import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.constants.ZegoPublishChannel;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.constants.ZegoStreamQualityLevel;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.constants.ZegoVideoBufferType;
import im.zego.zegoexpress.entity.ZegoCustomVideoProcessConfig;
import im.zego.zegoexpress.entity.ZegoEngineProfile;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMEventHandler;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMMessage;
import im.zego.zim.entity.ZIMRoomAttributesUpdateInfo;
import im.zego.zim.entity.ZIMUserInfo;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import im.zego.zim.enums.ZIMRoomEvent;
import im.zego.zim.enums.ZIMRoomState;

/**
 * Class ZEGO Live business logic management
 *
 * Description: This class contains the ZEGO Live business logic, manages the service instances of different modules, and also distributing the data delivered by the SDK.
 */
public class ZegoRoomManager {

    private static volatile ZegoRoomManager singleton = null;

    private ZegoRoomManager() {
    }

    /**
     * Get the ZegoRoomManager singleton instance
     *
     * Description: This method can be used to get the ZegoRoomManager singleton instance.
     *
     * Call this method at: Any time
     *
     * @return ZegoRoomManager singleton instance
     */
    public static ZegoRoomManager getInstance() {
        if (singleton == null) {
            synchronized (ZegoRoomManager.class) {
                if (singleton == null) {
                    singleton = new ZegoRoomManager();
                }
            }
        }
        return singleton;
    }

    private static final String TAG = "ZegoRoomManager";

    // The room information management instance,
    // contains the room information, room status and other business logic.
    public ZegoRoomService roomService;

    // The user information management instance,
    // contains the in-room user information management, logged-in user information and other business logic.
    public ZegoUserService userService;

    // The message management instance, contains the IM messages management logic.
    public ZegoMessageService messageService;

    // The face beautify management instance,
    // contains the enabling/disabling logic and parameter setting logic of the face beautification and face shape retouch feature.
    public ZegoFaceBeautifyService faceBeautifyService;

    // The sound effects management instance,
    // contains the sound effects business logic.
    public ZegoSoundEffectsService soundEffectService;

    // The device management instance,
    // contains the video capturing, rendering, related parameters, stream playing, and other business logic.
    public ZegoDeviceService deviceService;

    /**
     * Initialize the SDK
     *
     * Description: This method can be used to initialize the ZIM SDK and the Express-audio SDK.
     *
     * Call this method at: Before you log in. We recommend you call this method when the application starts.
     *
     * @param appID refers to the project ID. To get this, go to <a href="https://console.zegocloud.com/dashboard?lang=en">ZEGOCLOUD Admin Console</a>
     * @param appSign refers to the secret key for authentication. To get this, go to <a href="https://console.zegocloud.com/dashboard?lang=en">ZEGOCLOUD Admin Console</a>
     */
    public void init(long appID, String appSign, Application application) {
        roomService = new ZegoRoomService();
        userService = new ZegoUserService();
        messageService = new ZegoMessageService();
        faceBeautifyService = new ZegoFaceBeautifyService(application);
        deviceService = new ZegoDeviceService();

        ZegoEngineProfile profile = new ZegoEngineProfile();
        profile.appID = appID;
        profile.appSign = appSign;
        profile.scenario = ZegoScenario.COMMUNICATION;
        profile.application = application;
        ZegoExpressEngine engine = ZegoExpressEngine.createEngine(profile, new IZegoEventHandler() {
            @Override
            public void onNetworkQuality(String userID, ZegoStreamQualityLevel upstreamQuality,
                ZegoStreamQualityLevel downstreamQuality) {
                super.onNetworkQuality(userID, upstreamQuality, downstreamQuality);
            }

            @Override
            public void onCapturedSoundLevelUpdate(float soundLevel) {
                super.onCapturedSoundLevelUpdate(soundLevel);
            }


            @Override
            public void onRemoteSoundLevelUpdate(HashMap<String, Float> soundLevels) {
                super.onRemoteSoundLevelUpdate(soundLevels);
            }

            @Override
            public void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoStream> streamList,
                JSONObject extendedData) {
                super.onRoomStreamUpdate(roomID, updateType, streamList, extendedData);
                if (roomService != null) {
                    roomService.onRoomStreamUpdate(roomID, updateType, streamList);
                }
            }

            @Override
            public void onRoomTokenWillExpire(String roomID, int remainTimeInSecond) {
                super.onRoomTokenWillExpire(roomID, remainTimeInSecond);
                if (roomService != null) {
                    roomService.onRoomTokenWillExpire(remainTimeInSecond, roomID);
                }
            }
        });
        soundEffectService = new ZegoSoundEffectsService(engine);

        ZegoZIMManager.getInstance().createZIM(appID, application);
        // distribute to specific services which listening what they want
        ZegoZIMManager.getInstance().zim.setEventHandler(new ZIMEventHandler() {
            @Override
            public void onConnectionStateChanged(ZIM zim, ZIMConnectionState state, ZIMConnectionEvent event,
                JSONObject extendedData) {
                super.onConnectionStateChanged(zim, state, event, extendedData);
                if (roomService != null) {
                    roomService.onConnectionStateChanged(zim, state, event, extendedData);
                }
            }

            @Override
            public void onError(ZIM zim, ZIMError errorInfo) {
                super.onError(zim, errorInfo);
            }

            @Override
            public void onTokenWillExpire(ZIM zim, int second) {
                super.onTokenWillExpire(zim, second);
                if (roomService != null) {
                    roomService.onRoomTokenWillExpire(second, roomService.roomInfo.getRoomID());
                }
            }

            @Override
            public void onReceivePeerMessage(ZIM zim, ArrayList<ZIMMessage> messageList, String fromUserID) {
                super.onReceivePeerMessage(zim, messageList, fromUserID);
                if (userService != null) {
                    userService.onReceivePeerMessage(zim, messageList, fromUserID);
                }
            }

            @Override
            public void onReceiveRoomMessage(ZIM zim, ArrayList<ZIMMessage> messageList, String fromRoomID) {
                super.onReceiveRoomMessage(zim, messageList, fromRoomID);
                if (messageService != null) {
                    messageService.onReceiveRoomMessage(zim, messageList, fromRoomID);
                }
            }

            @Override
            public void onRoomMemberJoined(ZIM zim, ArrayList<ZIMUserInfo> memberList, String roomID) {
                super.onRoomMemberJoined(zim, memberList, roomID);
                if (userService != null) {
                    userService.onRoomMemberJoined(zim, memberList, roomID);
                }
            }

            @Override
            public void onRoomMemberLeft(ZIM zim, ArrayList<ZIMUserInfo> memberList, String roomID) {
                super.onRoomMemberLeft(zim, memberList, roomID);
                if (userService != null) {
                    userService.onRoomMemberLeft(zim, memberList, roomID);
                }
            }

            @Override
            public void onRoomStateChanged(ZIM zim, ZIMRoomState state, ZIMRoomEvent event, JSONObject extendedData,
                String roomID) {
                super.onRoomStateChanged(zim, state, event, extendedData, roomID);
                if (roomService != null) {
                    roomService.onRoomStateChanged(zim, state, event, extendedData, roomID);
                }
            }

            @Override
            public void onRoomAttributesUpdated(ZIM zim, ZIMRoomAttributesUpdateInfo info, String roomID) {
                super.onRoomAttributesUpdated(zim, info, roomID);
                if (roomService != null) {
                    roomService.onRoomAttributesUpdated(zim, info, roomID);
                }
            }

            @Override
            public void onRoomAttributesBatchUpdated(ZIM zim, ArrayList<ZIMRoomAttributesUpdateInfo> infos,
                String roomID) {
                super.onRoomAttributesBatchUpdated(zim, infos, roomID);
            }
        });

        faceBeautifyService.init(application, appID, appSign, new IGetLicenseCallback() {
            @Override
            public void onGetLicense(int code, String message, License license) {
                Log.d("Beautify", "onGetLicense() called with: code = [" + code + "], message = [" + message);

                ZegoCustomVideoProcessConfig config = new ZegoCustomVideoProcessConfig();
                config.bufferType = ZegoVideoBufferType.GL_TEXTURE_2D;
                ZegoExpressEngine.getEngine().enableCustomVideoProcessing(true, config, ZegoPublishChannel.MAIN);
                ZegoExpressEngine.getEngine().setCustomVideoProcessHandler(new IZegoCustomVideoProcessHandler() {

                    @Override
                    public void onStart(ZegoPublishChannel channel) {
//                        faceBeautifyService.onStart();
                    }

                    @Override
                    public void onStop(ZegoPublishChannel channel) {
                        faceBeautifyService.onStop();
                    }

                    @Override
                    public void onCapturedUnprocessedTextureData(int textureID, int width, int height,
                        long referenceTimeMillisecond, ZegoPublishChannel channel) {

                        // Process buffer by ZegoEffects
                        int processedTextureID = faceBeautifyService.gainProcessedTextureID(textureID, width, height);

                        // Send processed texture to ZegoExpressEngine
                        ZegoExpressEngine.getEngine().sendCustomVideoProcessedTextureData(processedTextureID, width, height,
                                referenceTimeMillisecond);
                    }
                });
            }
        });
    }

    /**
     * The method to deinitialize the SDK
     *
     * Description: This method can be used to deinitialize the SDK and release the resources it occupies.
     *
     * Call this method at: When the SDK is no longer be used. We recommend you call this method when the application exits.
     */
    public void unInit() {
        ZegoZIMManager.getInstance().destroyZIM();
        ZegoExpressEngine.destroyEngine(null);
        if (faceBeautifyService != null) {
            faceBeautifyService.zegoEffects.destroy();
        }
    }

    /**
     * Upload local logs to the ZEGOCLOUD server
     *
     * Description: You can call this method to upload the local logs to the ZEGOCLOUD Server for troubleshooting when exception occurs.
     *
     * Call this method at: When exceptions occur.
     *
     * @param callback refers to the callback that be triggered when the logs are upload successfully or failed to upload logs.
     */
    public void uploadLog(final ZegoRoomCallback callback) {
        ZegoZIMManager.getInstance().zim
            .uploadLog(errorInfo -> callback.onRoomCallback(errorInfo.code.value()));
        ZegoExpressEngine.getEngine().uploadLog();
    }
}
