package im.zego.livedemo.helper;


import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.Utils;

import im.zego.livedemo.R;

public class ToastHelper {

    private static final Handler toastHandler = new Handler(Looper.getMainLooper());

    public enum ToastMessageType {
        NORMAL, WARN
    }

    public static void showNormalToast(String message) {
        showToast(ToastMessageType.NORMAL, message);
    }

    public static void showNormalToast(int res) {
        showToast(ToastMessageType.NORMAL, Utils.getApp().getString(res));
    }

    public static void showWarnToast(String message) {
        showToast(ToastMessageType.WARN, message);
    }

    public static void showWarnToast(int res) {
        showToast(ToastMessageType.WARN, Utils.getApp().getString(res));
    }

    private static void showToast(ToastMessageType type, String message) {
        if (!AppUtils.isAppForeground() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            toastHandler.post(() -> Toast.makeText(Utils.getApp(), message, Toast.LENGTH_SHORT).show());
        } else {
            toastHandler.post(() -> {
                if (type == ToastMessageType.NORMAL) {
                    showColorToast(ToastMessageType.NORMAL, message);
                } else {
                    showColorToast(ToastMessageType.WARN, message);
                }
            });
        }
    }

    private static void showColorToast(ToastMessageType type, String message) {
        View view = LayoutInflater.from(Utils.getApp()).inflate(R.layout.layout_top_toast_view, null);
        TextView textView = view.findViewById(R.id.tv_toast_message);
        textView.setText(message);
        if (type == ToastMessageType.NORMAL) {
            textView.setBackgroundColor(Utils.getApp().getResources().getColor(R.color.light_green));
        } else {
            textView.setBackgroundColor(Utils.getApp().getResources().getColor(R.color.light_red));
        }

        Toast toast = new Toast(Utils.getApp());
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, -BarUtils.getStatusBarHeight());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.setMargin(0, 0);
        toast.show();
    }
}
