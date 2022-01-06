package im.zego.livedemo.feature.live.adapter;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseViewHolder;
import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.view.BeautyInfo;
import java.util.List;

/**
 * 美颜美型adapter
 */
public class EffectsBeautyAdapter extends BaseSelectAdapter<BeautyInfo, BaseViewHolder> {

    public EffectsBeautyAdapter(@Nullable List<BeautyInfo> data) {
        super(R.layout.liveshow_voice_change_item, data);
        setCurrentType(SelectType.single);
    }

    @Override
    protected void selectConvert(boolean isSelect, BaseViewHolder helper, BeautyInfo item) {
        TextView voiceName = helper.getView(R.id.tv_voice_change_name);
        ImageView voiceIcon = helper.getView(R.id.iv_voice_change);

        voiceName.setText(item.getBeautyName());

        if (isSelect) {
            voiceIcon.setImageResource(item.getBeautyIconSelect());
            voiceName.setTextColor(Color.parseColor("#ffa653ff"));
        } else {
            voiceIcon.setImageResource(item.getBeautyIconUnSelect());
            voiceName.setTextColor(Color.parseColor("#ffcccccc"));
        }

    }


}
