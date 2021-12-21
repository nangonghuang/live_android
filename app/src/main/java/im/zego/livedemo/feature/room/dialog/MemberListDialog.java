package im.zego.livedemo.feature.room.dialog;

import android.content.Context;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import im.zego.live.model.ZegoUserInfo;
import im.zego.livedemo.R;
import im.zego.livedemo.feature.room.adapter.MemberListAdapter;
import im.zego.livedemo.feature.room.model.MemberInfo;
import im.zego.livedemo.helper.DialogHelper;

public class MemberListDialog extends BaseBottomDialog {
    private List<ZegoUserInfo> userInfoList;

    private RecyclerView recyclerView;
    private TextView tvTitle;

    public MemberListDialog(Context context, List<ZegoUserInfo> userList) {
        super(context);
        this.userInfoList = userList;
    }

    public void updateInfo(List<ZegoUserInfo> userList) {
        this.userInfoList = userList;
        initData();
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
    }

    @Override
    protected void initData() {
        super.initData();
        ArrayList<MemberInfo> arrayList = new ArrayList<>();
        for (ZegoUserInfo user : userInfoList) {
            MemberInfo info = new MemberInfo();
            info.userID = user.getUserID();
            info.userName = user.getUserName();
            arrayList.add(info);
        }

        tvTitle.setText(StringUtils.getString(R.string.room_page_user_list, arrayList.size()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        MemberListAdapter adapter = new MemberListAdapter(arrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.setItemOnClick(userID -> {
            DialogHelper.showToastDialog(getContext(), StringUtils.getString(R.string.room_page_invite_take_seat), dialog -> {
            });
        });
    }
}
