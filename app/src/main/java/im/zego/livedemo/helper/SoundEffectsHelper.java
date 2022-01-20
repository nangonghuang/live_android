package im.zego.livedemo.helper;

import android.content.Context;
import android.util.ArrayMap;

import com.blankj.utilcode.util.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import im.zego.live.util.CopyAssetsFileUtil;
import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.model.AudioEffectInfo;

/**
 * Created by rocket_wang on 2022/1/5.
 */
public class SoundEffectsHelper {
    public static final Map<Integer, String> songFileMap = new ArrayMap<>();

    public static void initLocalAudioEffectList(Context context) {
        songFileMap.clear();

        List<AudioEffectInfo> audioEffectInfos = new ArrayList<>();

        String[] musicList = context.getResources().getStringArray(R.array.background_music);
        for (int i = 0; i < musicList.length; ++i) {
            String musicName = musicList[i];
            String fileAssetsPath = musicName + ".mp3";
            audioEffectInfos.add(new AudioEffectInfo().setId(i).setFileAssetsPath(fileAssetsPath));
        }

        for (AudioEffectInfo audioEffectInfo : audioEffectInfos) {
            ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<String>() {
                @Override
                public String doInBackground() {
                    return CopyAssetsFileUtil.copyAssetsFile2Phone(context, audioEffectInfo.getFileAssetsPath());
                }

                @Override
                public void onSuccess(String path) {
                    songFileMap.put(audioEffectInfo.getId(), path);
                }
            });
        }
    }
}