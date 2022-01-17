package im.zego.livedemo.base;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.gyf.immersionbar.ImmersionBar;

import java.util.List;

import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.dialog.ToastDialog;
import im.zego.livedemo.feature.login.WelcomeActivity;
import im.zego.livedemo.helper.ToastHelper;

public abstract class BaseActivity<VB extends ViewBinding> extends BaseBindingActivity<VB> {
    private static final String TAG = "BaseActivity";

    protected ToastDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .statusBarColor(getStatusBarColor())
                .fitsSystemWindows(true)
                .statusBarDarkFont(false)
                .init();
        dialog = new ToastDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean needRestart = true;
        StringBuilder sb = new StringBuilder();
        List<Activity> activities = ActivityUtils.getActivityList();
        for (Activity activity : activities) {
            sb.append("activity=");
            sb.append(activity.getLocalClassName());
            sb.append(", ");

            if (activity.getComponentName().getClassName().contains(WelcomeActivity.class.getName())) {
                needRestart = false;
            }
        }
        Log.d(TAG, "BaseActivity onResume() called with: " + sb.toString());

        if (needRestart) {
            AppUtils.relaunchApp(true);
        }
    }

    @ColorRes
    protected int getStatusBarColor() {
        return R.color.common_bg;
    }

    protected void showNormalToastDialog(String content) {
        if (isFinishing()) return;
        try {
            dialog.showColorToast(ToastHelper.ToastMessageType.NORMAL, content);
            dialog.show();
        } catch (Exception ignore) {
        }
    }

    protected void dismissAllToast() {
        if (isFinishing()) return;
        try {
            dialog.dismiss();
        } catch (Exception ignore) {
        }
    }
}