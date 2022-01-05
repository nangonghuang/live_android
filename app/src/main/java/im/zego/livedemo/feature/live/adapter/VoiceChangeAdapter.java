package im.zego.livedemo.feature.live.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.model.VoiceChangeInfo;

/**
 * Created by rocket_wang on 2022/1/5.
 */
public class VoiceChangeAdapter extends RecyclerView.Adapter<VoiceChangeAdapter.VH> {
    protected int currentSelectItemPosition = 0;

    private IItemOnClickListener<Integer> listener = null;
    private List<VoiceChangeInfo> data = new ArrayList<>();

    public void setList(List<VoiceChangeInfo> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voice_change, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        VoiceChangeInfo item = data.get(position);
        holder.voiceName.setText(item.getName());

        boolean isCurrentSelect = position == currentSelectItemPosition;
        if (isCurrentSelect) {
            holder.voiceIcon.setImageResource(item.getIconSelect());
            holder.voiceName.setTextColor(Color.parseColor("#ffa653ff"));
        } else {
            holder.voiceIcon.setImageResource(item.getIconUnSelect());
            holder.voiceName.setTextColor(Color.parseColor("#ffcccccc"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (!isCurrentSelect) {
                currentSelectItemPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onClick(currentSelectItemPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setListener(IItemOnClickListener<Integer> listener) {
        this.listener = listener;
    }

    public void updateSelect(int position) {
        currentSelectItemPosition = position;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView voiceName;
        ImageView voiceIcon;

        public VH(@NonNull View itemView) {
            super(itemView);
            voiceName = itemView.findViewById(R.id.tv_voice_change_name);
            voiceIcon = itemView.findViewById(R.id.iv_voice_change);
        }
    }
}