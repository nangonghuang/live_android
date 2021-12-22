package im.zego.livedemo.feature.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;

import org.json.JSONException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.zego.live.ZegoRoomManager;
import im.zego.live.constants.ZegoRoomErrorCode;
import im.zego.live.model.ZegoUserInfo;
import im.zego.live.util.TokenServerAssistant;
import im.zego.livedemo.KeyCenter;
import im.zego.livedemo.R;
import im.zego.livedemo.base.BaseActivity;
import im.zego.livedemo.databinding.ActivityUserLoginBinding;
import im.zego.livedemo.feature.room.RoomListActivity;

public class UserLoginActivity extends BaseActivity<ActivityUserLoginBinding> {

    public static void start(Context context) {
        Intent intent = new Intent(context, UserLoginActivity.class);
        context.startActivity(intent);
    }

    // login info
    private String userID;
    private String userName;

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
        binding.etUserId.setText(DeviceUtils.getManufacturer() + (int) (100 + Math.random() * 900));
    }

    private void initListener() {
        binding.btnLogin.setOnClickListener(v -> {
            userID = binding.etUserId.getText().toString();
            userName = binding.etUserName.getText().toString();
            if (TextUtils.isEmpty(userName)) {
                userName = userID;
            }
            String regEx = "^[a-zA-Z\\d]+$";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(binding.etUserId.getText().toString());
            if (!m.matches()) {
                ToastUtils.showShort(R.string.toast_user_id_error);
                return;
            }

            ZegoUserInfo user = new ZegoUserInfo();
            if (!(TextUtils.isEmpty(userID))) {
                user.setUserID(userID);
                user.setUserName(userName);
                try {
                    // Call Chat Room SDK
                    String token = TokenServerAssistant.generateToken(KeyCenter.appID(), userID, KeyCenter.appZIMServerSecret(), 60 * 60 * 24).data;
                    ZegoRoomManager.getInstance().userService.login(user, token, errorCode -> {
                        if (errorCode == ZegoRoomErrorCode.SUCCESS) {
                            RoomListActivity.start(this);
                        } else {
                            ToastUtils.showShort(StringUtils.getString(R.string.toast_login_fail, errorCode));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastUtils.showShort(StringUtils.getString(R.string.toast_userid_login_fail));
            }
        });
    }
}