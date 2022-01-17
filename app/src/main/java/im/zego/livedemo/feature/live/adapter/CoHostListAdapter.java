package im.zego.livedemo.feature.live.adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ImageUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import im.zego.live.ZegoRoomManager;
import im.zego.live.helper.UserInfoHelper;
import im.zego.live.helper.ZegoLiveHelper;
import im.zego.live.model.ZegoCoHostSeatModel;
import im.zego.live.model.ZegoUserInfo;
import im.zego.livedemo.databinding.ItemCoHostListBinding;
import im.zego.livedemo.feature.live.viewmodel.LiveRoomViewModel;
import im.zego.livedemo.helper.AvatarHelper;

public class CoHostListAdapter extends RecyclerView.Adapter<CoHostListAdapter.ViewHolder> {
    private static final String TAG = "CoHostListAdapter";

    private List<ZegoCoHostSeatModel> seatModels = new ArrayList<>();
    private ICoHostClickListener listener;
    private LiveRoomViewModel liveRoomViewModel;

    public CoHostListAdapter(LiveRoomViewModel liveRoomViewModel, ICoHostClickListener listener) {
        this.liveRoomViewModel = liveRoomViewModel;
        this.listener = listener;
    }

    public void setList(List<ZegoCoHostSeatModel> list) {
        Log.d(TAG, "setList" + list);
        if (list == null) return;
        seatModels.clear();
        seatModels.addAll(list);
        removeHostSeat(seatModels);
        if (needSort()) {
            sortList(seatModels);
        }
        notifyDataSetChanged();
    }

    private boolean needSort() {
        return !UserInfoHelper.isSelfHost() && UserInfoHelper.isSelfCoHost();
    }

    private void removeHostSeat(List<ZegoCoHostSeatModel> seatModels) {
        Iterator<ZegoCoHostSeatModel> iterator = seatModels.iterator();
        while (iterator.hasNext()) {
            ZegoCoHostSeatModel seatModel = iterator.next();
            if (UserInfoHelper.isUserIDHost(seatModel.getUserID())) {
                iterator.remove();
                break;
            }
        }
    }

    private void sortList(List<ZegoCoHostSeatModel> seatModels) {
        if (seatModels.size() <= 1) return;
        int index = 0;
        ZegoCoHostSeatModel selfModel = null;
        for (int i = 0; i < seatModels.size(); i++) {
            selfModel = seatModels.get(i);
            if (UserInfoHelper.isUserIDSelf(selfModel.getUserID())) {
                index = i;
                break;
            }
        }
        seatModels.remove(index);
        seatModels.add(0, selfModel);
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
            binding.ivMicOff.setVisibility(View.VISIBLE);
        } else {
            if (model.isMicEnable()) {
                binding.ivMicOff.setVisibility(View.GONE);
            } else {
                binding.ivMicOff.setVisibility(View.VISIBLE);
            }
        }

        if (model.isCameraEnable()) {
            binding.textureView.setVisibility(View.VISIBLE);
            binding.ivCoHostBg.setVisibility(View.GONE);
            binding.ivCoHostHead.setVisibility(View.GONE);
        } else {
            binding.textureView.setVisibility(View.GONE);
            binding.ivCoHostBg.setVisibility(View.VISIBLE);
            binding.ivCoHostHead.setVisibility(View.VISIBLE);
        }

        ZegoUserInfo userInfo = ZegoRoomManager.getInstance().userService.getUserInfo(model.getUserID());
        Log.d(TAG, "userInfo" + userInfo);
        if (userInfo == null) return;

        int avatarId = AvatarHelper.getAvatarIdByUserName(userInfo.getUserName());
        Bitmap bitmap = ImageUtils.getBitmap(avatarId);
        Bitmap blurBitmap = ImageUtils.fastBlur(bitmap, 1F, 15F);
        Bitmap roundBitmap = ImageUtils.toRound(bitmap);

        binding.ivCoHostBg.setImageBitmap(blurBitmap);
        binding.ivCoHostHead.setImageBitmap(roundBitmap);

        binding.tvCoHostName.setText(userInfo.getUserName());

        if (UserInfoHelper.isSelfHost()) {
            binding.ivMore.setVisibility(View.VISIBLE);
            binding.ivMore.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClickMore(model);
                }
            });
        } else {
            binding.ivMore.setVisibility(View.GONE);
        }

        if (UserInfoHelper.isUserIDSelf(model.getUserID())) {
            liveRoomViewModel.startPreview(binding.textureView);
        } else {
            liveRoomViewModel.startPlayingStream(ZegoLiveHelper.getStreamID(model.getUserID()), binding.textureView);
        }
//        ZegoExpressEngine.getEngine().enableCamera(model.isCameraEnable());
    }

    @Override
    public int getItemCount() {
        return seatModels.size();
    }

    public interface ICoHostClickListener {
        void onClickMore(ZegoCoHostSeatModel seatModel);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemCoHostListBinding binding;

        private ViewHolder(ItemCoHostListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}