package im.zego.livedemo.feature.live;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.StringUtils;

import im.zego.live.ZegoRoomManager;
import im.zego.live.constants.ZegoRoomErrorCode;
import im.zego.live.helper.UserInfoHelper;
import im.zego.live.model.ZegoRoomInfo;
import im.zego.livedemo.R;
import im.zego.livedemo.base.BaseActivity;
import im.zego.livedemo.databinding.ActivityLiveRoomBinding;
import im.zego.livedemo.feature.live.adapter.CoHostListAdapter;
import im.zego.livedemo.feature.live.adapter.MessageListAdapter;
import im.zego.livedemo.feature.live.dialog.IMInputDialog;
import im.zego.livedemo.feature.live.dialog.MemberListDialog;
import im.zego.livedemo.feature.live.dialog.MicManagerDialog;
import im.zego.livedemo.feature.live.dialog.MoreSettingDialog;
import im.zego.livedemo.feature.live.view.CreateLiveView;
import im.zego.livedemo.feature.live.view.LiveBottomView;
import im.zego.livedemo.feature.live.view.LiveHeadView;
import im.zego.livedemo.feature.live.viewmodel.ILiveRoomViewModelListener;
import im.zego.livedemo.feature.live.viewmodel.LiveRoomViewModel;
import im.zego.livedemo.helper.DialogHelper;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import im.zego.zim.enums.ZIMErrorCode;

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
    private CoHostListAdapter coHostListAdapter;

    private IMInputDialog imInputDialog;
    private MemberListDialog memberListDialog;
    private MoreSettingDialog moreSettingDialog;

    private final ArrayMap<String, Dialog> requestDialogMap = new ArrayMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        liveRoomViewModel = new ViewModelProvider(this).get(LiveRoomViewModel.class);
        liveRoomViewModel.init(new ILiveRoomViewModelListener() {
            @Override
            public void onReceiveRoomInfoUpdate(ZegoRoomInfo roomInfo) {
            }

            @Override
            public void onConnectionStateChanged(ZIMConnectionState state, ZIMConnectionEvent event) {

            }

            @Override
            public void onReceiveAddCoHostInvitation() {
                DialogHelper.showAlertDialog(LiveRoomActivity.this,
                    StringUtils.getString(R.string.dialog_invite_to_connect_title),
                    StringUtils.getString(R.string.dialog_invite_to_connect_descrip),
                    StringUtils.getString(R.string.dialog_accept),
                    StringUtils.getString(R.string.dialog_refuse),
                    (dialog, which) -> {
                        dialog.dismiss();
                        liveRoomViewModel.respondCoHostInvitation(true, errorCode -> {
                            if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                                binding.liveBottomView.toCoHost();
                            } else {
                                showErrorToast(StringUtils.getString(R.string.toast_take_seat_fail, errorCode));
                            }
                        });
                    },
                    (dialog, which) -> {
                        dialog.dismiss();
                        liveRoomViewModel.respondCoHostInvitation(false, null);
                    }
                );
            }

            @Override
            public void onReceiveAddCoHostRespond(boolean accept) {

            }

            @Override
            public void onReceiveToCoHostRequest(String requestUserID) {
                String userName = ZegoRoomManager.getInstance().userService.getUserName(requestUserID);
                Dialog alertDialog = DialogHelper.showAlertDialog(LiveRoomActivity.this,
                    StringUtils.getString(R.string.dialog_request_connect_title),
                    StringUtils.getString(R.string.dialog_request_connect_descrip, userName),
                    StringUtils.getString(R.string.dialog_accept),
                    StringUtils.getString(R.string.dialog_refuse),
                    (dialog, which) -> {
                        dialog.dismiss();
                        liveRoomViewModel.respondToBeCoHostRequest(true, requestUserID, null);
                    },
                    (dialog, which) -> {
                        dialog.dismiss();
                        liveRoomViewModel.respondToBeCoHostRequest(false, requestUserID, null);
                    }
                );
                requestDialogMap.put(requestUserID, alertDialog);
            }

            @Override
            public void onReceiveCancelToCoHostRequest(String requestUserID) {
                Dialog dialog = requestDialogMap.remove(requestUserID);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onReceiveToCoHostRespond(boolean agree) {
                liveRoomViewModel.takeCoHostSeat(errorCode -> {
                    if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                        binding.liveBottomView.toCoHost();
                    } else {
                        showErrorToast(StringUtils.getString(R.string.toast_take_seat_fail, errorCode));
                    }
                });
            }
        });

        initUI();
        initUIListener();
        initData();
    }

    private void initData() {
        messageListAdapter = new MessageListAdapter();
        binding.rvMessageList.setAdapter(messageListAdapter);

        coHostListAdapter = new CoHostListAdapter(liveRoomViewModel, seatModel -> {
            MicManagerDialog dialog = new MicManagerDialog(LiveRoomActivity.this, seatModel,
                new MicManagerDialog.IMicManagerListener() {
                    @Override
                    public void onClickMuteBtn(boolean mute) {
                        liveRoomViewModel.muteUser(mute, seatModel.getUserID(), errorCode -> {
                        });
                    }

                    @Override
                    public void onClickProhibitConnect() {
                        liveRoomViewModel.leaveCoHostSeat(seatModel.getUserID(), errorCode -> {
                        });
                    }
                });
            dialog.show();
        });
        binding.rvCoHostList.setAdapter(coHostListAdapter);

        memberListDialog = new MemberListDialog(this, userInfo -> {
            if (liveRoomViewModel.isCoHostMax()) {
                showErrorToast(StringUtils.getString(R.string.room_page_invite_to_connect_at_max));
            } else {
                String string = StringUtils.getString(R.string.room_page_invite_to_connect);
                DialogHelper.showToastDialog(this, string, dialog -> {
                    liveRoomViewModel.inviteToBeCoHost(userInfo.getUserID(), errorCode -> {
                        if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                            showErrorToast(StringUtils.getString(R.string.toast_invite_to_connect_fail));
                        }
                    });
                });
            }
        });

        moreSettingDialog = new MoreSettingDialog(this);
        moreSettingDialog.setListener(new MoreSettingDialog.ISettingMoreListener() {
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
            public void onClickData() {

            }

            @Override
            public void onClickSettings() {

            }
        });

        startObservingDataChange();
    }

    private void startObservingDataChange() {
        liveRoomViewModel.userList.observe(this, userList -> {
            memberListDialog.updateUserList(userList);
            binding.liveHeadView.updateOnlineNum(String.valueOf(userList.size()));

            ZegoRoomInfo roomInfo = ZegoRoomManager.getInstance().roomService.roomInfo;
            String userName = ZegoRoomManager.getInstance().userService.getUserName(roomInfo.getHostID());
            binding.liveHeadView.updateHostName(userName);
        });
        liveRoomViewModel.coHostList.observe(this, userList -> {
            coHostListAdapter.setList(userList);
        });
        liveRoomViewModel.isCameraEnable.observe(this, enable -> {
            binding.liveBottomView.enableCameraView(enable);
            moreSettingDialog.enableCamaraView(enable);
            if (enable) {
                liveRoomViewModel.startPreview(binding.textureView);
            } else {
                liveRoomViewModel.stopPreview();
            }
        });
        liveRoomViewModel.isMicEnable.observe(this, enable -> {
            binding.liveBottomView.enableMicView(enable);
            moreSettingDialog.enableMicView(enable);
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
            liveRoomViewModel.joinRoom(roomID, errorCode -> {
                if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                    binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_NOT_APPLY);
                } else if (errorCode == ZIMErrorCode.ROOM_NOT_EXIST.value()) {
                    showErrorToast(StringUtils.getString(R.string.toast_room_not_exist_fail));
                } else {
                    showErrorToast(StringUtils.getString(R.string.toast_join_room_fail, errorCode));
                }
            });
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
                String roomID = String.valueOf((int) (100 + Math.random() * 900));
                liveRoomViewModel.createRoom(roomID, roomName, errorCode -> {
                    if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                        showLiveUI();
                        binding.liveBottomView.toHost();
                        showTipsToast(StringUtils.getString(R.string.toast_create_room_success));
                    } else {
                        showErrorToast(StringUtils.getString(R.string.toast_create_room_fail, errorCode));
                    }
                });
            }
        });

        binding.liveHeadView.setListener(new LiveHeadView.HeadViewListener() {
            @Override
            public void onCloseRoomClick() {
                onBackPressed();
            }

            @Override
            public void onOnlineNumClick() {
                if (!memberListDialog.isShowing()) {
                    memberListDialog.show();
                }
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
                moreSettingDialog.show();
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
                liveRoomViewModel.requestToBeCoHost(errorCode -> {
                    if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                        showErrorToast(StringUtils.getString(R.string.toast_request_to_connect_fail));
                        binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_NOT_APPLY);
                    }
                });
            }

            @Override
            public void onCancelApplyConnection() {
                liveRoomViewModel.cancelRequestToBeCoHost(errorCode -> {
                    if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                        showErrorToast(StringUtils.getString(R.string.toast_cancel_request_to_connect_fail));
                        binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_APPLYING);
                    }
                });
            }

            @Override
            public void onEndConnection() {
                DialogHelper.showAlertDialog(LiveRoomActivity.this,
                    StringUtils.getString(R.string.dialog_end_connect_title),
                    StringUtils.getString(R.string.dialog_end_connect_descrip),
                    StringUtils.getString(R.string.dialog_confirm),
                    StringUtils.getString(R.string.dialog_cancel),
                    (dialog, which) -> {
                        dialog.dismiss();
                        liveRoomViewModel.leaveCoHostSeat(null, errorCode -> {
                            if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                                showErrorToast(StringUtils.getString(R.string.toast_end_connect_fail));
                                binding.liveBottomView.toParticipant(LiveBottomView.CONNECTING);
                            }
                        });
                    },
                    (dialog, which) -> dialog.dismiss()
                );
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (UserInfoHelper.isSelfOwner()) {
            DialogHelper.showAlertDialog(LiveRoomActivity.this,
                    StringUtils.getString(R.string.room_page_destroy_room),
                    StringUtils.getString(R.string.dialog_sure_to_destroy_room),
                    StringUtils.getString(R.string.dialog_confirm),
                    null,
                    true,
                    (dialog, which) -> {
                        liveRoomViewModel.leaveRoom(errorCode -> {
                            if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                                showTipsToast(StringUtils.getString(R.string.toast_room_has_destroyed));
                                dialog.dismiss();
                                super.onBackPressed();
                            } else {
                                showErrorToast(StringUtils.getString(R.string.toast_room_end_fail_tip, errorCode));
                            }
                        });
                    },
                    null
            );
        } else {
            super.onBackPressed();
        }
    }

    }
}