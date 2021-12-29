package im.zego.livedemo.helper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import im.zego.livedemo.feature.room.dialog.CommonDialog;
import im.zego.livedemo.feature.room.dialog.ConfirmDialog;

public final class DialogHelper {
    public static Dialog showAlertDialog(
            Context context,
            String title,
            String content,
            String positiveText,
            String negativeText,
            DialogInterface.OnClickListener positiveClickListener,
            DialogInterface.OnClickListener negativeClickListener
    ) {
        CommonDialog.Builder builder = new CommonDialog.Builder(context)
                .setTitle(title)
                .setContent(content)
                .setPositiveButton(positiveText, positiveClickListener)
                .setNegativeButton(negativeText, negativeClickListener);

        CommonDialog dialog = builder.create();
        dialog.show();
        return dialog;
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