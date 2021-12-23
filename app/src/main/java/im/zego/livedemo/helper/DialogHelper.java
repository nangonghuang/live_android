package im.zego.livedemo.helper;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import im.zego.livedemo.feature.room.dialog.CommonDialog;
import im.zego.livedemo.feature.room.dialog.ConfirmDialog;

public final class DialogHelper {
    public static void showAlertDialog(
            Context context,
            String title,
            String content,
            String positiveText,
            String negativeText,
            DialogInterface.OnClickListener positiveClickListener,
            DialogInterface.OnClickListener negativeClickListener
    ) {
        CommonDialog.Builder builder = new CommonDialog.Builder(context);
        builder.setTitle(title);
        builder.setContent(content);

        builder.setPositiveButton(positiveText, positiveClickListener);
        builder.setNegativeButton(negativeText, negativeClickListener);

        CommonDialog dialog = builder.create();
        dialog.show();
    }

    public static void showToastDialog(
            Context context,
            @NonNull String text,
            @NonNull ConfirmDialog.IDialogListener listener
    ) {
        ConfirmDialog confirmDialog = new ConfirmDialog(context, dialog -> {
            dialog.dismiss();
            listener.onConfirmClick(dialog);
        });
        confirmDialog.setTitle(text);
        confirmDialog.show();
    }
}