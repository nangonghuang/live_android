package im.zego.livedemo.feature.room.dialog;

import android.content.Context;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.StringUtils;

import java.util.List;

import im.zego.live.model.ZegoUserInfo;
import im.zego.livedemo.R;
import im.zego.livedemo.feature.room.adapter.MemberListAdapter;
import im.zego.livedemo.helper.DialogHelper;


public class MemberListDialog extends BaseBottomDialog {

    private List<ZegoUserInfo> userList;

    private RecyclerView recyclerView;
    private TextView tvTitle;
    private MemberListAdapter memberListAdapter;

    public MemberListDialog(Context context, List<ZegoUserInfo> userList) {
        super(context);
        this.userList = userList;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_member;
    }

    @Override
    protected void initView() {
        super.initView();
        tvTitle = findViewById(R.id.tv_title);
        recyclerView = findViewById(R.id.rv_user_list);

        tvTitle.setText(StringUtils.getString(R.string.room_page_user_list, userList.size()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        memberListAdapter = new MemberListAdapter(userList);
        recyclerView.setAdapter(memberListAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        memberListAdapter.setItemOnClick(userInfo -> {
            String string = StringUtils.getString(R.string.room_page_invite_take_seat);
            DialogHelper.showToastDialog(getContext(), string, dialog -> {
            });
        });
    }

    public void updateUserList(List<ZegoUserInfo> userList) {
        this.userList = userList;
        memberListAdapter.updateUserList(userList);
        tvTitle.setText(StringUtils.getString(R.string.room_page_user_list, userList.size()));
    }
}
