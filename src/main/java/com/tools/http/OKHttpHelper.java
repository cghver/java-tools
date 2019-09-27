package com.tools.http;

import com.tools.json.JsonHelper;
import com.tools.response.ResponseCode;
import okhttp3.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OKHttpHelper {
    @FunctionalInterface
    public interface OkHttpResHandler {
        void execute(String res);
    }

    private Map<String, Object> properties = new HashMap<>();
    private String url = null;
    private String res = null;
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();
    private OkHttpClient tmpClient = null;

    public OKHttpHelper() {

    }

    public OKHttpHelper(int connectTimeout, int readTimeout) {
        this.tmpClient = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .build();
    }

    public OKHttpHelper setUrl(String url) {
        this.url = url;
        return this;
    }

    public OKHttpHelper addProperty(String key, Object value) {
        if (key != null && value != null) {
            properties.put(key, value);
        }
        return this;
    }

    /**
     * 请求消息返回正确
     *
     * @param okHttpResHandler
     * @return
     */
    public OKHttpHelper onSuccess(OkHttpResHandler okHttpResHandler) {
        if (res != null) {
            if (!ResponseCode.CONNECT_TIMEOUT.getCode().equals(JsonHelper.getNodeString("code", res))
                    && !ResponseCode.READ_TIMEOUT.getCode().equals(JsonHelper.getNodeString("code", res))) {
                okHttpResHandler.execute(res);
            }
        }
        return this;
    }


    /**
     * 无论网络请求是否成功，都处理
     *
     * @param okHttpResHandler
     * @return
     */
    public OKHttpHelper onCallback(OkHttpResHandler okHttpResHandler) {
        if (res != null) {
            okHttpResHandler.execute(res);
        }
        return this;
    }

    /**
     * 网络连接超时
     *
     * @param okHttpResHandler
     * @return
     */
    public OKHttpHelper onConnectTimeout(OkHttpResHandler okHttpResHandler) {
        if (res != null) {
            if (ResponseCode.CONNECT_TIMEOUT.getCode().equals(JsonHelper.getNodeString("code", res))) {
                okHttpResHandler.execute(res);
            }
        }
        return this;
    }

    /**
     * 网络读取超时
     *
     * @param okHttpResHandler
     * @return
     */
    public OKHttpHelper onReadTimeout(OkHttpResHandler okHttpResHandler) {
        if (res != null) {
            if (ResponseCode.READ_TIMEOUT.getCode().equals(JsonHelper.getNodeString("code", res))) {
                okHttpResHandler.execute(res);
            }
        }
        return this;
    }


    /**
     * 链式post请求，json格式
     *
     * @return
     */
    public OKHttpHelper sendChainedPostAsJson() {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, JsonHelper.transToString(properties));
        Request request = new Request.Builder().url(url).post(body).build();
        doHttp(request);
        return this;
    }

    /**
     * 以from的方式提交 application/x-www-form-urlencoded,链式
     *
     * @return
     */
    public OKHttpHelper sendChainedPostAsForm() {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : properties.keySet()) {
            builder.add(key, (String) properties.get(key));
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        doHttp(request);
        return this;
    }

    /**
     * 链式get请求，json格式
     *
     * @return
     */
    public OKHttpHelper sendChainedGet() {
        Request request = new Request.Builder().url(url).build();
        doHttp(request);
        return this;
    }

    private String doHttp(Request request) {
        try {
            Response response;
            if (tmpClient != null) {
                response = tmpClient.newCall(request).execute();
            } else {
                response = client.newCall(request).execute();
            }

            String responseBodyString = null;
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                responseBodyString = responseBody.string();
            }

            if (response.code() != 200) {
                res = JsonHelper.transToString(com.tools.response.Response.failed("请求错误", responseBodyString));
            } else {
                res = responseBodyString;
            }
        } catch (SocketTimeoutException | ConnectException e) {
            res = socketTimeoutExceptionProcess(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tmpClient = null;
        return res;
    }

    private String socketTimeoutExceptionProcess(Exception e) {
        Map<String, Object> map = new HashMap<>();
        if (e.getMessage().trim().equals("Read timed out")) {
            map.put("code", ResponseCode.READ_TIMEOUT.getCode());
            map.put("response", "读取返回数据超时");
            map.put("data", null);
        } else {
            map.put("code", ResponseCode.CONNECT_TIMEOUT.getCode());
            map.put("response", "网络连接超时");
            map.put("data", null);
        }
        return JsonHelper.transToString(map);
    }

    /**
     * 以from的方式提交 application/x-www-form-urlencoded
     *
     * @param url
     * @param params
     * @return
     */
    public String sendPostAsForm(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        return doHttp(request);
    }

    /**
     * 参数以json形式 注意content-type
     *
     * @param url
     * @param json
     * @return
     */
    public String sendPostAsJson(String url, String json) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        return doHttp(request);
    }

    /**
     * Get请求
     *
     * @param url
     * @return
     */
    public String sendGet(String url) {
        Request request = new Request.Builder().url(url).build();
        return doHttp(request);
    }

    public static void main(String[] args) {
        new OKHttpHelper()
                .setUrl("http://172.16.41.161:8072/v2/ccs/plog/find")
                .addProperty("pageNo", 1)
                .addProperty("pageSize", 10)
                .sendChainedPostAsJson()
                .onConnectTimeout(v -> {
                    System.out.println("====网络连接超时=====");
                    System.out.println(v);
                })
                .onReadTimeout(v -> {
                    System.out.println("====读取数据超时=====");
                    System.out.println(v);
                })
                .onSuccess(v -> {
                    System.out.println("====成功接收到数据=====");
                    System.out.println(v);
                })
                .onCallback(v -> {
                    System.out.println("====无论如何都会执行=====");
                    System.out.println(v);
                });
    }
}
