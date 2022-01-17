package im.zego.livedemo.feature.live;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.ImmersionBar;

import java.util.List;
import java.util.Objects;

import im.zego.live.ZegoRoomManager;
import im.zego.live.constants.ZegoRoomErrorCode;
import im.zego.live.helper.UserInfoHelper;
import im.zego.live.helper.ZegoLiveHelper;
import im.zego.live.model.ZegoCoHostSeatModel;
import im.zego.live.model.ZegoRoomInfo;
import im.zego.livedemo.R;
import im.zego.livedemo.base.BaseActivity;
import im.zego.livedemo.constants.Constants;
import im.zego.livedemo.databinding.ActivityLiveRoomBinding;
import im.zego.livedemo.feature.live.adapter.CoHostListAdapter;
import im.zego.livedemo.feature.live.adapter.MessageListAdapter;
import im.zego.livedemo.feature.live.dialog.CommonDialog;
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
import im.zego.livedemo.helper.PermissionHelper;
import im.zego.livedemo.helper.ShareHelper;
import im.zego.livedemo.helper.ToastHelper;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;

/**
 * Created by rocket_wang on 2021/12/23.
 */
public class LiveRoomActivity extends BaseActivity<ActivityLiveRoomBinding> {

    public static final String EXTRA_KEY_ROOM_ID = "extra_key_room_id";

    /**
     * create new room
     */
    public static void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, LiveRoomActivity.class);
        activity.startActivityForResult(intent, requestCode);
//        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * enter exist room
     */
    public static void start(Activity activity, String roomID, int requestCode) {
        Intent intent = new Intent(activity, LiveRoomActivity.class);
        intent.putExtra(EXTRA_KEY_ROOM_ID, roomID);
        activity.startActivityForResult(intent, requestCode);
//        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    private final Runnable cancelApplyConnectionRunnable = () -> {
        dismissAllToast();
        binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_NOT_APPLY);
        liveRoomViewModel.cancelRequestToBeCoHost(errorCode -> {
        });
    };

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
                if (roomInfo == null && !isFinishing()) {
                    try {
                        new CommonDialog.Builder(LiveRoomActivity.this)
                                .setTitle(StringUtils.getString(R.string.dialog_attetion_title))
                                .setContent(StringUtils.getString(R.string.toast_room_has_destroyed))
                                .setPositiveButton(StringUtils.getString(R.string.dialog_close), (dialog, which) -> {
                                    finish();
                                })
                                .create()
                                .show();
                    } catch (Exception ignore) {
                    }
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
//                            ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_room_has_destroyed));
//                            finish();
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
            public void onReceiveAddCoHostInvitation(String operateUserID) {
                DialogHelper.showAlertDialog(LiveRoomActivity.this,
                    StringUtils.getString(R.string.dialog_invition_title),
                    StringUtils.getString(R.string.dialog_invition_descrip),
                    StringUtils.getString(R.string.dialog_room_page_agree),
                    StringUtils.getString(R.string.dialog_room_page_disagree),
                    (dialog, which) -> {
                        dialog.dismiss();
                        if (liveRoomViewModel.isCoHostMax()) {
                            ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_room_maximum));
                        } else {
                            liveRoomViewModel.respondCoHostInvitation(true, operateUserID, errorCode -> {
                                if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                                    userTakeSeat(isAllGranted -> {
                                        if (!isAllGranted) {
                                            ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_room_page_permission_error));
                                            liveRoomViewModel.respondCoHostInvitation(false, operateUserID, null);
                                        }
                                    });
                                } else {
                                    ToastHelper
                                            .showWarnToast(StringUtils.getString(R.string.toast_user_list_page_connected_failed));
                                }
                            });
                        }
                    },
                    (dialog, which) -> {
                        dialog.dismiss();
                        liveRoomViewModel.respondCoHostInvitation(false, operateUserID, null);
                    }
                );
            }

            @Override
            public void onReceiveAddCoHostRespond(String userID, boolean accept) {
                if (!accept) {
                    String userName = UserInfoHelper.getUserName(userID);
                    ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_user_list_page_rejected_invitation, userName));
                }
            }

            @Override
            public void onReceiveToCoHostRequest(String requestUserID) {
                String userName = UserInfoHelper.getUserName(requestUserID);
                Dialog alertDialog = DialogHelper.showAlertDialog(LiveRoomActivity.this,
                    StringUtils.getString(R.string.dialog_room_page_title_connection_request),
                    StringUtils.getString(R.string.dialog_room_page_message_connection_request, UserInfoHelper.getUserNameShort(userName)),
                    StringUtils.getString(R.string.dialog_room_page_agree),
                    StringUtils.getString(R.string.dialog_room_page_disagree),
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
                String userName = UserInfoHelper.getUserName(requestUserID);
                ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_room_has_canceled_connection_apply, userName));
                Dialog dialog = requestDialogMap.remove(requestUserID);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onReceiveToCoHostRespond(boolean agree) {
                ThreadUtils.getMainHandler().removeCallbacks(cancelApplyConnectionRunnable);
                dismissAllToast();
                if (agree) {
                    if (liveRoomViewModel.isCoHostMax()) {
                        ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_room_maximum));
                        binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_NOT_APPLY);
                    } else {
                        userTakeSeat(isAllGranted -> {
                            if (!isAllGranted) {
                                binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_NOT_APPLY);
                                ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_room_page_permission_error));
                            }
                        });
                    }
                } else {
                    binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_NOT_APPLY);
                    ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_room_has_rejected));
                }
            }

            @Override
            public void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, List<ZegoStream> streamList) {
                if (!UserInfoHelper.isSelfHost()) {
                    for (ZegoStream zegoStream : streamList) {
                        if (updateType == ZegoUpdateType.ADD) {
                            // if I'm not host then we need play the host stream
                            String streamID = zegoStream.streamID;
                            Log.d("ADD", "onRoomStreamUpdate: " + streamID);
                            if (ZegoLiveHelper.isHostStreamID(streamID)) {
                                liveRoomViewModel.startPlayingStream(streamID, binding.textureView);
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

    private void userTakeSeat(PermissionHelper.IPermissionCallback permissionCallback) {
        PermissionHelper.requestCameraAndAudio(LiveRoomActivity.this, isAllGranted -> {
            if (isAllGranted) {
                liveRoomViewModel.takeSeat(errorCode -> {
                    if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                        binding.liveBottomView.toCoHost();
                    } else {
                        ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_user_list_page_connected_failed));
                    }
                });
            }
            permissionCallback.onRequestCallback(isAllGranted);
        });
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
                        liveRoomViewModel.leaveSeat(seatModel.getUserID(), errorCode -> {
                        });
                    }
                });
            dialog.show();
        });
        // Fix refresh flickering issue
        SimpleItemAnimator itemAnimator = ((SimpleItemAnimator) binding.rvCoHostList.getItemAnimator());
        if (itemAnimator != null) {
            itemAnimator.setSupportsChangeAnimations(false);
        }
        binding.rvCoHostList.setAdapter(coHostListAdapter);

        memberListDialog = new MemberListDialog(this, userInfo -> {
            if (liveRoomViewModel.isCoHostMax()) {
                ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_room_maximum));
            } else {
                String string = StringUtils.getString(R.string.user_list_page_invite_to_speak);
                DialogHelper.showToastDialog(this, string, dialog -> {
                    liveRoomViewModel.inviteToBeCoHost(userInfo.getUserID(), errorCode -> {
                        if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                            ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_user_list_page_connected_failed));
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
            String userName = UserInfoHelper.getUserName(roomInfo.getHostID());
            binding.liveHeadView.updateHostName(userName);

            int avatarId = AvatarHelper.getAvatarIdByUserName(userName);
            Bitmap bitmap = ImageUtils.getBitmap(avatarId);
            Bitmap blurBitmap = ImageUtils.fastBlur(bitmap, 1F, 15F);
            Bitmap roundBitmap = ImageUtils.toRound(bitmap);

            binding.ivHostBg.setImageBitmap(blurBitmap);
            binding.ivHostHead.setImageBitmap(roundBitmap);
            binding.tvHostName.setText(userName);

            coHostListAdapter.notifyDataSetChanged();
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

            if (!UserInfoHelper.isSelfCoHost()) {
                if (binding.liveBottomView.isConnecting()) {
                    binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_NOT_APPLY);
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
            switch (action.getType()) {
                case LeaveSeat:
                    if (UserInfoHelper.isUserIDSelf(action.getTargetID())) {
                        binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_NOT_APPLY);
                        if (UserInfoHelper.isUserIDHost(action.getOperatorID())) {
                            ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_room_prohibited_connection));
                        }
                    }
                    if (UserInfoHelper.isSelfHost() && !UserInfoHelper.isUserIDHost(action.getOperatorID())) {
                        if (Objects.equals(action.getOperatorID(), action.getTargetID()) && !StringUtils.isTrimEmpty(action.getOperatorID())) {
                            String userName = UserInfoHelper.getUserName(action.getTargetID());
                            ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_room_ended_the_connection, userName));
                        }
                    }
                    break;
                case Mute:
                    if (UserInfoHelper.isUserIDSelf(action.getTargetID()) && UserInfoHelper.isUserIDHost(action.getOperatorID())) {
                        ZegoCoHostSeatModel model = UserInfoHelper.getSelfCoHost();
                        if (model != null) {
                            if (model.isMuted()) {
                                ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_room_muted_by_host));
                            }
                            binding.liveBottomView.enableMicView(!model.isMuted());
                        }
                    }
                    break;
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
                ShareHelper.startToShare(LiveRoomActivity.this, Constants.URL_DOWNLOAD);
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
                if (isMicEnable) {
                    ZegoCoHostSeatModel model = UserInfoHelper.getSelfCoHost();
                    if (model != null) {
                        if (model.isMuted()) {
                            ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_room_muted_by_host));
                            binding.liveBottomView.enableMicView(false);
                            return;
                        }
                    }
                }
                liveRoomViewModel.enableMic(isMicEnable);
            }

            @Override
            public void onApplyConnection() {
                if (liveRoomViewModel.isCoHostMax()) {
                    ToastHelper.showNormalToast(StringUtils.getString(R.string.toast_room_maximum));
                    binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_NOT_APPLY);
                } else {
                    liveRoomViewModel.requestToBeCoHost(errorCode -> {
                        if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                            ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_room_failed_to_operate));
                            binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_NOT_APPLY);
                        } else {
                            showNormalToastDialog(StringUtils.getString(R.string.toast_room_applied_connection));
                        }
                    });
                    ThreadUtils.getMainHandler().removeCallbacks(cancelApplyConnectionRunnable);
                    ThreadUtils.runOnUiThreadDelayed(cancelApplyConnectionRunnable, 30_000L);
                }
            }

            @Override
            public void onCancelApplyConnection() {
                ThreadUtils.getMainHandler().removeCallbacks(cancelApplyConnectionRunnable);
                liveRoomViewModel.cancelRequestToBeCoHost(errorCode -> {
                    if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                        ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_room_failed_to_operate));
                        binding.liveBottomView.toParticipant(LiveBottomView.CONNECTION_APPLYING);
                    } else {
                        dismissAllToast();
                    }
                });
            }

            @Override
            public void onEndConnection() {
                DialogHelper.showAlertDialog(LiveRoomActivity.this,
                        StringUtils.getString(R.string.dialog_attetion_title),
                    StringUtils.getString(R.string.dialog_room_message_ended_the_connection),
                    StringUtils.getString(R.string.dialog_room_page_ok),
                    StringUtils.getString(R.string.dialog_room_page_cancel),
                    (dialog, which) -> {
                        dialog.dismiss();
                        liveRoomViewModel.leaveSeat(null, errorCode -> {
                            if (errorCode != ZegoRoomErrorCode.SUCCESS) {
                                ToastHelper.showWarnToast(StringUtils.getString(R.string.toast_room_failed_to_operate));
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
        builder2.setPositiveButton(R.string.dialog_room_page_ok, (dialog1, which1) -> {
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
        if (isFinishing()) return;
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
        ThreadUtils.getMainHandler().removeCallbacks(cancelApplyConnectionRunnable);
        liveRoomViewModel.leaveRoom(errorCode -> {
        });
    }

    @Override
    public void onBackPressed() {
        if (UserInfoHelper.isSelfHost()) {
            if (!isFinishing()) {
                DialogHelper.showAlertDialog(LiveRoomActivity.this,
                    StringUtils.getString(R.string.room_page_destroy_room),
                    StringUtils.getString(R.string.dialog_sure_to_destroy_room),
                    StringUtils.getString(R.string.dialog_room_page_ok),
                        StringUtils.getString(R.string.dialog_room_page_cancel),
                    true,
                    (dialog, which) -> {
                        dialog.dismiss();
                        super.onBackPressed();
                    },
                    (dialog, which) -> dialog.dismiss()
                );
            }
        } else {
            super.onBackPressed();
        }
    }
}