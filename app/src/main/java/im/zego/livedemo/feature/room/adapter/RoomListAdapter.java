package im.zego.livedemo.feature.room.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import im.zego.livedemo.R;
import im.zego.livedemo.constants.Constants;
import im.zego.livedemo.feature.room.model.RoomBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rocket_wang on 2021/12/22.
 */
public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomListViewHolder> {

    private static final String TAG = "RoomListAdapter";
    private List<RoomBean> items = new ArrayList<>();
    private OnClickListener listener;

    public void setList(List<RoomBean> list) {
        Log.d(TAG, "setList() called with: list = [" + list.size() + "]");
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(List<RoomBean> list) {
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
        RoomBean item = items.get(position);

        int index = (int) ((item.getCreateTime() - 1) % 5);
        Log.d(TAG, "onBindViewHolder,position: " +  index);
        holder.rootView.setBackgroundResource(Constants.coverList[index]);
        holder.roomUserNum.setText(String.valueOf(item.getUserNum()));
        holder.roomTitle.setText(item.getName());

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

        void onClick(View v, int position, RoomBean item);
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