package im.zego.livedemo.feature.room;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import im.zego.live.http.APIBase;
import im.zego.live.http.IAsyncGetCallback;
import im.zego.livedemo.feature.room.model.GetRoomListParam;
import im.zego.livedemo.feature.room.model.RoomBean;
import im.zego.livedemo.feature.room.model.RoomList;
import im.zego.livedemo.feature.room.model.RoomRequestCommonParam;

public class RoomApi {

    private static final String TAG = "RoomApi";
    private static final String baseUrl = "http://192.168.100.44:3128";
    private static Gson gson = new Gson();

    public static final int PARAM_ERROR = 4;
    public static final int ROOM_NOT_EXISTED = 80001;
    public static final int USER_NOT_INROOM = 80002;
    public static final int SYSTEM_ERROR = 100000;

    public static void getRoomList(int pageNumber, String fromIndex, IAsyncGetCallback<RoomList> reqCallback) {
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        builder.appendEncodedPath("v1/room/get_room_list");
        String url = builder.build().toString();

        GetRoomListParam param = new GetRoomListParam();
        if (!TextUtils.isEmpty(fromIndex)) {
            param.fromIndex = fromIndex;
        }
        param.pageNumber = pageNumber;

        String json = gson.toJson(param);
        Log.d(TAG, "getRoomList: url:" + url + ",json:" + json);

        APIBase.asyncPost(url, json, RoomList.class, new IAsyncGetCallback<RoomList>() {
            @Override
            public void onResponse(int errorCode, @NonNull String message, RoomList responseJsonBean) {
                Log.d(TAG,
                    "getRoomList onResponse() called with: errorCode = [" + errorCode + "], message = [" + message
                        + "], responseJsonBean = [" + responseJsonBean + "]");
                if (reqCallback != null) {
                    reqCallback.onResponse(errorCode, message, responseJsonBean);
                }
            }
        });
    }

    public static void createRoom(String name, String hostID, IAsyncGetCallback<RoomBean> reqCallback) {
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        builder.appendEncodedPath("v1/room/create_room");
        String url = builder.build().toString();

        RoomRequestCommonParam param = new RoomRequestCommonParam();
        param.hostID = hostID;
        param.name = name;

        String json = gson.toJson(param);
        Log.d(TAG, "crateRoom: url:" + url + ",json:" + json);

        APIBase.asyncPost(url, json, RoomBean.class, new IAsyncGetCallback<RoomBean>() {
            @Override
            public void onResponse(int errorCode, @NonNull String message, RoomBean responseJsonBean) {
                Log.d(TAG, "crateRoom onResponse() called with: errorCode = [" + errorCode + "], message = [" + message
                    + "], responseJsonBean = [" + responseJsonBean + "]");
                if (reqCallback != null) {
                    reqCallback.onResponse(errorCode, message, responseJsonBean);
                }
            }
        });
    }

    public static void endRoom(String roomID, IAsyncGetCallback<RoomBean> reqCallback) {
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        builder.appendEncodedPath("v1/room/end_room");
        String url = builder.build().toString();

        RoomRequestCommonParam param = new RoomRequestCommonParam();
        param.roomID = roomID;

        String json = gson.toJson(param);
        Log.d(TAG, "endRoom: url:" + url + ",json:" + json);

        APIBase.asyncPost(url, json, RoomBean.class, new IAsyncGetCallback<RoomBean>() {
            @Override
            public void onResponse(int errorCode, @NonNull String message, RoomBean responseJsonBean) {
                Log.d(TAG, "endRoom onResponse() called with: errorCode = [" + errorCode + "], message = [" + message
                    + "], responseJsonBean = [" + responseJsonBean + "]");
                if (reqCallback != null) {
                    reqCallback.onResponse(errorCode, message, responseJsonBean);
                }
            }
        });
    }

    public static void joinRoom(String userID, String roomID, IAsyncGetCallback<RoomBean> reqCallback) {
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        builder.appendEncodedPath("v1/room/join_room");
        String url = builder.build().toString();

        RoomRequestCommonParam param = new RoomRequestCommonParam();
        param.roomID = roomID;
        param.userID = userID;

        String json = gson.toJson(param);
        Log.d(TAG, "joinRoom: url:" + url + ",json:" + json);

        APIBase.asyncPost(url, json, RoomBean.class, new IAsyncGetCallback<RoomBean>() {
            @Override
            public void onResponse(int errorCode, @NonNull String message, RoomBean responseJsonBean) {
                Log.d(TAG, "joinRoom onResponse() called with: errorCode = [" + errorCode + "], message = [" + message
                    + "], responseJsonBean = [" + responseJsonBean + "]");
                if (reqCallback != null) {
                    reqCallback.onResponse(errorCode, message, responseJsonBean);
                }
            }
        });
    }

    public static void leaveRoom(String userID, String roomID, IAsyncGetCallback<RoomBean> reqCallback) {
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        builder.appendEncodedPath("v1/room/leave_room");
        String url = builder.build().toString();

        RoomRequestCommonParam param = new RoomRequestCommonParam();
        param.roomID = roomID;
        param.userID = userID;

        String json = gson.toJson(param);
        Log.d(TAG, "leaveRoom: url:" + url + ",json:" + json);

        APIBase.asyncPost(url, json, RoomBean.class, new IAsyncGetCallback<RoomBean>() {
            @Override
            public void onResponse(int errorCode, @NonNull String message, RoomBean responseJsonBean) {
                Log.d(TAG, "leaveRoom onResponse() called with: errorCode = [" + errorCode + "], message = [" + message
                    + "], responseJsonBean = [" + responseJsonBean + "]");
                if (reqCallback != null) {
                    reqCallback.onResponse(errorCode, message, responseJsonBean);
                }
            }
        });
    }

    public static void heartBeat(String userID, String roomID, boolean keepRoom,
        IAsyncGetCallback<RoomBean> reqCallback) {
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        builder.appendEncodedPath("v1/room/heartbeat");
        String url = builder.build().toString();

        RoomRequestCommonParam param = new RoomRequestCommonParam();
        param.roomID = roomID;
        param.userID = userID;
        param.keepRoom = keepRoom;

        String json = gson.toJson(param);
        Log.d(TAG, "heartBeat: url:" + url + ",json:" + json);

        APIBase.asyncPost(url, json, RoomBean.class, new IAsyncGetCallback<RoomBean>() {
            @Override
            public void onResponse(int errorCode, @NonNull String message, RoomBean responseJsonBean) {
                Log.d(TAG, "heartBeat onResponse() called with: errorCode = [" + errorCode + "], message = [" + message
                    + "], responseJsonBean = [" + responseJsonBean + "]");
                if (reqCallback != null) {
                    reqCallback.onResponse(errorCode, message, responseJsonBean);
                }
            }
        });
    }
}
