package im.zego.live.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import im.zego.effects.ZegoEffects;
import im.zego.effects.entity.ZegoEffectsBigEyesParam;
import im.zego.effects.entity.ZegoEffectsEyesBrighteningParam;
import im.zego.effects.entity.ZegoEffectsFaceLiftingParam;
import im.zego.effects.entity.ZegoEffectsLongChinParam;
import im.zego.effects.entity.ZegoEffectsNoseNarrowingParam;
import im.zego.effects.entity.ZegoEffectsRosyParam;
import im.zego.effects.entity.ZegoEffectsSharpenParam;
import im.zego.effects.entity.ZegoEffectsSmallMouthParam;
import im.zego.effects.entity.ZegoEffectsSmoothParam;
import im.zego.effects.entity.ZegoEffectsTeethWhiteningParam;
import im.zego.effects.entity.ZegoEffectsWhitenParam;
import im.zego.live.http.APIBase;
import im.zego.live.http.IAsyncGetCallback;
import im.zego.live.http.IGetLicenseCallback;
import im.zego.live.http.License;
import im.zego.live.model.FaceBeautifyType;
import im.zego.live.util.EffectsSDKHelper;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public class FaceBeautifyService {

    private static final String TAG = "Beautify";
    private Context context;
    private String license;
    private final static String BACKEND_API_URL = "https://aieffects-api.zego.im?Action=DescribeEffectsLicense";
    public ZegoEffects zegoEffects;

    private ZegoEffectsWhitenParam skinToneEnhancementParam = new ZegoEffectsWhitenParam();
    private ZegoEffectsSmoothParam skinSmoothingParam = new ZegoEffectsSmoothParam();
    private ZegoEffectsSharpenParam imageSharpeningParam = new ZegoEffectsSharpenParam();
    private ZegoEffectsRosyParam cheekBlusherParam = new ZegoEffectsRosyParam();
    private ZegoEffectsBigEyesParam eyesEnlargingParam = new ZegoEffectsBigEyesParam();
    private ZegoEffectsFaceLiftingParam faceSlimingParam = new ZegoEffectsFaceLiftingParam();
    private ZegoEffectsSmallMouthParam mouthShapeAdjustmentParam = new ZegoEffectsSmallMouthParam();
    private ZegoEffectsEyesBrighteningParam eyesBrighteningParam = new ZegoEffectsEyesBrighteningParam();
    private ZegoEffectsNoseNarrowingParam noseSlimingParam = new ZegoEffectsNoseNarrowingParam();
    private ZegoEffectsTeethWhiteningParam teethWhiteningParam = new ZegoEffectsTeethWhiteningParam();
    private ZegoEffectsLongChinParam chinLengtheningParam = new ZegoEffectsLongChinParam();

    public FaceBeautifyService(Context context) {
        ArrayList<String> aiModeInfoList = EffectsSDKHelper.copyAiModeInfoList(context);
        ArrayList<String> resourcesInfoList = EffectsSDKHelper.copyResourcesInfoList(context);
        resourcesInfoList.addAll(aiModeInfoList);
        ZegoEffects.setResources(resourcesInfoList);
    }

    public void init(Context context, long appID, String appSign, IGetLicenseCallback callback) {
        this.context = context;

        String authInfo = ZegoEffects.getAuthInfo(appSign, context);
        Uri.Builder builder = Uri.parse(BACKEND_API_URL).buildUpon();
        builder.appendQueryParameter("AppId", String.valueOf(appID));
        builder.appendQueryParameter("AuthInfo", authInfo);
        String url = builder.build().toString();

        APIBase.asyncGet(url, License.class, new IAsyncGetCallback<License>() {
            @Override
            public void onResponse(int code, @NotNull String message, License responseJsonBean) {
                if (code == 0) {
                    license = responseJsonBean.getLicense();
                    zegoEffects = ZegoEffects.create(license, context);
                    for (FaceBeautifyType type : FaceBeautifyType.values()) {
                        enableBeautify(true, type);
                    }
                    initBeauty();
                }
                if (callback != null) {
                    callback.onGetLicense(code, message, responseJsonBean);
                }
            }
        });
    }

    public void enableBeautify(boolean enable, FaceBeautifyType type) {
        if (zegoEffects == null && license != null) {
            zegoEffects = ZegoEffects.create(license, context);
        }
        switch (type) {
            case SkinToneEnhancement:
                zegoEffects.enableWhiten(enable);
                break;
            case SkinSmoothing:
                zegoEffects.enableSmooth(enable);
                break;
            case ImageSharpening:
                zegoEffects.enableSharpen(enable);
                break;
            case CheekBlusher:
                zegoEffects.enableRosy(enable);
                break;
            case EyesEnlarging:
                zegoEffects.enableBigEyes(enable);
                break;
            case FaceSliming:
                zegoEffects.enableFaceLifting(enable);
                break;
            case MouthShapeAdjustment:
                zegoEffects.enableSmallMouth(enable);
                break;
            case EyesBrightening:
                zegoEffects.enableEyesBrightening(enable);
                break;
            case NoseSliming:
                zegoEffects.enableNoseNarrowing(enable);
                break;
            case ChinLengthening:
                zegoEffects.enableLongChin(enable);
                break;
            case TeethWhitening:
                zegoEffects.enableTeethWhitening(enable);
                break;
            default:
                break;
        }
    }

    private void initBeauty() {
        setBeautifyValue(50, FaceBeautifyType.SkinToneEnhancement);
        setBeautifyValue(50, FaceBeautifyType.SkinSmoothing);
        setBeautifyValue(50, FaceBeautifyType.ImageSharpening);
        setBeautifyValue(5, FaceBeautifyType.CheekBlusher);
        setBeautifyValue(50, FaceBeautifyType.EyesEnlarging);
        setBeautifyValue(50, FaceBeautifyType.FaceSliming);
        setBeautifyValue(0, FaceBeautifyType.MouthShapeAdjustment);
        setBeautifyValue(50, FaceBeautifyType.EyesBrightening);
        setBeautifyValue(50, FaceBeautifyType.NoseSliming);
        setBeautifyValue(50, FaceBeautifyType.TeethWhitening);
        setBeautifyValue(0, FaceBeautifyType.ChinLengthening);
    }

    public void resetBeauty() {
        setBeautifyValue(50, FaceBeautifyType.SkinToneEnhancement);
        setBeautifyValue(50, FaceBeautifyType.SkinSmoothing);
        setBeautifyValue(50, FaceBeautifyType.ImageSharpening);
        setBeautifyValue(5, FaceBeautifyType.CheekBlusher);
    }

    public void resetReSharp() {
        setBeautifyValue(50, FaceBeautifyType.EyesEnlarging);
        setBeautifyValue(50, FaceBeautifyType.FaceSliming);
        setBeautifyValue(0, FaceBeautifyType.MouthShapeAdjustment);
        setBeautifyValue(50, FaceBeautifyType.EyesBrightening);
        setBeautifyValue(50, FaceBeautifyType.NoseSliming);
        setBeautifyValue(50, FaceBeautifyType.TeethWhitening);
        setBeautifyValue(0, FaceBeautifyType.ChinLengthening);
    }

    public int getBeautifyValue(FaceBeautifyType type) {
        int value = 0;
        switch (type) {
            case SkinToneEnhancement:
                value = skinToneEnhancementParam.intensity;
                break;
            case SkinSmoothing:
                value = skinSmoothingParam.intensity;
                break;
            case ImageSharpening:
                value = imageSharpeningParam.intensity;
                break;
            case CheekBlusher:
                value = cheekBlusherParam.intensity;
                break;
            case EyesEnlarging:
                value = eyesEnlargingParam.intensity;
                break;
            case FaceSliming:
                value = faceSlimingParam.intensity;
                break;
            case MouthShapeAdjustment:
                value = mouthShapeAdjustmentParam.intensity;
                break;
            case EyesBrightening:
                value = eyesBrighteningParam.intensity;
                break;
            case NoseSliming:
                value = noseSlimingParam.intensity;
                break;
            case ChinLengthening:
                value = chinLengtheningParam.intensity;
                break;
            case TeethWhitening:
                value = teethWhiteningParam.intensity;
                break;
            default:
                break;
        }
        Log.d(TAG, "getBeautifyValue() returned: " + value + ",type:" + type);
        return value;
    }

    public void setBeautifyValue(int value, FaceBeautifyType type) {
        Log.d(TAG, "setBeautifyValue() called with: value = [" + value + "], type = [" + type + "]");
        if (zegoEffects == null && license != null) {
            zegoEffects = ZegoEffects.create(license, context);
        }
        switch (type) {
            case SkinToneEnhancement:
                skinToneEnhancementParam.intensity = value;
                zegoEffects.setWhitenParam(skinToneEnhancementParam);
                break;
            case SkinSmoothing:
                skinSmoothingParam.intensity = value;
                zegoEffects.setSmoothParam(skinSmoothingParam);
                break;
            case ImageSharpening:
                imageSharpeningParam.intensity = value;
                zegoEffects.setSharpenParam(imageSharpeningParam);
                break;
            case CheekBlusher:
                cheekBlusherParam.intensity = value;
                zegoEffects.setRosyParam(cheekBlusherParam);
                break;
            case EyesEnlarging:
                eyesEnlargingParam.intensity = value;
                zegoEffects.setBigEyesParam(eyesEnlargingParam);
                break;
            case FaceSliming:
                faceSlimingParam.intensity = value;
                zegoEffects.setFaceLiftingParam(faceSlimingParam);
                break;
            case MouthShapeAdjustment:
                mouthShapeAdjustmentParam.intensity = value;
                zegoEffects.setSmallMouthParam(mouthShapeAdjustmentParam);
                break;
            case EyesBrightening:
                eyesBrighteningParam.intensity = value;
                zegoEffects.setEyesBrighteningParam(eyesBrighteningParam);
                break;
            case NoseSliming:
                noseSlimingParam.intensity = value;
                zegoEffects.setNoseNarrowingParam(noseSlimingParam);
                break;
            case ChinLengthening:
                chinLengtheningParam.intensity = value;
                zegoEffects.setLongChinParam(chinLengtheningParam);
                break;
            case TeethWhitening:
                teethWhiteningParam.intensity = value;
                zegoEffects.setTeethWhiteningParam(teethWhiteningParam);
                break;
            default:
                break;
        }
    }
}
