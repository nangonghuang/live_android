package im.zego.livedemo.feature.room.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import im.zego.live.ZegoRoomManager;
import im.zego.live.model.ZegoCoHostSeatModel;
import im.zego.live.model.ZegoUserInfo;
import im.zego.livedemo.databinding.ItemCoHostListBinding;
import im.zego.livedemo.helper.UserInfoHelper;

public class CoHostListAdapter extends RecyclerView.Adapter<CoHostListAdapter.ViewHolder> {

    public CoHostListAdapter(ICoHostClickListener listener) {
        this.listener = listener;
    }

    private List<ZegoCoHostSeatModel> seatModels = new ArrayList<>();
    private ICoHostClickListener listener;

    public void setList(List<ZegoCoHostSeatModel> list) {
        seatModels.clear();
        seatModels.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(List<ZegoCoHostSeatModel> list) {
        seatModels.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCoHostListBinding binding = ItemCoHostListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ZegoCoHostSeatModel model = seatModels.get(position);
        ItemCoHostListBinding binding = holder.binding;

        if (model.isMuted()) {
            binding.textureView.setVisibility(View.GONE);
            binding.ivCoHostBg.setVisibility(View.VISIBLE);
            binding.ivCoHostHead.setVisibility(View.VISIBLE);
        } else {
            if (model.isMicEnable()) {
                binding.ivMicOff.setVisibility(View.GONE);
            } else {
                binding.ivMicOff.setVisibility(View.VISIBLE);
            }

            if (model.isCameraEnable()) {
                binding.textureView.setVisibility(View.GONE);
                binding.ivCoHostBg.setVisibility(View.VISIBLE);
                binding.ivCoHostHead.setVisibility(View.VISIBLE);
            }
        }

        ZegoUserInfo userInfo = ZegoRoomManager.getInstance().userService.getUserInfo(model.getUserID());
        if (userInfo == null) return;

        int avatarId = UserInfoHelper.getAvatarIdByUserName(userInfo.getUserName());
        Bitmap bitmap = ImageUtils.getBitmap(avatarId);
        Bitmap blurBitmap = ImageUtils.fastBlur(bitmap, 1F, 15F);
        Bitmap roundBitmap = ImageUtils.toRound(bitmap);

        binding.ivCoHostBg.setImageBitmap(blurBitmap);
        binding.ivCoHostHead.setImageBitmap(roundBitmap);

        binding.tvCoHostName.setText(userInfo.getUserName());

        if (UserInfoHelper.isSelfOwner()) {
            binding.ivMore.setVisibility(View.VISIBLE);
            binding.ivMore.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserMicStatus(model);
                }
            });
        } else {
            binding.ivMore.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return seatModels.size();
    }

    public interface ICoHostClickListener {
        void onUserMicStatus(ZegoCoHostSeatModel seatModel);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemCoHostListBinding binding;

        private ViewHolder(ItemCoHostListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}