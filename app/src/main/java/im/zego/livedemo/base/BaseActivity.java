package im.zego.livedemo.base;

import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.gyf.immersionbar.ImmersionBar;

import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.dialog.ToastDialog;
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

    @ColorRes
    protected int getStatusBarColor() {
        return R.color.common_bg;
    }

    protected void showNormalToastDialog(String content) {
        dialog.showColorToast(ToastHelper.ToastMessageType.NORMAL, content);
        dialog.show();
    }

    protected void showErrorToastDialog(String content) {
        dialog.showColorToast(ToastHelper.ToastMessageType.WARN, content);
        dialog.show();
    }

    protected void dismissAllToast() {
        dialog.dismiss();
    }
}