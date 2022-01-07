package im.zego.livedemo.feature.live.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SizeUtils;

import java.util.ArrayList;
import java.util.List;

import im.zego.live.ZegoRoomManager;
import im.zego.live.helper.UserInfoHelper;
import im.zego.live.model.ZegoTextMessage;
import im.zego.livedemo.R;
import im.zego.livedemo.helper.RoundedBackgroundSpan;


/**
 * Show all the information sent
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageHolder> {

    private List<ZegoTextMessage> messageList = new ArrayList<>();

    public void setMessages(List<ZegoTextMessage> messages) {
        messageList.clear();
        messageList.addAll(messages);
        notifyDataSetChanged();
    }

    public void updateMessages(List<ZegoTextMessage> messages) {
        messageList.addAll(messages);
        notifyDataSetChanged();
    }

    public void insertMessage(ZegoTextMessage message) {
        messageList.add(message);
        notifyItemInserted(messageList.size());
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_message, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        ZegoTextMessage message = messageList.get(position);
        boolean isHostMessage = UserInfoHelper.isUserIDHost(message.userID);
        String fromUserName = ZegoRoomManager.getInstance().userService.getUserName(message.userID);
        String content = message.message;
        Context context = holder.itemView.getContext();

        StringBuilder builder = new StringBuilder();
        if (isHostMessage) {
            builder.append(context.getString(R.string.room_page_host));
            builder.append(" ");
        }
        builder.append(fromUserName);
        builder.append(" ");
        builder.append(content);
        String source = builder.toString();
        SpannableString string = new SpannableString(source);
        RoundedBackgroundSpan backgroundColorSpan = new RoundedBackgroundSpan(
                ContextCompat.getColor(context, R.color.purple_dark),
                ContextCompat.getColor(context, R.color.white),
                SizeUtils.sp2px(10));
        if (isHostMessage) {
            AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(SizeUtils.sp2px(10));
            string.setSpan(absoluteSizeSpan, 0,
                context.getString(R.string.room_page_host).length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            string.setSpan(backgroundColorSpan, 0,
                context.getString(R.string.room_page_host).length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(
            ContextCompat.getColor(context, R.color.teal)
        );
        int indexOfUser = source.indexOf(fromUserName);
        string.setSpan(foregroundColorSpan, indexOfUser, indexOfUser + fromUserName.length(),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(SizeUtils.sp2px(13));
        string.setSpan(absoluteSizeSpan, indexOfUser, indexOfUser + fromUserName.length(),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        holder.tvSendMessage.setText(string);

//        if (!message.isRoomUserInfoMessage) {
//        } else {
//            SpannableString string = new SpannableString(content);
//            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(
//                ContextCompat.getColor(context, R.color.teal)
//            );
//            AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(SizeUtils.sp2px(13));
//            string.setSpan(foregroundColorSpan, 0, content.length(),
//                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//            string
//                .setSpan(absoluteSizeSpan, 0, content.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//            holder.tvSendMessage.setText(string);
//        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageHolder extends RecyclerView.ViewHolder {

        public TextView tvSendMessage;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            tvSendMessage = itemView.findViewById(R.id.tv_send_message);
        }
    }
}
