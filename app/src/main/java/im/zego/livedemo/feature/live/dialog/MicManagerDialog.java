package im.zego.livedemo.feature.live.dialog;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;

import im.zego.live.model.ZegoCoHostSeatModel;
import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.dialog.base.BaseBottomDialog;

public class MicManagerDialog extends BaseBottomDialog {

    private TextView tvMicStatus;
    private TextView tvProhibitConnect;
    private TextView tvCancel;

    private ZegoCoHostSeatModel seatModel;
    private IMicManagerListener listener;

    public MicManagerDialog(@NonNull Context context, ZegoCoHostSeatModel seatModel, IMicManagerListener listener) {
        super(context);
        this.seatModel = seatModel;
        this.listener = listener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_mic_manager;
    }

    @Override
    public void initView() {
        tvMicStatus = findViewById(R.id.tv_mic_status);
        tvProhibitConnect = findViewById(R.id.tv_prohibit_connect);
        tvCancel = findViewById(R.id.tv_cancel);

        if (seatModel.isMuted()) {
            tvMicStatus.setText(StringUtils.getString(R.string.room_page_co_host_unmute));
        } else {
            tvMicStatus.setText(StringUtils.getString(R.string.room_page_co_host_mute));
        }

        tvCancel.setOnClickListener(v -> dismiss());

        tvProhibitConnect.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.onClickMuteBtn(!seatModel.isMuted());
            }
        });

        tvMicStatus.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.onClickProhibitConnect();
            }
        });
    }

    public interface IMicManagerListener {
        void onClickMuteBtn(boolean mute);

        void onClickProhibitConnect();
    }
}
