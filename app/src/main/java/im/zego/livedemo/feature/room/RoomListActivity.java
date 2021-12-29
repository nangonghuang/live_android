package im.zego.livedemo.feature.room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.SizeUtils;

import im.zego.live.ZegoRoomManager;
import im.zego.livedemo.R;
import im.zego.livedemo.base.BaseActivity;
import im.zego.livedemo.databinding.ActivityRoomListBinding;
import im.zego.livedemo.feature.live.LiveRoomActivity;
import im.zego.livedemo.feature.room.adapter.RoomListAdapter;
import im.zego.livedemo.feature.settings.SettingsActivity;
import im.zego.livedemo.helper.PermissionHelper;
import im.zego.livedemo.view.GridLayoutItemDecoration;

public class RoomListActivity extends BaseActivity<ActivityRoomListBinding> {

    public static void start(Context context) {
        Intent intent = new Intent(context, RoomListActivity.class);
        context.startActivity(intent);
    }

    private RoomListAdapter roomListAdapter;

    @SuppressLint("ResourceType")
    @Override
    protected int getStatusBarColor() {
        return R.color.login_dark_bg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initListener();
    }

    private void initData() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.recyclerView.setLayoutManager(layoutManager);

        roomListAdapter = new RoomListAdapter();
        binding.recyclerView.addItemDecoration(new GridLayoutItemDecoration(this,
                SizeUtils.dp2px(16), SizeUtils.dp2px(18), true, false));
        binding.recyclerView.setAdapter(roomListAdapter);

        roomListAdapter.setOnClickListener((v, position, item) -> {

        });

        binding.recyclerView.setVisibility(View.GONE);
        binding.emptyView.setVisibility(View.GONE);
    }

    private void initListener() {
        binding.commonTitleView.setBackBtnClickListener(v -> onBackPressed());
        binding.commonTitleView.setSettingsBtnClickListener(v -> SettingsActivity.start(this));
        binding.flCreateLive.setOnClickListener(v -> {
            PermissionHelper.requestCameraAndAudio(this, isAllGranted -> {
                if (isAllGranted) {
                    LiveRoomActivity.start(this);
                }
            });
        });
        binding.smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
//                refreshLayout.finishRefresh();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoRoomManager.getInstance().userService.logout();
    }
}