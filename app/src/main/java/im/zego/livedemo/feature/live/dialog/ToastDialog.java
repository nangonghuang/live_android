package im.zego.livedemo.feature.live.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.Utils;

import im.zego.livedemo.R;
import im.zego.livedemo.helper.ToastHelper;

public class ToastDialog extends Dialog {

    private TextView textView;

    public ToastDialog(@NonNull Context context) {
        super(context);
        initDialog(context);
    }

    public ToastDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initDialog(context);
    }

    private void initDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_top_toast_view, null);
        textView = view.findViewById(R.id.tv_toast_message);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setContentView(view);

        Window window = getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.TOP);
        window.setDimAmount(0F);
    }

    public void showColorToast(ToastHelper.ToastMessageType type, String text) {
        textView.setText(text);
        if (type == ToastHelper.ToastMessageType.NORMAL) {
            textView.setBackgroundColor(Utils.getApp().getResources().getColor(R.color.light_green));
        } else {
            textView.setBackgroundColor(Utils.getApp().getResources().getColor(R.color.light_red));
        }
    }
}
