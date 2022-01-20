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

/**
 * Created by rocket_wang on 2022/1/5.
 */
public class BackgroundSoundAdapter extends RecyclerView.Adapter<BackgroundSoundAdapter.VH> {
    protected int currentSelectItemPosition = -1;

    private IItemOnClickListener<Integer> listener = null;
    private List<String> data = new ArrayList<>();

    public void setList(List<String> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_background_sound, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String item = data.get(position);
        holder.soundName.setText(item);

        boolean isCurrentSelect = position == currentSelectItemPosition;
        if (isCurrentSelect) {
            holder.soundImgSelect.setVisibility(View.VISIBLE);
            holder.soundImgUnSelect.setVisibility(View.INVISIBLE);
            holder.soundName.setTextColor(Color.parseColor("#ffa653ff"));
        } else {
            holder.soundImgSelect.setVisibility(View.INVISIBLE);
            holder.soundImgUnSelect.setVisibility(View.VISIBLE);
            holder.soundName.setTextColor(Color.parseColor("#ffffffff"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (isCurrentSelect) {
                currentSelectItemPosition = -1;
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onClick(null);
                }
            } else {
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

    static class VH extends RecyclerView.ViewHolder {
        TextView soundName;
        ImageView soundImgUnSelect;
        ImageView soundImgSelect;

        public VH(@NonNull View itemView) {
            super(itemView);
            soundName = itemView.findViewById(R.id.tv_sound_type_name);
            soundImgUnSelect = itemView.findViewById(R.id.iv_icon_unselect);
            soundImgSelect = itemView.findViewById(R.id.iv_icon_select);
        }
    }
}