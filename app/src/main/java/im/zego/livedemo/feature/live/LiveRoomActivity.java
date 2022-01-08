package im.zego.livedemo.feature.live;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.StringUtils;
import com.gyf.immersionbar.ImmersionBar;
import im.zego.live.ZegoRoomManager;
import im.zego.live.constants.ZegoRoomErrorCode;
import im.zego.live.helper.UserInfoHelper;
import im.zego.live.helper.ZegoLiveHelper;
import im.zego.live.model.OperationActionType;
import im.zego.live.model.ZegoCoHostSeatModel;
import im.zego.live.model.ZegoRoomInfo;
import im.zego.livedemo.R;
import im.zego.livedemo.base.BaseActivity;
import im.zego.livedemo.databinding.ActivityLiveRoomBinding;
import im.zego.livedemo.feature.live.adapter.CoHostListAdapter;
import im.zego.livedemo.feature.live.adapter.MessageListAdapter;
import im.zego.livedemo.feature.live.dialog.EffectsBeautyDialog;
import im.zego.livedemo.feature.live.dialog.IMInputDialog;
import im.zego.livedemo.feature.live.dialog.LoadingDialog;
import im.zego.livedemo.feature.live.dialog.MemberListDialog;
import im.zego.livedemo.feature.live.dialog.MoreSettingDialog;
import im.zego.livedemo.feature.live.dialog.MoreVideoSettingsDialog;
import im.zego.livedemo.feature.live.dialog.SeatMoreDialog;
import im.zego.livedemo.feature.live.dialog.SoundEffectsDialog;
import im.zego.livedemo.feature.live.dialog.VideoSettingsDialog;
import im.zego.livedemo.feature.live.view.CreateLiveView;
import im.zego.livedemo.feature.live.view.LiveBottomView;
import im.zego.livedemo.feature.live.view.LiveHeadView;
import im.zego.livedemo.feature.live.viewmodel.ILiveRoomViewModelListener;
import im.zego.livedemo.feature.live.viewmodel.LiveRoomViewModel;
import im.zego.livedemo.feature.live.viewmodel.VideoConfigViewModel;
import im.zego.livedemo.feature.login.UserLoginActivity;
import im.zego.livedemo.helper.AvatarHelper;
import im.zego.livedemo.helper.DialogHelper;
import im.zego.livedemo.helper.ToastHelper;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import im.zego.zim.enums.ZIMErrorCode;
import java.util.List;

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
    private VideoConfigViewModel videoConfigViewModel;

    private MessageListAdapter messageListAdapter;
    private CoHostListAdapter coHostListAdapter;

    private LoadingDialog loadingDialog;
    private IMInputDialog imInputDialog;
    private MemberListDialog memberListDialog;
    private MoreSettingDialog moreSettingDialog;
    private VideoSettingsDialog videoSettingsDialog;
    private MoreVideoSettingsDialog moreVideoSettingsDialog;
    private SoundEffectsDialog soundEffectsDialog;

    private final ArrayMap<String, Dialog> requestDialogMap = new ArrayMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        videoConfigViewModel = new ViewModelProvider(this).get(VideoConfigViewModel.class);
        videoConfigViewModel.init();

        liveRoomViewModel = new ViewModelProvider(this).get(LiveRoomViewModel.class);
        liveRoomViewModel.init(new ILiveRoomViewModelListener() {
            @Override
            public void onReceiveRoomInfoUpdate(ZegoRoomInfo roomInfo) {
                if (roomInfo == null) {
                    ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_room_has_destroyed));
                    finish();
                }
            }

            @Override
            public void onConnectionStateChanged(ZIMConnectionState state, ZIMConnectionEvent event) {
                if (state == ZIMConnectionState.DISCONNECTED) {
                    dismissDialog(loadingDialog);
                    if (event == ZIMConnectionEvent.LOGIN_TIMEOUT) {
                        showDisconnectDialog();
                    } else {
                        if (event == ZIMConnectionEvent.SUCCESS) {
                            // disconnect because of room end
                            ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_room_has_destroyed));
                            finish();
                        } else if (event == ZIMConnectionEvent.KICKED_OUT) {
                            //disconnect because of multiple login,been kicked out
                            ToastHelper.showNormalToast(R.string.toast_kickout_error);
                            ActivityUtils.finishToActivity(UserLoginActivity.class, false);
                        } else {
                            ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_disconnect_tips));
                            ActivityUtils.finishToActivity(UserLoginActivity.class, false);
                        }

                    }
                } else if (state == ZIMConnectionState.RECONNECTING) {
                    showLoadingDialog();
                } else if (state == ZIMConnectionState.CONNECTED) {
                    dismissDialog(loadingDialog);
                }
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
                                ToastHelper
                                    .showWarnToast(StringUtils.getString(R.string.toast_take_seat_fail, errorCode));
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
                if (!accept) {
                    ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_invite_to_connect_refuse));
                }
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
                if (agree) {
                    liveRoomViewModel.takeCoHostSeat(errorCode -> {
                        if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                            binding.liveBottomView.toCoHost();
                        } else {
                            ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_take_seat_fail, errorCode));
                        }
                    });
                } else {
                    binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_NOT_APPLY);
                    ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_request_connect_refuse));
                }
            }

            @Override
            public void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, List<ZegoStream> streamList) {
                if (!UserInfoHelper.isSelfHost()) {
                    for (ZegoStream zegoStream : streamList) {
                        if (updateType == ZegoUpdateType.ADD) {
                            // if I'm not host then we need play the host stream
                            if (ZegoLiveHelper.isHostStreamID(zegoStream.streamID)) {
                                liveRoomViewModel.startPlayingStream(zegoStream.streamID, binding.textureView);
                            }
                        }
                    }
                }
            }
        });

        initUI();
        initUIListener();
        initData();
    }

    private void toTransparentStatusBar() {
        ImmersionBar.with(this)
            .reset()
            .transparentStatusBar()
            .init();
    }

    private void toDimStatusBar() {
        ImmersionBar.with(this)
            .reset()
            .statusBarColor(R.color.create_live_dark_bg)
            .statusBarDarkFont(false)
            .init();
    }

    private void initData() {
        messageListAdapter = new MessageListAdapter();
        binding.rvMessageList.setAdapter(messageListAdapter);

        coHostListAdapter = new CoHostListAdapter(liveRoomViewModel, seatModel -> {
            SeatMoreDialog dialog = new SeatMoreDialog(LiveRoomActivity.this, seatModel,
                new SeatMoreDialog.IMicManagerListener() {
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
                ToastHelper.showWarnToast(StringUtils.getString(R.string.room_page_invite_to_connect_at_max));
            } else {
                String string = StringUtils.getString(R.string.room_page_invite_to_connect);
                DialogHelper.showToastDialog(this, string, dialog -> {
                    liveRoomViewModel.inviteToBeCoHost(userInfo.getUserID(), errorCode -> {
                        if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                            ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_invite_to_connect_fail));
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
                moreVideoSettingsDialog.show();
            }
        });

        videoSettingsDialog = new VideoSettingsDialog(LiveRoomActivity.this, videoConfigViewModel);
        moreVideoSettingsDialog = new MoreVideoSettingsDialog(LiveRoomActivity.this, videoConfigViewModel);
        soundEffectsDialog = new SoundEffectsDialog(LiveRoomActivity.this);

        startObservingDataChange();
    }

    private void startObservingDataChange() {
        liveRoomViewModel.userList.observe(this, userList -> {
            memberListDialog.updateUserList(userList);
            binding.liveHeadView.updateOnlineNum(String.valueOf(userList.size()));

            ZegoRoomInfo roomInfo = ZegoRoomManager.getInstance().roomService.roomInfo;
            String userName = ZegoRoomManager.getInstance().userService.getUserName(roomInfo.getHostID());
            binding.liveHeadView.updateHostName(userName);

            int avatarId = AvatarHelper.getAvatarIdByUserName(userName);
            Bitmap bitmap = ImageUtils.getBitmap(avatarId);
            Bitmap blurBitmap = ImageUtils.fastBlur(bitmap, 1F, 15F);
            Bitmap roundBitmap = ImageUtils.toRound(bitmap);

            binding.ivHostBg.setImageBitmap(blurBitmap);
            binding.ivHostHead.setImageBitmap(roundBitmap);
            binding.tvHostName.setText(userName);
        });

        liveRoomViewModel.coHostList.observe(this, coHostList -> {
            memberListDialog.updateUserList(liveRoomViewModel.userList.getValue());
            // if host camera/mic status change, we need update main ui
            if (!UserInfoHelper.isSelfHost()) {
                for (ZegoCoHostSeatModel seatModel : coHostList) {
                    if (UserInfoHelper.isUserIDHost(seatModel.getUserID())) {
                        toggleHostPreviewUI(seatModel.isCameraEnable());
                        break;
                    }
                }
            }

            // others coHost, let adapter update
            coHostListAdapter.setList(coHostList);
        });

        liveRoomViewModel.isCameraEnable.observe(this, enable -> {
            binding.liveBottomView.enableCameraView(enable);
            moreSettingDialog.enableCamaraView(enable);
            if (UserInfoHelper.isSelfHost()) {
                toggleHostPreviewUI(enable);
                if (enable) {
                    liveRoomViewModel.startPreview(binding.textureView);
                } else {
                    liveRoomViewModel.stopPreview();
                }
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

        liveRoomViewModel.operationAction.observe(this, action -> {
            if (UserInfoHelper.isUserIDSelf(action.getTargetID())) {
                if (action.getType() == OperationActionType.LeaveCoHostSeat) {
                    binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_NOT_APPLY);
                }
            }
        });
    }

    private void toggleHostPreviewUI(boolean isCameraEnable) {
        binding.textureView.setVisibility(isCameraEnable ? View.VISIBLE : View.GONE);
        binding.layoutHostDisableCamera.setVisibility(isCameraEnable ? View.GONE : View.VISIBLE);
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
                    ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_room_not_exist_fail));
                    finish();
                } else {
                    ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_join_room_fail, errorCode));
                    finish();
                }
            });
        }
    }

    private void showCreateRoomUI() {
        toDimStatusBar();
        binding.createLiveView.setVisibility(View.VISIBLE);
        binding.liveHeadView.setVisibility(View.GONE);
        binding.liveBottomView.setVisibility(View.GONE);
        binding.rvMessageList.setVisibility(View.GONE);
        binding.rvCoHostList.setVisibility(View.GONE);
    }

    private void showLiveUI() {
        toTransparentStatusBar();
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
                EffectsBeautyDialog beautyDialog = new EffectsBeautyDialog(LiveRoomActivity.this);
                beautyDialog.show();
            }

            @Override
            public void onSettingsClick() {
                videoSettingsDialog.show();
            }

            @Override
            public void onStartLiveClick(String roomName) {
                videoConfigViewModel.updateVideoConfig();
                liveRoomViewModel.createRoom(roomName, errorCode -> {
                    if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                        showLiveUI();
                        binding.liveBottomView.toHost();
                        ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_create_room_success));
                    } else {
                        ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_create_room_fail, errorCode));
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
                EffectsBeautyDialog beautyDialog = new EffectsBeautyDialog(LiveRoomActivity.this);
                beautyDialog.show();
            }

            @Override
            public void onMusicClick() {
                soundEffectsDialog.show();
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
                        ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_request_to_connect_fail));
                        binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_NOT_APPLY);
                    }
                });
            }

            @Override
            public void onCancelApplyConnection() {
                liveRoomViewModel.cancelRequestToBeCoHost(errorCode -> {
                    if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                        ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_cancel_request_to_connect_fail));
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
                                ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_end_connect_fail));
                                binding.liveBottomView.toParticipant(LiveBottomView.CONNECTING);
                            } else {
                                binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_NOT_APPLY);
                            }
                        });
                    },
                    (dialog, which) -> dialog.dismiss()
                );
            }
        });
    }

    private void showDisconnectDialog() {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(LiveRoomActivity.this);
        builder2.setTitle(R.string.network_connect_failed_title);
        builder2.setMessage(R.string.network_connect_failed);
        builder2.setCancelable(false);
        builder2.setPositiveButton(R.string.dialog_confirm, (dialog1, which1) -> {
            ActivityUtils.finishToActivity(UserLoginActivity.class, false);
        });
        if (!LiveRoomActivity.this.isFinishing()) {
            AlertDialog alertDialog = builder2.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    private void dismissDialog(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.updateText(R.string.network_reconnect);
        loadingDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog(loadingDialog);
        dismissDialog(imInputDialog);
        dismissDialog(memberListDialog);
        dismissDialog(moreSettingDialog);
        dismissDialog(videoSettingsDialog);
        dismissDialog(moreVideoSettingsDialog);
        dismissDialog(soundEffectsDialog);
        liveRoomViewModel.leaveRoom(errorCode -> {
        });
    }

    @Override
    public void onBackPressed() {
        if (UserInfoHelper.isSelfHost()) {
            DialogHelper.showAlertDialog(LiveRoomActivity.this,
                StringUtils.getString(R.string.room_page_destroy_room),
                StringUtils.getString(R.string.dialog_sure_to_destroy_room),
                StringUtils.getString(R.string.dialog_confirm),
                null,
                true,
                (dialog, which) -> {
                    //                    liveRoomViewModel.leaveRoom(errorCode -> {
                    //                        if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                    //                            ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_room_has_destroyed));
                    //                            dialog.dismiss();
                    //                            super.onBackPressed();
                    //                        } else {
                    //                            ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_room_end_fail_tip, errorCode));
                    //                        }
                    //                    });
                    dialog.dismiss();
                    super.onBackPressed();
                },
                null
            );
        } else {
            //            liveRoomViewModel.leaveRoom(errorCode -> {
            //            });
            super.onBackPressed();
        }
    }
}