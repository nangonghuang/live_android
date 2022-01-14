package im.zego.livedemo.helper;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.blankj.utilcode.util.ResourceUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class AvatarHelper {
    private static final String TAG = "AvatarHelper";

    private static final int MAX_INDEX = 6;

    public static Drawable getAvatarByUserName(String userName) {
        return ResourceUtils.getDrawable(getAvatarIdByUserName(userName));
    }

    public static int getAvatarIdByUserName(String userName) {
        int index = getIndex(userName);
        return getUserAvatarId(index);
    }

    private static int getUserAvatarId(int position) {
        return ResourceUtils.getDrawableIdByName("icon_avatar_" + (position % MAX_INDEX + 1));
    }

    public static int getIndex(String userName) {
        byte[] value;
        try {
            value = md5(userName);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return 0;
        }

        if (value.length > 0) {
            String hex = bytesToHex(value);
            int value0 = value[0] & 0xff;
            int index = Math.abs(value0 % MAX_INDEX);
            Log.d(TAG, "getIndex: md5=" + hex + ", value[0]=" + value0 + ", index=" + index);
            return index;
        } else {
            return 0;
        }
    }

    private static byte[] md5(String input) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("MD5").digest(input.getBytes());
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}