package im.zego.livedemo.feature.room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import com.blankj.utilcode.util.SizeUtils;
import com.scwang.smart.refresh.header.MaterialHeader;
import im.zego.live.ZegoRoomManager;
import im.zego.live.http.IAsyncGetCallback;
import im.zego.livedemo.R;
import im.zego.livedemo.base.BaseActivity;
import im.zego.livedemo.databinding.ActivityRoomListBinding;
import im.zego.livedemo.feature.live.LiveRoomActivity;
import im.zego.livedemo.feature.room.adapter.RoomListAdapter;
import im.zego.livedemo.feature.room.model.RoomList;
import im.zego.livedemo.feature.settings.SettingsActivity;
import im.zego.livedemo.helper.PermissionHelper;
import im.zego.livedemo.view.GridLayoutItemDecoration;

public class RoomListActivity extends BaseActivity<ActivityRoomListBinding> {

    public static void start(Context context) {
        Intent intent = new Intent(context, RoomListActivity.class);
        context.startActivity(intent);
    }

    private RoomListAdapter roomListAdapter;
    private static final String TAG = "RoomList";

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

    @Override
    protected void onStart() {
        super.onStart();
        RoomApi.getRoomList(100, null, new IAsyncGetCallback<RoomList>() {
            @Override
            public void onResponse(int errorCode, @NonNull String message, RoomList responseJsonBean) {
                if (errorCode == 0) {
                    if (responseJsonBean.roomList.size() > 0) {
                        roomListAdapter.setList(responseJsonBean.roomList);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        binding.emptyView.setVisibility(View.GONE);
                    } else {
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.emptyView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void initData() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.recyclerView.setLayoutManager(layoutManager);

        roomListAdapter = new RoomListAdapter();
        binding.recyclerView.addItemDecoration(new GridLayoutItemDecoration(SizeUtils.dp2px(13)));
        binding.recyclerView.setAdapter(roomListAdapter);
        binding.smartRefreshLayout.setRefreshHeader(new MaterialHeader(this));

        roomListAdapter.setOnClickListener((v, position, item) -> {
            Log.d(TAG, "initData: " + item.getRoomID());
            PermissionHelper.requestCameraAndAudio(this, isAllGranted -> {
                if (isAllGranted) {
                    LiveRoomActivity.start(RoomListActivity.this, item.getRoomID());
                }
            });
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
            RoomApi.getRoomList(100, null, new IAsyncGetCallback<RoomList>() {
                @Override
                public void onResponse(int errorCode, @NonNull String message, RoomList responseJsonBean) {
                    refreshLayout.finishRefresh();
                    if (errorCode == 0) {
                        if (responseJsonBean.roomList.size() > 0) {
                            roomListAdapter.setList(responseJsonBean.roomList);
                            binding.recyclerView.setVisibility(View.VISIBLE);
                            binding.emptyView.setVisibility(View.GONE);
                        } else {
                            binding.recyclerView.setVisibility(View.GONE);
                            binding.emptyView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoRoomManager.getInstance().userService.logout();
    }
}