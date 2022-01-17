package im.zego.livedemo.helper;

import android.content.Context;
import android.content.Intent;

import im.zego.livedemo.R;

/**
 * Created by rocket_wang on 2022/1/8.
 */
public class ShareHelper {

    public static void startToShare(Context context, String content) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        shareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, null));
    }
}