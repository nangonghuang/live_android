package im.zego.livedemo.feature.live.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.constraintlayout.widget.ConstraintLayout;

import im.zego.livedemo.R;
import im.zego.livedemo.databinding.LayoutLiveBottomViewBinding;

public class LiveBottomView extends ConstraintLayout {

    public static final int CONNECTION_NOT_APPLY = 1;
    public static final int CONNECTION_APPLYING = 2;
    public static final int CONNECTING = 3;

    private final LayoutLiveBottomViewBinding binding;
    private BottomViewListener listener;

    private int connectionType = CONNECTION_NOT_APPLY;
    private boolean isMicEnable = true;
    private boolean isCameraEnable = true;
    private boolean isCameraFront = true;

    public LiveBottomView(Context context) {
        this(context, null);
    }

    public LiveBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        binding = LayoutLiveBottomViewBinding.inflate(LayoutInflater.from(context), this, true);
        initListener();
    }

    private void initListener() {
        binding.ivIm.setOnClickListener(v -> {
            listener.onImClick();
        });
        binding.ivShare.setOnClickListener(v -> {
            listener.onShareClick();
        });
        binding.ivBeauty.setOnClickListener(v -> {
            listener.onBeautyClick();
        });
        binding.ivMusic.setOnClickListener(v -> {
            listener.onMusicClick();
        });
        binding.ivMore.setOnClickListener(v -> {
            listener.onMoreClick();
        });

        binding.ivFlipCamera.setOnClickListener(v -> {
            isCameraFront = !isCameraFront;
            listener.onCameraFlip(isCameraFront);
        });

        binding.ivCamera.setOnClickListener(v -> {
            boolean enable = !isCameraEnable;
            enableCameraView(enable);
            listener.onCameraEnable(enable);
        });

        binding.ivMic.setOnClickListener(v -> {
            boolean enable = !isMicEnable;
            enableMicView(enable);
            listener.onMicEnable(enable);
        });

        binding.layoutApplyConnection.setOnClickListener(v -> {
            switch (connectionType) {
                case CONNECTION_NOT_APPLY:
                    listener.onApplyConnection();
                    toConnectionType(CONNECTION_APPLYING);
                    break;
                case CONNECTION_APPLYING:
                    listener.onCancelApplyConnection();
                    toConnectionType(CONNECTION_NOT_APPLY);
                    break;
                case CONNECTING:
                    listener.onEndConnection();
                    break;
            }
        });
    }

    public void toHost() {
        binding.ivBeauty.setVisibility(VISIBLE);
        binding.ivMusic.setVisibility(VISIBLE);
        binding.ivMore.setVisibility(VISIBLE);
        binding.ivFlipCamera.setVisibility(GONE);
        binding.ivCamera.setVisibility(GONE);
        binding.ivMic.setVisibility(GONE);
        binding.layoutApplyConnection.setVisibility(GONE);
    }

    public void toCoHost() {
        binding.ivBeauty.setVisibility(GONE);
        binding.ivMusic.setVisibility(GONE);
        binding.ivMore.setVisibility(GONE);
        binding.ivFlipCamera.setVisibility(VISIBLE);
        binding.ivCamera.setVisibility(VISIBLE);
        binding.ivMic.setVisibility(VISIBLE);
        binding.layoutApplyConnection.setVisibility(VISIBLE);
        toConnectionType(CONNECTING);
    }

    public void toParticipant(int type) {
        binding.ivBeauty.setVisibility(GONE);
        binding.ivMusic.setVisibility(GONE);
        binding.ivMore.setVisibility(GONE);
        binding.ivFlipCamera.setVisibility(GONE);
        binding.ivCamera.setVisibility(GONE);
        binding.ivMic.setVisibility(GONE);
        binding.layoutApplyConnection.setVisibility(VISIBLE);
        toConnectionType(type);
    }

    private void toConnectionType(int type) {
        this.connectionType = type;
        switch (type) {
            case CONNECTION_NOT_APPLY:
                binding.ivApplyConnection.setVisibility(VISIBLE);
                binding.tvApplyConnection.setVisibility(VISIBLE);
                binding.tvCancelApplyConnection.setVisibility(GONE);
                binding.layoutApplyConnection.setBackgroundResource(R.drawable.dark_gray_round_rect);
                binding.tvApplyConnection.setText(getContext().getString(R.string.room_page_apply_to_connect));
                break;
            case CONNECTION_APPLYING:
                binding.ivApplyConnection.setVisibility(GONE);
                binding.tvApplyConnection.setVisibility(GONE);
                binding.tvCancelApplyConnection.setVisibility(VISIBLE);
                binding.layoutApplyConnection.setBackgroundResource(R.drawable.dark_gray_round_rect);
                break;
            case CONNECTING:
                binding.ivApplyConnection.setVisibility(VISIBLE);
                binding.tvApplyConnection.setVisibility(VISIBLE);
                binding.tvCancelApplyConnection.setVisibility(GONE);
                binding.layoutApplyConnection.setBackgroundResource(R.drawable.red_gray_round_rect);
                binding.tvApplyConnection.setText(getContext().getString(R.string.room_page_end_connect));
                break;
        }
    }

    public void enableMicView(boolean enable) {
        binding.ivMic.setImageResource(enable ? R.drawable.icon_bottom_mic_on : R.drawable.icon_bottom_mic_off);
        isMicEnable = enable;
    }

    public void enableCameraView(boolean enable) {
        binding.ivCamera.setImageResource(enable ? R.drawable.icon_bottom_camera_on : R.drawable.icon_bottom_camera_off);
        isCameraEnable = enable;
    }

    public void setListener(BottomViewListener listener) {
        this.listener = listener;
    }

    public interface BottomViewListener {
        void onImClick();

        void onShareClick();

        void onBeautyClick();

        void onMusicClick();

        void onMoreClick();

        void onCameraFlip(boolean isCameraFront);

        void onCameraEnable(boolean isCameraEnable);

        void onMicEnable(boolean isMicEnable);

        void onApplyConnection();

        void onCancelApplyConnection();

        void onEndConnection();
    }
}
