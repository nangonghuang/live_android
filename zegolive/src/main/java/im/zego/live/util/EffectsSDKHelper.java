package im.zego.live.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class EffectsSDKHelper {

    public static ArrayList<String> copyAiModeInfoList(Context context) {
        String path = context.getExternalCacheDir().getPath();
        String faceDetection = "effects/FaceDetectionModel.model";
        String segmentation = "effects/SegmentationModel.model";
        copyFileFromAssets(context, faceDetection, path + File.separator + faceDetection);
        copyFileFromAssets(context, segmentation, path + File.separator + segmentation);
        ArrayList<String> aiModeInfoList = new ArrayList<>();
        aiModeInfoList.add(path + File.separator + faceDetection);
        aiModeInfoList.add(path + File.separator + segmentation);

        return aiModeInfoList;
    }

    public static ArrayList<String> copyResourcesInfoList(Context context) {
        String path = context.getExternalCacheDir().getPath();
        String faceWhitening = "effects/FaceWhiteningResources.bundle";
        String common = "effects/CommonResources.bundle";

        String rosyResources = "effects/RosyResources.bundle";
        String teethWhiteningResources = "effects/TeethWhiteningResources.bundle";

        copyFileFromAssets(context, faceWhitening, path + File.separator + faceWhitening);
        copyFileFromAssets(context, common, path + File.separator + common);
        copyFileFromAssets(context, rosyResources, path + File.separator + rosyResources);
        copyFileFromAssets(context, teethWhiteningResources, path + File.separator + teethWhiteningResources);

        ArrayList<String> resourcesInfoList = new ArrayList<>();
        resourcesInfoList.add(path + File.separator + faceWhitening);
        resourcesInfoList.add(path + File.separator + common);
        resourcesInfoList.add(path + File.separator + rosyResources);
        resourcesInfoList.add(path + File.separator + teethWhiteningResources);

        return resourcesInfoList;
    }

    public static void copyFileFromAssets(Context context, String assetsFilePath, String targetFileFullPath) {

        try {
            if (assetsFilePath.endsWith(File.separator)) {
                assetsFilePath = assetsFilePath.substring(0, assetsFilePath.length() - 1);
            }
            String fileNames[] = context.getAssets().list(assetsFilePath);
            if (fileNames.length > 0) {
                File file = new File(targetFileFullPath);
                file.mkdirs();
                for (String fileName : fileNames) {
                    copyFileFromAssets(context, assetsFilePath + File.separator + fileName,
                            targetFileFullPath + File.separator + fileName);
                }
            } else {

                File file = new File(targetFileFullPath);
                File fileTemp = new File(targetFileFullPath + ".temp");
                if (file.exists()) {
                    return;
                }
                fileTemp.getParentFile().mkdir();

                InputStream is = context.getAssets().open(assetsFilePath);

                FileOutputStream fos = new FileOutputStream(fileTemp);
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();

                fileTemp.renameTo(file);
            }

        } catch (Exception e) {
            Log.d("Tag", "copyFileFromAssets " + "IOException-" + e.getMessage());
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
