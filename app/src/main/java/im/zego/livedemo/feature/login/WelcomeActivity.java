package im.zego.livedemo.feature.login;

import android.os.Bundle;

import com.blankj.utilcode.util.LanguageUtils;

import java.util.Locale;

import im.zego.livedemo.R;
import im.zego.livedemo.base.BaseActivity;
import im.zego.livedemo.constants.Constants;
import im.zego.livedemo.databinding.ActivityWelcomeBinding;
import im.zego.livedemo.feature.webview.WebViewActivity;

public class WelcomeActivity extends BaseActivity<ActivityWelcomeBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        if (LanguageUtils.getSystemLanguage().getLanguage().contains(Locale.CHINESE.getLanguage())) {
            binding.layoutLiveShow.setBackgroundResource(R.drawable.icon_live_show_entrance_zh);
        } else {
            binding.layoutLiveShow.setBackgroundResource(R.drawable.icon_live_show_entrance);
        }
        binding.layoutLiveShow.setOnClickListener(v -> UserLoginActivity.start(this));
        binding.tvGetMore.setOnClickListener(v -> WebViewActivity.start(this, Constants.URL_GET_MORE));
        binding.flSignUp.setOnClickListener(v -> WebViewActivity.start(this, Constants.URL_SIGN_UP));
        binding.flContactUs.setOnClickListener(v -> WebViewActivity.start(this, Constants.URL_CONTACT_US));
    }
}