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
import im.zego.live.service.ZegoSoundEffectService;
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
 * Created by rocket_wang on 2021/12/14.
 */
public class ZegoRoomManager {

    private static volatile ZegoRoomManager singleton = null;

    private ZegoRoomManager() {
    }

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
    public ZegoRoomService roomService;
    public ZegoUserService userService;
    public ZegoMessageService messageService;
    public ZegoFaceBeautifyService faceBeautifyService;
    public ZegoSoundEffectService soundEffectService;
    public ZegoDeviceService deviceService;

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
        });
        soundEffectService = new ZegoSoundEffectService(engine);

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

    public void unInit() {
        ZegoZIMManager.getInstance().destroyZIM();
        ZegoExpressEngine.destroyEngine(null);
        if (faceBeautifyService != null) {
            faceBeautifyService.zegoEffects.destroy();
        }
    }

    public void uploadLog(final ZegoRoomCallback callback) {
        ZegoZIMManager.getInstance().zim
            .uploadLog(errorInfo -> callback.onRoomCallback(errorInfo.code.value()));
        ZegoExpressEngine.getEngine().uploadLog();
    }
}