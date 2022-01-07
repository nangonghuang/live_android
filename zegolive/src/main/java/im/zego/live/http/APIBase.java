package im.zego.live.http;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;


public class APIBase {

    private static final String TAG = "APIBase";

    private static Gson mGson = new Gson();
    private static final Handler okHandler = new Handler(Looper.getMainLooper());
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    public static Gson getGson() {
        return mGson;
    }

    public static class OkHttpInstance {

        private volatile static OkHttpInstance instance;
        private OkHttpClient mOkHttpClient;

        private OkHttpInstance() {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
            mOkHttpClient = builder.build();
        }

        public static OkHttpClient getInstance() {
            if (instance == null) {
                synchronized (OkHttpInstance.class) {
                    if (instance == null) {
                        instance = new OkHttpInstance();
                    }
                }
            }
            return instance.mOkHttpClient;
        }

    }

    public static <T> void asyncGet(@NotNull String url, final Class<T> classType,
        final IAsyncGetCallback<T> reqCallback) {
        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

        OkHttpInstance.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                String str = "";
                try {
                    str = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final String finalStr = str;
                Log.d(TAG, "onResponse: api: " + call.request().url().toString());
                Log.d(TAG, "onResponse: respJson: " + finalStr);

                try {
                    JSONObject jsonObject = new JSONObject(finalStr);
                    final int code = jsonObject.getInt("Code");
                    final String message = jsonObject.getString("Message");
                    String dataJson = "";
                    try {
                        dataJson = jsonObject.getJSONObject("Data").toString();
                    } catch (Exception jsonException) {
                        dataJson = "";
                    }

                    if (reqCallback != null) {
                        final T bean = mGson.fromJson(dataJson, classType);

                        okHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                reqCallback.onResponse(code, message, bean);
                            }
                        });

                    }
                } catch (Exception jsonException) {
                    jsonException.printStackTrace();
                    if (reqCallback != null) {
                        okHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                reqCallback.onResponse(ErrorcodeConstants.ErrorJSONFormatInvalid, "Json解析异常", null);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());

                if (reqCallback != null) {
                    okHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            reqCallback.onResponse(ErrorcodeConstants.ErrorFailNetwork, "网络异常", null);
                        }
                    });
                }

            }
        });

    }

    public static <T> void asyncPost(String url, String json, final Class<T> classType,
        final IAsyncGetCallback<T> reqCallback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();
        Call call = OkHttpInstance.getInstance().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure() called with: call = [" + call + "], e = [" + e + "]");
                if (reqCallback != null) {
                    okHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            reqCallback.onResponse(ErrorcodeConstants.ErrorFailNetwork, "网络异常", null);
                        }
                    });
                }

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String str = "";
                try {
                    str = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final String finalStr = str;

                try {
                    JSONObject jsonObject = new JSONObject(finalStr);
                    final int code = jsonObject.getInt("code");
                    final String message = jsonObject.getString("message");
                    String dataJson = "";
                    try {
                        dataJson = jsonObject.getJSONObject("data").toString();
                    } catch (Exception jsonException) {
                        dataJson = "";
                    }

                    if (reqCallback != null) {
                        Log.d(TAG, "dataJson: " + dataJson);
                        final T bean = mGson.fromJson(dataJson, classType);

                        okHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                reqCallback.onResponse(code, message, bean);
                            }
                        });

                    }
                } catch (Exception jsonException) {
                    jsonException.printStackTrace();
                    if (reqCallback != null) {
                        okHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                reqCallback.onResponse(ErrorcodeConstants.ErrorJSONFormatInvalid, "Json解析异常", null);
                            }
                        });
                    }
                }
            }
        });
    }
}
