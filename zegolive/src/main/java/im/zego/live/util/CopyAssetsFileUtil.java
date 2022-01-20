package im.zego.live.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CopyAssetsFileUtil {

    public static String copyAssetsFile2Phone(Context context, String fileName) {
        InputStream inputStream = null;
        FileOutputStream fos = null;
        try {
            File file = new File(context.getFilesDir().getAbsolutePath() + File.separator + fileName);
            inputStream = context.getAssets().open(fileName);

            if (!file.exists() || file.length() != inputStream.available()) {
                File folder = file.getParentFile();
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                fos = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024 * 8];
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
            }
            return file.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignore) {
                }
            }
        }
        return null;
    }
}
