package im.zego.livedemo.feature.live.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import im.zego.live.model.FaceBeautifyType;
import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.adapter.EffectsBeautyAdapter;

public class EffectsBeautyView extends ConstraintLayout {

    private IBeautyCallBack callBack;

    private RecyclerView mRvBeauty;
    private EffectsBeautyAdapter effectsBeautyAdapter;

    public EffectsBeautyView(@NonNull Context context) {
        this(context, null);
    }

    public EffectsBeautyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EffectsBeautyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = inflate(context, R.layout.liveshow_view_beauty, this);
        mRvBeauty = view.findViewById(R.id.rv_beauty);

        setAdaptet();

    }

    public void setAdaptet() {
        this.mRvBeauty.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        this.effectsBeautyAdapter = new EffectsBeautyAdapter(createBeautyList());
        this.mRvBeauty.setAdapter(this.effectsBeautyAdapter);
        this.effectsBeautyAdapter.setOnItemClickListener((adapter, view, position) -> {
            effectsBeautyAdapter.notifySelect(position, false);
            returnData(position);
        });
    }

    private void returnData(int position) {
        switch (position) {
            case 0:
                if (callBack != null) {
                    callBack.onClickBeauty(FaceBeautifyType.SkinToneEnhancement, 0,position);
                }
                break;
            case 1:
                if (callBack != null) {
                    callBack.onClickBeauty(FaceBeautifyType.SkinSmoothing, 0,position);
                }
                break;
            case 2:
                if (callBack != null) {
                    callBack.onClickBeauty(FaceBeautifyType.ImageSharpening, 0,position);
                }
                break;
            case 3:
                if (callBack != null) {
                    callBack.onClickBeauty(FaceBeautifyType.CheekBlusher, 0,position);
                }
                break;
        }
    }

    public void notifyData() {
        effectsBeautyAdapter = null;
    }

    public void notifyPosition(int position) {
       if(position != -1 && effectsBeautyAdapter != null){
           effectsBeautyAdapter.notifySelect(position,true);
       }
    }

    private List<BeautyInfo> createBeautyList() {
        List<BeautyInfo> list = new ArrayList<>();
        String[] beautyList = getResources().getStringArray(R.array.beauty_list);
        for (int i = 0; i < beautyList.length; ++i) {
            String beautyName = beautyList[i];
            int beautyIconUnselect = BeautyInfo.beautyUnselect[i];
            int beautyIconSelect = BeautyInfo.beautySelect[i];
            list.add(new BeautyInfo().setBeautyName(beautyName).setBeautyIconUnSelect(beautyIconUnselect).setBeautyIconSelect(beautyIconSelect));
        }
        return list;
    }

    public void setCallBack(IBeautyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface IBeautyCallBack {
        void onClickBeauty(FaceBeautifyType type, int minProgress,int position);
    }

}
