package im.zego.livedemo.feature.room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.ImmersionBar;

import im.zego.live.constants.ZegoRoomErrorCode;
import im.zego.livedemo.R;
import im.zego.livedemo.base.BaseActivity;
import im.zego.livedemo.databinding.ActivityLiveRoomBinding;
import im.zego.livedemo.feature.room.adapter.MessageListAdapter;
import im.zego.livedemo.feature.room.dialog.IMInputDialog;
import im.zego.livedemo.feature.room.dialog.MemberListDialog;
import im.zego.livedemo.feature.room.view.CreateLiveView;
import im.zego.livedemo.feature.room.view.LiveBottomView;
import im.zego.livedemo.feature.room.view.LiveHeadView;
import im.zego.livedemo.helper.DialogHelper;
import im.zego.livedemo.helper.UserInfoHelper;

/**
 * Created by rocket_wang on 2021/12/23.
 */
public class LiveRoomActivity extends BaseActivity<ActivityLiveRoomBinding> {
    public static final String EXTRA_KEY_ROOM_ID = "extra_key_room_id";

    /**
     * create new room
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, LiveRoomActivity.class);
        context.startActivity(intent);
    }

    /**
     * enter exist room
     */
    public static void start(Context context, String roomID) {
        Intent intent = new Intent(context, LiveRoomActivity.class);
        intent.putExtra(EXTRA_KEY_ROOM_ID, roomID);
        context.startActivity(intent);
    }

    private LiveRoomViewModel liveRoomViewModel;

    private MessageListAdapter messageListAdapter;

    private IMInputDialog imInputDialog;
    private MemberListDialog memberListDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        liveRoomViewModel = new LiveRoomViewModel();
        initUI();
        initUIListener();
        initData();
    }

    private void initData() {
        messageListAdapter = new MessageListAdapter();
        binding.rvMessageList.setAdapter(messageListAdapter);

        liveRoomViewModel.onlineRoomUsersNum.observe(this, nums -> {
            binding.liveHeadView.updateOnlineNum(String.valueOf(nums));
        });
        liveRoomViewModel.isCameraOpen.observe(this, open -> {
            binding.liveBottomView.enableCameraView(open);
        });
        liveRoomViewModel.isMicOpen.observe(this, open -> {
            binding.liveBottomView.enableMicView(open);
        });
        liveRoomViewModel.textMessageList.observe(this, messages -> {
            messageListAdapter.setMessages(messages);
            binding.rvMessageList.scrollToPosition(messageListAdapter.getItemCount() - 1);
        });
    }

    private void initUI() {
        String roomID = getIntent().getStringExtra(EXTRA_KEY_ROOM_ID);
        if (StringUtils.isTrimEmpty(roomID)) {
            showCreateRoomUI();
            liveRoomViewModel.startPreview(binding.textureView);
        } else {
            showLiveUI();
        }
    }

    private void showCreateRoomUI() {
        binding.createLiveView.setVisibility(View.VISIBLE);
        binding.liveHeadView.setVisibility(View.GONE);
        binding.liveBottomView.setVisibility(View.GONE);
        binding.rvMessageList.setVisibility(View.GONE);
        binding.rvCoHostList.setVisibility(View.GONE);
    }

    private void showLiveUI() {
        binding.createLiveView.setVisibility(View.GONE);
        binding.liveHeadView.setVisibility(View.VISIBLE);
        binding.liveBottomView.setVisibility(View.VISIBLE);
        binding.rvMessageList.setVisibility(View.VISIBLE);
        binding.rvCoHostList.setVisibility(View.VISIBLE);
    }

    private void initUIListener() {
        binding.createLiveView.setListener(new CreateLiveView.CreateViewListener() {
            @Override
            public void onBackClick() {
                onBackPressed();
            }

            @Override
            public void onCameraFlip(boolean isCameraFront) {
                liveRoomViewModel.useFrontCamera(isCameraFront);
            }

            @Override
            public void onBeautyClick() {

            }

            @Override
            public void onSettingsClick() {

            }

            @Override
            public void onStartLiveClick(String roomName) {
                liveRoomViewModel.createRoom(roomName, errorCode -> {
                    if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                        showLiveUI();
                        binding.liveBottomView.toHost();
                        ToastUtils.showShort(StringUtils.getString(R.string.toast_create_room_success));
                    } else {
                        ToastUtils.showShort(StringUtils.getString(R.string.toast_create_room_fail, errorCode));
                    }
                });
            }
        });

        binding.liveHeadView.setListener(new LiveHeadView.HeadViewListener() {
            @Override
            public void onCloseRoomClick() {
                if (UserInfoHelper.isSelfOwner()) {
                    DialogHelper.showAlertDialog(LiveRoomActivity.this,
                            StringUtils.getString(R.string.room_page_destroy_room),
                            StringUtils.getString(R.string.dialog_sure_to_destroy_room),
                            StringUtils.getString(R.string.dialog_confirm),
                            null,
                            (dialog, which) -> {
                                ToastUtils.showShort(R.string.toast_room_has_destroyed);
                                dialog.dismiss();
                                onBackPressed();
                            },
                            null
                    );
                } else {
                    onBackPressed();
                }
            }

            @Override
            public void onOnlineNumClick() {

            }
        });

        binding.liveBottomView.setListener(new LiveBottomView.BottomViewListener() {
            @Override
            public void onImClick() {
                imInputDialog = new IMInputDialog(LiveRoomActivity.this);
                imInputDialog.setOnSendListener(imText -> liveRoomViewModel.sendTextMessage(imText));
                imInputDialog.show();
            }

            @Override
            public void onShareClick() {

            }

            @Override
            public void onBeautyClick() {

            }

            @Override
            public void onMusicClick() {

            }

            @Override
            public void onMoreClick() {

            }

            @Override
            public void onCameraFlip(boolean isCameraFront) {
                liveRoomViewModel.useFrontCamera(isCameraFront);
            }

            @Override
            public void onCameraEnable(boolean isCameraEnable) {
                liveRoomViewModel.enableCamera(isCameraEnable);
            }

            @Override
            public void onMicEnable(boolean isMicEnable) {
                liveRoomViewModel.enableMic(isMicEnable);
            }

            @Override
            public void onApplyConnection() {

            }

            @Override
            public void onCancelApplyConnection() {

            }

            @Override
            public void onEndConnection() {

            }
        });
    }

    @Override
    public void onBackPressed() {
        liveRoomViewModel.destroy();
        super.onBackPressed();
    }
}