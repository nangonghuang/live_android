package im.zego.livedemo.feature.webview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebChromeClient;

import im.zego.livedemo.R;
import im.zego.livedemo.base.BaseActivity;
import im.zego.livedemo.databinding.ActivityWebViewBinding;

/**
 * Created by rocket_wang on 2021/12/22.
 */
public class WebViewActivity extends BaseActivity<ActivityWebViewBinding> {

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_KEY_URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static final String EXTRA_KEY_URL = "extra_key_url";
    private AgentWeb mAgentWeb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra(EXTRA_KEY_URL);

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(binding.webViewContainer, new FrameLayout.LayoutParams(-1, -1))
                .useDefaultIndicator(ColorUtils.getColor(R.color.purple), SizeUtils.dp2px(1f))
                .setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
                        binding.commonTitleView.setTitle(title);
                    }
                })
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .createAgentWeb()
                .ready()
                .go(url);
        binding.commonTitleView.setBackBtnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        if (!mAgentWeb.back()) {
            super.onBackPressed();
        }
    }
}