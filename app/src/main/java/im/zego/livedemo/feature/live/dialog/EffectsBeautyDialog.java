package im.zego.livedemo.feature.live.dialog;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import im.zego.live.ZegoRoomManager;
import im.zego.live.model.FaceBeautifyType;
import im.zego.live.service.ZegoFaceBeautifyService;
import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.dialog.base.BaseBottomDialog;
import im.zego.livedemo.feature.live.view.EffectsBeautyTypeView;
import im.zego.livedemo.feature.live.view.EffectsBeautyTypeView.IBeautyTypeCallBack;
import im.zego.livedemo.feature.live.view.EffectsBeautyView;
import im.zego.livedemo.feature.live.view.EffectsBeautyView.IBeautyCallBack;
import im.zego.livedemo.feature.live.view.SeekBarWithNumber;

public class EffectsBeautyDialog extends BaseBottomDialog implements View.OnClickListener {

    private TextView mTvBeauty;
    private TextView mTvBeautyType;
    private ImageView mResetting;
    private EffectsBeautyView mEffectsBeautyView;
    private EffectsBeautyTypeView mEffectsBeautyTypeView;
    private SeekBarWithNumber seekBarWithNumber;

    private FaceBeautifyType selectType;
    private int selectIndex = 0;
    private int beautyPosition = -1;
    private int beautyTypePosition = -1;

    private int min = 0;

    public EffectsBeautyDialog(@NonNull Context context) {
        super(context, R.style.BeautyDialog);
    }

    @Override
    public void initView() {
        mTvBeauty = findViewById(R.id.tv_beauty);
        mTvBeautyType = findViewById(R.id.tv_beauty_type);
        mResetting = findViewById(R.id.resetting);
        mEffectsBeautyView = findViewById(R.id.effectsBeautyView);
        mEffectsBeautyTypeView = findViewById(R.id.effectsBeautyTypeView);
        seekBarWithNumber = findViewById(R.id.seekBarWithNumber);

        mTvBeauty.setOnClickListener(this);
        mTvBeautyType.setOnClickListener(this);
        mResetting.setOnClickListener(this);

        selectView(mTvBeauty);
        unSelectView(mTvBeautyType);
        mEffectsBeautyView.setVisibility(View.VISIBLE);
        mEffectsBeautyTypeView.setVisibility(View.GONE);

        mEffectsBeautyView.setCallBack(new IBeautyCallBack() {
            @Override
            public void onClickBeauty(FaceBeautifyType type, int minProgress, int position) {
                selectType = type;
                beautyPosition = position;
                setSeekBarProgress(type, 0, minProgress);
            }
        });
        mEffectsBeautyTypeView.setCallBack(new IBeautyTypeCallBack() {
            @Override
            public void onClickBeautyType(FaceBeautifyType type, int minProgress, int position) {
                selectType = type;
                setSeekBarProgress(type, 1, minProgress);
                beautyTypePosition = position;
            }
        });

        setSeekBar();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.liveshow_dialog_effect;
    }

    private void selectView(TextView view) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        view.setTextColor(getContext().getResources().getColor(R.color.white_color));
    }

    private void unSelectView(TextView view) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        view.setTextColor(getContext().getResources().getColor(R.color.white_color_30alpha));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_beauty) {
            selectIndex = 0;
            selectView(mTvBeauty);
            unSelectView(mTvBeautyType);
            mEffectsBeautyTypeView.notifyData();
            mEffectsBeautyView.setVisibility(View.VISIBLE);
            mEffectsBeautyTypeView.setVisibility(View.GONE);
            seekBarWithNumber.setVisibility(View.INVISIBLE);
            mEffectsBeautyView.setAdaptet();

        } else if (id == R.id.tv_beauty_type) {
            selectIndex = 1;
            selectView(mTvBeautyType);
            unSelectView(mTvBeauty);
            mEffectsBeautyView.notifyData();
            mEffectsBeautyView.setVisibility(View.GONE);
            mEffectsBeautyTypeView.setVisibility(View.VISIBLE);
            seekBarWithNumber.setVisibility(View.INVISIBLE);
            mEffectsBeautyTypeView.setAdapter();

        } else if (id == R.id.resetting) {
            seekBarWithNumber.setVisibility(View.INVISIBLE);
            ZegoFaceBeautifyService beautifyService = ZegoRoomManager.getInstance().faceBeautifyService;
            if (selectIndex == 0) {
                mEffectsBeautyView.notifyPosition(beautyPosition);
                beautyPosition = -1;
                beautifyService.resetBeauty();
            } else {
                mEffectsBeautyTypeView.notifyPosition(beautyTypePosition);
                beautyTypePosition = -1;
                beautifyService.resetReSharp();
            }
        }
    }

    private static final String TAG = "Beautify";

    private void setSeekBarProgress(FaceBeautifyType type, int index, int minProgress) {
        ZegoFaceBeautifyService beautifyService = ZegoRoomManager.getInstance().faceBeautifyService;
        int progress = beautifyService.getBeautifyValue(type);

        if (seekBarWithNumber.getVisibility() != View.VISIBLE) {
            seekBarWithNumber.setVisibility(View.VISIBLE);
        }

        min = minProgress;
        seekBarWithNumber.setOffsetValue(minProgress);
        seekBarWithNumber.setMax(100 - minProgress);
        seekBarWithNumber.setProgress(progress);
    }

    private void setSeekBar() {
        seekBarWithNumber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int currentProgress = (progress + min);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int currentProgress = seekBar.getProgress();
                ZegoFaceBeautifyService beautifyService = ZegoRoomManager.getInstance().faceBeautifyService;
                if (selectType != null) {
                    beautifyService.setBeautifyValue(currentProgress, selectType);
                }
            }
        });
    }
}
