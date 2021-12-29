package im.zego.livedemo.feature.room.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import im.zego.live.ZegoRoomManager;
import im.zego.live.model.ZegoRoomUserRole;
import im.zego.live.model.ZegoUserInfo;
import im.zego.livedemo.R;
import im.zego.livedemo.helper.UserInfoHelper;


public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.UserListHolder> {

    private IItemOnClickListener itemOnClickListener = null;

    private List<ZegoUserInfo> userListInRoom;

    public MemberListAdapter(List<ZegoUserInfo> userListInRoom) {
        this.userListInRoom = new ArrayList<>();
        this.userListInRoom.addAll(userListInRoom);
    }

    @NonNull
    @Override
    public UserListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_user, parent, false);
        return new UserListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListHolder holder, int position) {
        final ZegoUserInfo userInfo = userListInRoom.get(position);

        holder.ivUserAvatar.setImageDrawable(UserInfoHelper.getAvatarByUserName(userInfo.getUserName()));
        holder.tvUserName.setText(userInfo.getUserName());
        holder.ivInvite.setVisibility(View.GONE);
        holder.tvUserInfo.setVisibility(View.GONE);
        holder.tvUserInfo.setTextColor(ColorUtils.getColor(R.color.light_gray));

        switch (getRoleType(userInfo)) {
            case Host:
                holder.tvUserInfo.setVisibility(View.VISIBLE);
                holder.tvUserInfo.setText(R.string.room_page_host);
                break;
            case CoHost:
                holder.tvUserInfo.setVisibility(View.VISIBLE);
                holder.tvUserInfo.setText(R.string.room_page_co_host);
                holder.tvUserInfo.setTextColor(ColorUtils.getColor(R.color.light_gray2));
                break;
            case InvitedCoHost:
                holder.tvUserInfo.setVisibility(View.VISIBLE);
                holder.tvUserInfo.setText(R.string.room_page_invited_co_host);
                holder.tvUserInfo.setTextColor(ColorUtils.getColor(R.color.light_gray2));
                break;
            case Me:
                holder.tvUserInfo.setVisibility(View.VISIBLE);
                holder.tvUserInfo.setText(R.string.room_page_me);
                break;
            case Participant:
                if (UserInfoHelper.isSelfOwner()) {
                    holder.ivInvite.setVisibility(View.VISIBLE);
                }
                break;
        }

        holder.ivInvite.setOnClickListener(v -> {
            if (itemOnClickListener != null) {
                itemOnClickListener.onClick(userInfo);
            }
        });
    }

    public void updateUserList(List<ZegoUserInfo> userList) {
        userListInRoom.clear();
        userListInRoom.addAll(userList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userListInRoom.size();
    }

    public void setItemOnClick(IItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    static class UserListHolder extends RecyclerView.ViewHolder {

        public ImageView ivUserAvatar;
        public TextView tvUserName;
        public TextView tvUserInfo;
        public ImageView ivInvite;

        public UserListHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.iv_user_avatar);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserInfo = itemView.findViewById(R.id.tv_user_info);
            ivInvite = itemView.findViewById(R.id.iv_invite);
        }
    }

    private RoleType getRoleType(ZegoUserInfo userInfo) {
        ZegoUserInfo selfUser = ZegoRoomManager.getInstance().userService.localUserInfo;
        if (Objects.equals(selfUser.getUserID(), userInfo.getUserID())) {
            return RoleType.Me;
        } else if (userInfo.getRole() == ZegoRoomUserRole.Host) {
            return RoleType.Host;
        } else if (userInfo.getRole() == ZegoRoomUserRole.CoHost) {
            return RoleType.CoHost;
        } else if (userInfo.getRole() == ZegoRoomUserRole.Participant && userInfo.isHasInvited()) {
            return RoleType.InvitedCoHost;
        } else {
            return RoleType.Participant;
        }
    }

    enum RoleType {
        Host,
        Participant,
        CoHost,
        InvitedCoHost,
        Me
    }
}
