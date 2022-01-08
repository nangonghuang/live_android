package im.zego.livedemo.feature.login;

import android.os.Bundle;

import im.zego.livedemo.base.BaseActivity;
import im.zego.livedemo.constants.Constants;
import im.zego.livedemo.databinding.ActivityWelcomeBinding;
import im.zego.livedemo.feature.webview.WebViewActivity;

public class WelcomeActivity extends BaseActivity<ActivityWelcomeBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListener();
    }

    private void initListener() {
//        binding.layoutLiveShow.setOnClickListener(v -> {
//            showNormalToastDialog("测试1");
//            ThreadUtils.runOnUiThreadDelayed(() -> {
//                showErrorToastDialog("测试2");
//                ThreadUtils.runOnUiThreadDelayed(this::dismissAllToast, 1000L);
//            }, 2000L);
//        });
        binding.layoutLiveShow.setOnClickListener(v -> UserLoginActivity.start(this));
        binding.tvGetMore.setOnClickListener(v -> WebViewActivity.start(this, Constants.URL_GET_MORE));
        binding.flSignUp.setOnClickListener(v -> WebViewActivity.start(this, Constants.URL_SIGN_UP));
        binding.flContactUs.setOnClickListener(v -> WebViewActivity.start(this, Constants.URL_CONTACT_US));
    }
}