package im.zego.livedemo.feature.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

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
import im.zego.livedemo.feature.BaseActivity;
import im.zego.livedemo.helper.PermissionHelper;

public class UserLoginActivity extends BaseActivity {
    private EditText etUserId;
    private EditText etUserName;
    private Button btnLogin;

    // login info
    private String userID;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        initUI();
        PermissionHelper.requestRecordAudio(this, null);
    }

    private void initUI() {
        etUserId = findViewById(R.id.et_user_id);
        etUserName = findViewById(R.id.et_user_name);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            userID = etUserId.getText().toString();
            userName = etUserName.getText().toString();
            if (TextUtils.isEmpty(userName)) {
                userName = userID;
            }
            String regEx = "^[a-zA-Z\\d]+$";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(etUserId.getText().toString());
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

        etUserId.setText(DeviceUtils.getManufacturer() + (int) (100 + Math.random() * 900));
    }
}