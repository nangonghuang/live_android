package im.zego.livedemo.feature.room.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ImageUtils;

import im.zego.live.model.ZegoUserInfo;
import im.zego.livedemo.databinding.LayoutLiveHeadViewBinding;

public class LiveHeadView extends ConstraintLayout {

    private final LayoutLiveHeadViewBinding binding;
    private HeadViewListener listener;

    public LiveHeadView(@NonNull Context context) {
        this(context, null);
    }

    public LiveHeadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveHeadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = LayoutLiveHeadViewBinding.inflate(LayoutInflater.from(context), this, true);
        initListener();
    }

    private void initListener() {
        binding.ivCloseRoom.setOnClickListener(v -> listener.onCloseRoomClick());
        binding.layoutOnlineNum.setOnClickListener(v -> listener.onOnlineNumClick());
    }

    public void updateUserInfo(ZegoUserInfo userInfo) {
        binding.ivHostAvatar.setImageBitmap(ImageUtils.toRound(ImageUtils.getBitmap(userInfo.getAvatar()), true));
        binding.tvHostName.setText(userInfo.getUserName());
    }

    public void updateOnlineNum(String num) {
        binding.tvOnlineNum.setText(num);
    }

    public void setListener(HeadViewListener listener) {
        this.listener = listener;
    }

    public interface HeadViewListener {
        void onCloseRoomClick();

        void onOnlineNumClick();
    }
}
