package im.zego.live.service;

import android.util.Log;
import im.zego.live.model.FaceBeautifyType;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.entity.ZegoEffectsBeautyParam;

/**
 * Class face beautify management
 * Description: This class contains the enabling/disabling logic, and the parameter setting logic of the face beautify feature.
 */
public class ZegoFaceBeautifyService {

    private static final String TAG = "Beautify";
    ZegoEffectsBeautyParam beautyParam = new ZegoEffectsBeautyParam();

    public ZegoFaceBeautifyService() {
        ZegoExpressEngine.getEngine().startEffectsEnv();
        initBeauty();
    }

    /**
     * Enable the face beautify feature (include the face beautification and face shape retouch).
     * <p>
     * Description: When this method gets called, the captured video streams will be processed before publishing to the remote users.
     * <p>
     * Call this method at: When joining a room
     *
     * @param enable determines whether to enable or disable the the face beautify feature.  true: enable.  false: disable.
     */
    public void enableBeautify(boolean enable) {
       ZegoExpressEngine.getEngine().enableEffectsBeauty(enable);
    }

    private void initBeauty() {
        setBeautifyValue(50, FaceBeautifyType.SkinToneEnhancement);
        setBeautifyValue(50, FaceBeautifyType.SkinSmoothing);
        setBeautifyValue(50, FaceBeautifyType.ImageSharpening);
        setBeautifyValue(5, FaceBeautifyType.CheekBlusher);
    }

    public void resetBeauty() {
        setBeautifyValue(50, FaceBeautifyType.SkinToneEnhancement);
        setBeautifyValue(50, FaceBeautifyType.SkinSmoothing);
        setBeautifyValue(50, FaceBeautifyType.ImageSharpening);
        setBeautifyValue(5, FaceBeautifyType.CheekBlusher);
    }

    public void resetReSharp() {

    }

    public int getBeautifyValue(FaceBeautifyType type) {
        int value = 0;
        switch (type) {
            case SkinToneEnhancement:
                value = beautyParam.whitenIntensity;
                break;
            case SkinSmoothing:
                value = beautyParam.smoothIntensity;
                break;
            case ImageSharpening:
                value = beautyParam.sharpenIntensity;
                break;
            case CheekBlusher:
                value = beautyParam.rosyIntensity;
                break;
            default:
                break;
        }
        Log.d(TAG, "getBeautifyValue() returned: " + value + ",type:" + type);
        return value;
    }

    /**
     * Set the intensity of the specific face beautify feature
     * <p>
     * Description: After the face beautify feature is enabled, you can specify the parameters to set the intensity of the specific feature as needed.
     * <p>
     * Call this method at: After enabling the face beautify feature.
     *
     * @param value refers to the range value of the specific face beautification feature or face shape retouch feature.
     * @param type  refers to the specific face beautification feature or face shape retouch feature.
     */
    public void setBeautifyValue(int value, FaceBeautifyType type) {
        Log.d(TAG, "setBeautifyValue() called with: value = [" + value + "], type = [" + type + "]");
        switch (type) {
            case SkinToneEnhancement:
                beautyParam.whitenIntensity = value;
                break;
            case SkinSmoothing:
                beautyParam.smoothIntensity = value;
                break;
            case ImageSharpening:
                beautyParam.sharpenIntensity = value;
                break;
            case CheekBlusher:
                beautyParam.rosyIntensity = value;
                break;
            default:
                break;
        }
        ZegoExpressEngine.getEngine().setEffectsBeautyParam(beautyParam);
    }
}
