package im.zego.livedemo.feature.room.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import im.zego.livedemo.R;
import im.zego.livedemo.constants.Constants;
import im.zego.livedemo.feature.room.model.RoomListItem;

/**
 * Created by rocket_wang on 2021/12/22.
 */
public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomListViewHolder> {

    private List<RoomListItem> items = new ArrayList<>();
    private OnClickListener listener;

    public void setList(List<RoomListItem> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(List<RoomListItem> list) {
        items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_item_room_list, parent, false);
        return new RoomListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomListViewHolder holder, int position) {
        RoomListItem item = items.get(position);

        holder.rootView.setBackgroundResource(Constants.coverList[Integer.parseInt(item.getCoverImg()) - 1]);
        holder.roomUserNum.setText(String.valueOf(item.getNum()));
        holder.roomTitle.setText(item.getTitle());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v, position, item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        listener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(View v, int position, RoomListItem item);
    }

    static class RoomListViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView roomUserNum;
        TextView roomTitle;

        private RoomListViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.root_view);
            roomUserNum = itemView.findViewById(R.id.tv_room_user_num);
            roomTitle = itemView.findViewById(R.id.tv_room_title);
        }
    }
}