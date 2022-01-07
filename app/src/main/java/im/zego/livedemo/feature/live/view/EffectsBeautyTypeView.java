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

public class EffectsBeautyTypeView extends ConstraintLayout {

    private IBeautyTypeCallBack callBack;

    private RecyclerView mRvBeauty;
    private EffectsBeautyAdapter effectsBeautyAdapter;

    public EffectsBeautyTypeView(@NonNull Context context) {
        this(context, null);
    }

    public EffectsBeautyTypeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EffectsBeautyTypeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = inflate(context, R.layout.liveshow_view_beauty, this);

        mRvBeauty = view.findViewById(R.id.rv_beauty);

        setAdapter();

    }

    public void setAdapter() {
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
                    callBack.onClickBeautyType(FaceBeautifyType.EyesEnlarging, 0, position);
                }
                break;
            case 1:
                if (callBack != null) {
                    callBack.onClickBeautyType(FaceBeautifyType.FaceSliming, 0, position);
                }
                break;
            case 2:
                if (callBack != null) {
                    callBack.onClickBeautyType(FaceBeautifyType.MouthShapeAdjustment, -100, position);
                }
                break;
            case 3:
                if (callBack != null) {
                    callBack.onClickBeautyType(FaceBeautifyType.EyesBrightening, 0, position);
                }
                break;
            case 4:
                if (callBack != null) {
                    callBack.onClickBeautyType(FaceBeautifyType.NoseSliming, 0, position);
                }
                break;
            case 5:
                if (callBack != null) {
                    callBack.onClickBeautyType(FaceBeautifyType.TeethWhitening, 0, position);
                }
                break;
            case 6:
                if (callBack != null) {
                    callBack.onClickBeautyType(FaceBeautifyType.ChinLengthening, -100, position);
                }
                break;
        }
    }

    public void notifyData() {
        effectsBeautyAdapter = null;
    }

    public void notifyPosition(int position) {
        if (position != -1 && effectsBeautyAdapter != null) {
            effectsBeautyAdapter.notifySelect(position, true);
        }
    }

    private List<BeautyInfo> createBeautyList() {
        List<BeautyInfo> list = new ArrayList<>();
        String[] beautyList = getResources().getStringArray(R.array.beauty_type_list);
        for (int i = 0; i < beautyList.length; ++i) {
            String beautyName = beautyList[i];
            int beautyIconUnselect = BeautyInfo.beautyTypeUnselect[i];
            int beautyIconSelect = BeautyInfo.beautyTypeSelect[i];
            list.add(new BeautyInfo().setBeautyName(beautyName).setBeautyIconUnSelect(beautyIconUnselect).setBeautyIconSelect(beautyIconSelect));
        }
        return list;
    }

    public void setCallBack(IBeautyTypeCallBack callBack) {
        this.callBack = callBack;
    }

    public interface IBeautyTypeCallBack {
        void onClickBeautyType(FaceBeautifyType type, int minProgress, int position);
    }

}
