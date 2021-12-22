package im.zego.livedemo.base;

import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.gyf.immersionbar.ImmersionBar;

import im.zego.livedemo.R;

public abstract class BaseActivity<VB extends ViewBinding> extends BaseBindingActivity<VB> {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .statusBarColor(getStatusBarColor())
                .fitsSystemWindows(true)
                .statusBarDarkFont(false)
                .init();
    }

    @ColorRes
    protected int getStatusBarColor() {
        return R.color.common_bg;
    }
}
