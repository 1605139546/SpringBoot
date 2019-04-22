package com.yh.cn.umeng.push;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 易煌
 */
public class PushEntity {

    private String appMasterSecret;

    public void setAppMasterSecret(String appMasterSecret) {
        this.appMasterSecret = appMasterSecret;
    }


    private   JSONObject rootJson = new JSONObject();

    private final static String PAY_KEY = "payload";

    /**
     * 友盟HOST
     */
    private final static String HOST = "http://msg.umeng.com";

    /**
     * 友盟发送地址
     */
    private final static String POST_PATH = "/api/send";

    /**
     * 友盟上传路径
     */
    private final static String UPLOAD_PATH = "/upload";

    /**
     * 友盟user agent
     */
    private final static String USER_AGENT = "Mozilla/5.0";

    private final static HttpClient client = HttpClientBuilder.create().build();

    private final static Logger logger = LoggerFactory.getLogger(PushEntity.class);

    private static final Set<String> ROOT_KEYS = new HashSet<>(Arrays.asList(new String[]{
            "appkey", "timestamp", "type", "device_tokens", "alias", "alias_type", "file_id",
            "filter", "production_mode", "feedback", "description", "thirdparty_id"}));

    private static final Set<String> PAYLOAD_KEYS = new HashSet<>(Arrays.asList(new String[]{
            "display_type"}));

    private static final Set<String> BODY_KEYS = new HashSet<>(Arrays.asList(new String[]{
            "ticker", "title", "text", "builder_id", "icon", "largeIcon", "img", "play_vibrate", "play_lights", "play_sound",
            "sound", "after_open", "url", "activity", "custom"}));

    private static final Set<String> POLICY_KEYS = new HashSet<>(Arrays.asList(new String[]{
            "start_time", "expire_time", "max_send_num"
    }));

    private static final Set<String> APS_KEYS = new HashSet<>(Arrays.asList(new String[]{
            "alert", "badge", "sound", "content-available", "body"
    }));


    /**
     * 点击通知后的后续行为(打开应用、跳转到URL、打开特定的activity、用户自定义内容)
     */
    public enum AfterOpenAction {
        /**
         * 打开应用
         */
        go_app,
        /**
         * 跳转到URL
         */
        go_url,
        /**
         * 打开特定的activity
         */
        go_activity,
        /**
         * 用户自定义内容
         */
        go_custom
    }

    /**
     * 通知:消息送达到用户设备后的，消息内容的处理(由友盟SDK接管处理、应用自身进行解析处理)
     */
    public enum DisplayType {
        NOTIFICATION {
            @Override
            public String getValue() {
                return "notification";
            }
        },///通知:消息送达到用户设备后，由友盟SDK接管处理并在通知栏上显示通知内容。
        MESSAGE {
            @Override
            public String getValue() {
                return "message";
            }
        };///消息:消息送达到用户设备后，消息内容透传给应用自身进行解析处理。

        public abstract String getValue();
    }

    private Result sendRequestCommon(String url, String postBody) throws IOException {
        String sign = DigestUtils.md5Hex(("POST" + url + postBody + appMasterSecret).getBytes("utf8"));
        url = url + "?sign=" + sign;
        HttpPost post = new HttpPost(url);
        post.setHeader("User-Agent", USER_AGENT);
        StringEntity se = new StringEntity(postBody, "UTF-8");
        post.setEntity(se);
        //向友盟发送post请求，并获得响应
        HttpResponse response = client.execute(post);
        //响应状态
        int status = response.getStatusLine().getStatusCode();
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(result.toString());
        Result result1 = Result.succeedWith(jsonObject.getJSONObject("data"), status, jsonObject.getString("ret"));
        logger.info("\n<<<<<<<<<友盟响应结果为:" + result1);
        return result1;
    }


    /**
     * 向友盟发送请求，并得到响应
     *
     * @return
     * @throws Exception
     */
    public Result sendRequestToUmeng() throws Exception {
        String timestamp = Integer.toString((int) (System.currentTimeMillis() / 1000));
        setPredefinedKeyValueAndroid("timestamp", timestamp);
        String url = HOST + POST_PATH;
        String postBody = rootJson.toString();
        logger.info("\n<<<<<<<<<向友盟发送的数据为:" + postBody);
        //共有的请求代码
        Result jsonResult = sendRequestCommon(url, postBody);
        return jsonResult;
    }


    /**
     * 上传文件向友盟发送请求，得到响应
     *
     * @param appkey
     * @param contents
     * @return
     * @throws Exception
     */
    public Result uploadContents(String appkey, String contents) throws Exception {
        JSONObject uploadJson = new JSONObject();
        uploadJson.put("appkey", appkey);
        String timestamp = Integer.toString((int) (System.currentTimeMillis() / 1000));
        uploadJson.put("timestamp", timestamp);
        uploadJson.put("content", contents);
        String url = HOST + UPLOAD_PATH;
        String postBody = uploadJson.toString();
        logger.info("\n<<<<<<<<<向友盟发送的文件上传数据为:" + postBody);
        Result jsonResult = sendRequestCommon(url, postBody);
        return jsonResult;

    }

    /**
     * 向友盟请求json中拼接数据额外的数据(安卓)  不是必需的
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public boolean setExtraField(String key, String value) {
        String extraKey = "extra";
        dealKey(key, value, PAY_KEY, extraKey);
        return true;
    }

    /**
     * 向友盟请求json中拼接数据额外的数据(IOS)  不是必需的
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public boolean setCustomizedField(String key, String value) throws Exception {
        String extraKey = "payload";
        dealKey(key, value, extraKey);
        return true;
    }

    /**
     * 向友盟请求json中拼接数据(IOS)   必须数据
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public boolean setPredefinedKeyValueIOS(String key, Object value) throws Exception {
        if (ROOT_KEYS.contains(key)) {
            rootJson.put(key, value);
        } else if (PAYLOAD_KEYS.contains(key)) {
            dealKey(key, value, PAY_KEY);
        } else if (APS_KEYS.contains(key)) {
            dealKey(key, value, "payload", "aps");
        } else if (POLICY_KEYS.contains(key)) {
            dealKey(key, value, "policy");
        } else {
            if (key == "payload" || key == "aps" || key == "policy") {
                throw new Exception("You don't need to set value for " + key + " , just set values for the sub keys in it.");
            } else {
                throw new Exception("Unknownd key: " + key);
            }
        }
        return true;
    }

    /**
     * 向友盟请求json中拼接数据(安卓)   必须数据
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public boolean setPredefinedKeyValueAndroid(String key, Object value) throws Exception {

        String bodyKey = "body";
        String policyKey = "policy";

        if (ROOT_KEYS.contains(key)) {
            rootJson.put(key, value);
        } else if (PAYLOAD_KEYS.contains(key)) {
            dealKey(key, value, PAY_KEY);
        } else if (BODY_KEYS.contains(key)) {
            dealKey(key, value, PAY_KEY, bodyKey);
        } else if (POLICY_KEYS.contains(key)) {
            dealKey(key, value, policyKey);
        } else {
            if (key == PAY_KEY || key == bodyKey || key == policyKey || key == "extra") {
                throw new Exception("You don't need to set value for " + key + " , just set values for the sub keys in it.");
            } else {
                throw new Exception("Unknown key: " + key);
            }
        }
        return true;
    }

    /**
     * 对友盟请求数据的key进行处理方式一
     *
     * @param key
     * @param value
     * @param selfKey
     */
    private void dealKey(String key, Object value, String selfKey) {
        JSONObject jsonObject = null;
        if (rootJson.has(selfKey)) {
            jsonObject = rootJson.getJSONObject(selfKey);
        } else {
            jsonObject = new JSONObject();
            rootJson.put(selfKey, jsonObject);
        }
        jsonObject.put(key, value);
    }

    /**
     * 对友盟请求数据的key进行处理方式二
     *
     * @param key
     * @param value
     * @param selfKey
     */
    private void dealKey(String key, Object value, String selfKey, String selfKey2) {
        JSONObject bodyJson = null;
        JSONObject payloadJson = null;
        if (rootJson.has(selfKey)) {
            payloadJson = rootJson.getJSONObject(selfKey);
        } else {
            payloadJson = new JSONObject();
            rootJson.put(selfKey, payloadJson);
        }
        if (payloadJson.has(selfKey2)) {
            bodyJson = payloadJson.getJSONObject(selfKey2);
        } else {
            bodyJson = new JSONObject();
            payloadJson.put(selfKey2, bodyJson);
        }
        bodyJson.put(key, value);
    }


}
