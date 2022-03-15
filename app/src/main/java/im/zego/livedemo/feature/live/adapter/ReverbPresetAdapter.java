package im.zego.livedemo.feature.live.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.SizeUtils;

import java.util.ArrayList;
import java.util.List;

import im.zego.livedemo.R;
import im.zego.livedemo.feature.live.model.ReverbPresetInfo;

/**
 * Created by rocket_wang on 2022/1/5.
 */
public class ReverbPresetAdapter extends RecyclerView.Adapter<ReverbPresetAdapter.VH> {
    protected int currentSelectItemPosition = 0;

    private IItemOnClickListener<Integer> listener = null;
    private List<ReverbPresetInfo> data = new ArrayList<>();

    public void setList(List<ReverbPresetInfo> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reverb_preset, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ReverbPresetInfo item = data.get(position);
        holder.reverbPresetName.setText(item.getName());

        boolean isCurrentSelect = position == currentSelectItemPosition;
        Bitmap bitmap = ImageUtils.toRound(ImageUtils.getBitmap(item.getIcon()), true);
        if (isCurrentSelect) {
            holder.reverbPresetIcon.setImageBitmap(ImageUtils.addCircleBorder(bitmap, SizeUtils.dp2px(3F), Color.parseColor("#ffa653ff"), true));
            holder.reverbPresetName.setTextColor(Color.parseColor("#ffa653ff"));
        } else {
            holder.reverbPresetIcon.setImageBitmap(bitmap);
            holder.reverbPresetName.setTextColor(Color.parseColor("#ffcccccc"));
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
        TextView reverbPresetName;
        ImageView reverbPresetIcon;

        public VH(@NonNull View itemView) {
            super(itemView);
            reverbPresetName = itemView.findViewById(R.id.tv_rp_name);
            reverbPresetIcon = itemView.findViewById(R.id.iv_rp_icon);
        }
    }
}