package im.zego.livedemo.feature.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.StringUtils;

import im.zego.effects.ZegoEffects;
import im.zego.live.ZegoRoomManager;
import im.zego.live.constants.ZegoRoomErrorCode;
import im.zego.livedemo.BuildConfig;
import im.zego.livedemo.R;
import im.zego.livedemo.base.BaseActivity;
import im.zego.livedemo.constants.Constants;
import im.zego.livedemo.databinding.ActivitySettingsBinding;
import im.zego.livedemo.feature.login.UserLoginActivity;
import im.zego.livedemo.feature.webview.WebViewActivity;
import im.zego.livedemo.helper.ToastHelper;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zim.ZIM;

public class SettingsActivity extends BaseActivity<ActivitySettingsBinding> {

    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initListener();
    }

    protected void initData() {
        binding.tvRtcSdkVersion.setText(ZegoExpressEngine.getVersion());
        binding.tvZimSdkVersion.setText(ZIM.getVersion());
        binding.tvEffectsSdkVersion.setText(ZegoEffects.getVersion());
        binding.tvAppVersion.setText(BuildConfig.VERSION_NAME);
    }

    protected void initListener() {
        binding.commonTitleView.setBackBtnClickListener(v -> finish());
        binding.tvLogout.setOnClickListener(v -> logout());

        binding.layoutTermsOfService.setOnClickListener(v -> WebViewActivity.start(this, Constants.URL_TERMS_OF_SERVICE));
        binding.layoutPrivacyPolicy.setOnClickListener(v -> WebViewActivity.start(this, Constants.URL_PRIVACY_POLICY));
        binding.layoutShareLog.setOnClickListener(v -> ZegoRoomManager.getInstance().uploadLog(errorCode -> {
            if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                ToastHelper.showNormalToast(R.string.toast_upload_log_success);
            } else {
                ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_upload_log_fail, errorCode));
            }
        }));
    }

    private void logout() {
        ZegoRoomManager.getInstance().userService.logout();
        ActivityUtils.finishToActivity(UserLoginActivity.class, false);
    }
}