package com.yh.cn.umeng.push;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author 易煌
 */
public class PushClient {

//    @Autowired
//    private UpushMsgService upushMsgService;

    private final static Logger logger = LoggerFactory.getLogger(PushClient.class);


    private  String appkey="5ca5b27961f5643069000c7c";

    private  String appMasterSecret="wlemkfrygukipmfqydaozcgqmhcmogw4";

    /**
     * 设置模式(正式模式和测试模式)
     */
    private  Boolean moType=false;

    /**
     * 校验消息推送相同的参数
     *
     * @param pushVo
     */
    private Result validParam(PushVo pushVo) {
        Result validResult = null;
        if (pushVo == null) {
            return Result.failed("pushVo不能为空");
        }
        if (pushVo.getTitle() == null) {
            return Result.failed("通知标题不能为空");
        }
        if (pushVo.getText() == null) {
            return Result.failed("设置通知文字描述");
        }
        return null;
    }

    /**
     * 设置消息到达设备之后相关的行为(安卓)
     *
     * @param pushEntity
     * @param pushVo
     * @throws Exception
     */
    private  Result setMsgAction(PushEntity pushEntity, PushVo pushVo, UpushRecord upushMessage, String extraInfo, String CashType) throws Exception {

        //1.设置appkey
        pushEntity.setPredefinedKeyValueAndroid("appkey", appkey);
        //2.设置appMasterSecret
        pushEntity.setAppMasterSecret(appMasterSecret);
        //3.设置播送类型
        pushEntity.setPredefinedKeyValueAndroid("type", CashType);
        //4.设置通知栏提示文字
        pushEntity.setPredefinedKeyValueAndroid("ticker", pushVo.getTicker());
        //5.设置通知标题
        pushEntity.setPredefinedKeyValueAndroid("title", pushVo.getTitle());
        //6.设置通知文字描述
        pushEntity.setPredefinedKeyValueAndroid("text", pushVo.getText());

        upushMessage.setTitle(pushVo.getTitle());
        upushMessage.setTicker(pushVo.getTicker());
        upushMessage.setText(pushVo.getText());

        //模式设置默认值为测试模式
        moType = moType == null ? false : moType;

        //设置点击"通知"的后续行为 跳转到URL
        if (StringUtils.isNotEmpty(pushVo.getUrl())) {
            upushMessage.setActionType(2);
            upushMessage.setAction(pushVo.getUrl());
            pushEntity.setPredefinedKeyValueAndroid("url", pushVo.getUrl());
            pushEntity.setPredefinedKeyValueAndroid("after_open", PushEntity.AfterOpenAction.go_url.toString());
        }
        //设置点击"通知"的后续行为 打开特定的activity
        else if (StringUtils.isNotEmpty(pushVo.getActivity())) {
            upushMessage.setActionType(3);
            upushMessage.setAction(pushVo.getActivity());
            pushEntity.setPredefinedKeyValueAndroid("activity", pushVo.getActivity());
            pushEntity.setPredefinedKeyValueAndroid("after_open", PushEntity.AfterOpenAction.go_activity.toString());
        }
        //设置点击"通知"的后续行为 用户自定义内容
        else if (StringUtils.isNotEmpty(pushVo.getCustom())) {
            upushMessage.setActionType(4);
            pushEntity.setPredefinedKeyValueAndroid("custom", pushVo.getCustomMsg());//自定义消息内容
            upushMessage.setAction(pushVo.getCustomMsg());
            pushEntity.setPredefinedKeyValueAndroid("after_open", PushEntity.AfterOpenAction.go_custom.toString());
        } else {
            //设置点击"通知"的后续行为 打开应用
            upushMessage.setActionType(1);
            pushEntity.setPredefinedKeyValueAndroid("after_open", PushEntity.AfterOpenAction.go_app.toString());
        }
        //6.设置DisplayType  消息送达到用户设备后，消息内容透传给应用自身进行解析处
        if (StringUtils.isNotEmpty(pushVo.getDisplayType())) {
            upushMessage.setDisplayType(2);
            pushEntity.setPredefinedKeyValueAndroid("custom", pushVo.getCustomMsg());
            pushEntity.setPredefinedKeyValueAndroid("display_type", PushEntity.DisplayType.MESSAGE.getValue());
        } else {
            upushMessage.setDisplayType(1);
            //6.设置DisplayType  消息送达到用户设备后，由友盟SDK接管处理并在通知栏上显示通知内容
            pushEntity.setPredefinedKeyValueAndroid("display_type", PushEntity.DisplayType.NOTIFICATION.getValue());
        }

        //7.设置模式(正式模式和测试模式)
        pushEntity.setPredefinedKeyValueAndroid("production_mode", moType);

        //设置推送任务描述
        pushEntity.setPredefinedKeyValueAndroid("description", pushVo.getDescription());
        upushMessage.setDescription(pushVo.getDescription());

        //通过推送时间和过期时间判断 该推送是否为定时推送  否则为立即推送
        Date startDate = pushVo.getStartDate();
        if (startDate != null) {
            if (startDate.getTime() <= System.currentTimeMillis()) {
                throw new RuntimeException("推送时间必须大于当前时间");
            }
            Date endDate = pushVo.getEndDate();
            if (endDate != null) {
                if (endDate.getTime() <= startDate.getTime()) {
                    throw new RuntimeException("过期时间必须大于推送时间");
                }
                final long daysTimes = 7 * 24 * 60 * 60 * 1000;
                if (endDate.getTime() >= (startDate.getTime() + daysTimes)) {
                    throw new RuntimeException("过期时间不能大于推送时间过去7天");
                }
                upushMessage.setEndDate(endDate);
                pushEntity.setPredefinedKeyValueAndroid("expire_time", DateFormatUtils.format(endDate, "yyyy-MM-dd HH:mm:ss"));
            }
            upushMessage.setStartDate(startDate);
            pushEntity.setPredefinedKeyValueAndroid("start_time", DateFormatUtils.format(startDate, "yyyy-MM-dd HH:mm:ss"));
        }

        //设置额外信息
        setExtraInfo(extraInfo, pushEntity);

        upushMessage.setExtraInfo(JSONObject.toJSONString(extraInfo));

        //向友盟发送请求，并返回响应
        return pushEntity.sendRequestToUmeng();
    }


    private  Result setMsgActionIOS(PushEntity pushEntity, PushVo pushVo, UpushRecord upushMessage, String extraInfo, String CashType) throws Exception {

        //1.设置appkey
        pushEntity.setPredefinedKeyValueIOS("appkey", appkey);
        //2.设置appMasterSecret
        pushEntity.setAppMasterSecret(appMasterSecret);
        //3.设置播送类型
        pushEntity.setPredefinedKeyValueIOS("type", CashType);
        //4.设置通知栏提示文字
//        pushEntity.setPredefinedKeyValueIOS("ticker", pushVo.getTicker());
        //5.设置通知标题
//        pushEntity.setPredefinedKeyValueIOS("title", pushVo.getTitle());
        //6.设置通知文字描述
        pushEntity.setPredefinedKeyValueIOS("text", pushVo.getText());

        upushMessage.setTitle(pushVo.getTitle());
        upushMessage.setTicker(pushVo.getTicker());
        upushMessage.setText(pushVo.getText());

        //模式设置默认值为测试模式
        moType = moType == null ? false : moType;

        //设置点击"通知"的后续行为 跳转到URL
        if (StringUtils.isNotEmpty(pushVo.getUrl())) {
            upushMessage.setActionType(2);
            upushMessage.setAction(pushVo.getUrl());
            pushEntity.setPredefinedKeyValueIOS("url", pushVo.getUrl());
            pushEntity.setPredefinedKeyValueIOS("after_open", PushEntity.AfterOpenAction.go_url.toString());
        }
        //设置点击"通知"的后续行为 打开特定的activity
        else if (StringUtils.isNotEmpty(pushVo.getActivity())) {
            upushMessage.setActionType(3);
            upushMessage.setAction(pushVo.getActivity());
            pushEntity.setPredefinedKeyValueIOS("activity", pushVo.getActivity());
            pushEntity.setPredefinedKeyValueIOS("after_open", PushEntity.AfterOpenAction.go_activity.toString());
        }
        //设置点击"通知"的后续行为 用户自定义内容
        else if (StringUtils.isNotEmpty(pushVo.getCustom())) {
            upushMessage.setActionType(4);
//            upushMessage.setAction(pushVo.getActivity());
            pushEntity.setPredefinedKeyValueIOS("after_open", PushEntity.AfterOpenAction.go_custom.toString());
        } else {
            //设置点击"通知"的后续行为 打开应用
            upushMessage.setActionType(1);
            pushEntity.setPredefinedKeyValueIOS("after_open", PushEntity.AfterOpenAction.go_app.toString());
        }
        //6.设置DisplayType  消息送达到用户设备后，消息内容透传给应用自身进行解析处
        if (StringUtils.isNotEmpty(pushVo.getDisplayType())) {
            upushMessage.setDisplayType(2);
            pushEntity.setPredefinedKeyValueIOS("custom", pushVo.getCustomMsg());
            pushEntity.setPredefinedKeyValueIOS("display_type", PushEntity.DisplayType.MESSAGE.getValue());
        } else {
            upushMessage.setDisplayType(1);
            //6.设置DisplayType  消息送达到用户设备后，由友盟SDK接管处理并在通知栏上显示通知内容
            pushEntity.setPredefinedKeyValueIOS("display_type", PushEntity.DisplayType.NOTIFICATION.getValue());
        }

        //7.设置模式(正式模式和测试模式)
        pushEntity.setPredefinedKeyValueIOS("production_mode", moType);

        //设置推送任务描述
        pushEntity.setPredefinedKeyValueIOS("description", pushVo.getDescription());
        upushMessage.setDescription(pushVo.getDescription());

        //通过推送时间和过期时间判断 该推送是否为定时推送  否则为立即推送
        Date startDate = pushVo.getStartDate();
        if (startDate != null) {
            if (startDate.getTime() <= System.currentTimeMillis()) {
                throw new RuntimeException("推送时间必须大于当前时间");
            }
            Date endDate = pushVo.getEndDate();
            if (endDate != null) {
                if (endDate.getTime() <= startDate.getTime()) {
                    throw new RuntimeException("过期时间必须大于推送时间");
                }
                final long daysTimes = 7 * 24 * 60 * 60 * 1000;
                if (endDate.getTime() >= (startDate.getTime() + daysTimes)) {
                    throw new RuntimeException("过期时间不能大于推送时间过去7天");
                }
                upushMessage.setEndDate(endDate);
                pushEntity.setPredefinedKeyValueIOS("expire_time", DateFormatUtils.format(endDate, "yyyy-MM-dd HH:mm:ss"));
            }
            upushMessage.setStartDate(startDate);
            pushEntity.setPredefinedKeyValueIOS("start_time", DateFormatUtils.format(startDate, "yyyy-MM-dd HH:mm:ss"));
        }

        //设置额外信息
        setExtraInfo(extraInfo, pushEntity);

        upushMessage.setExtraInfo(JSONObject.toJSONString(extraInfo));

        //向友盟发送请求，并返回响应
        return pushEntity.sendRequestToUmeng();
    }

    /**
     * 设置额外信息
     *
     * @param extraInfo
     * @param pushEntity
     */
    private  void setExtraInfo(String extraInfo, PushEntity pushEntity) throws Exception {
        if (extraInfo != null) {
            Map<String,String> extraInfoMap = JSONObject.parseObject(extraInfo, Map.class);
            Iterator<Map.Entry<String, String>> iterator = extraInfoMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> keyValue = iterator.next();
                String key = keyValue.getKey();
                String value = keyValue.getValue();
                pushEntity.setExtraField(key, value);
            }
        }
    }

    /**
     * 安卓广播方式(向安装该App的所有设备发送消息)
     *
     * @param pushVo    pushVo.getTicker()    设置通知栏提示文字    不是必输
     *                  pushVo.getTitle()     设置通知标题         必输，但是可以为空字符串
     *                  pushVo.getText()     设置通知文字描述      必输，但是可以为空字符串
     *                  pushVo.getUrl()       当点击通知消息需要跳转URL时 传此值
     *                  pushVo.getActivity()   当点击通知消息需要跳转activity时 传此值
     *                  pushVo.getCustom()  设置点击"通知"的后续行为为用户自定义内容 传此值
     *                  pushVo.getDisplayType()  消息送达到用户设备后，消息内容透传给应用自身进行解析处 传此值 否则交由友盟进行处理
     *                  pushVo.getCustomMsg()    如果传了pushVo.getCustom() 那么此值必传
     *                  pushVo.getDescription()    设置推送任务描述 不是必输
     *                  pushVo.getStartDate()   设置推送时间  定时推送 此值必传 此值必须大于当前时间 否则则为立即推送   重复推送暂时不涉及
     *                  pushVo.getEndDate() 设置过期时间   定时推送  此值如果不传 那过期时间默认为三天 此值必须大于推送时间,但不能超过推送时间的7天   重复推送暂时不涉及
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public Result sendAndroidMsgBroadCast(PushVo pushVo) {
        //校验消息推送相同的参数
        Result result = validParam(pushVo);
        if (result != null) {
            return result;
        }

        UpushRecord upushMessage = new UpushRecord();
        upushMessage.setCastType(1);

        Result jsonResult = null;
        //广播方式
        final String castType = "broadcast";
        PushEntity pushEntity = new PushEntity();

        try {
            //设置播送的基本参数
            jsonResult = setMsgAction(pushEntity, pushVo, upushMessage, pushVo.getExtralInfo(), castType);
            Integer code = 200;
            if (!code.equals(jsonResult.getCode())) {
                return jsonResult;
            }
            //往推送表中插入数据
//            upushMsgService.insertUpushMsg(upushMessage);
        } catch (Exception e) {
            logger.error("\n 安卓广播推送失败:" + e.getMessage());
            jsonResult = Result.failed(e.getLocalizedMessage());
            return jsonResult;
        }
        return jsonResult;
    }

    /**
     * 安卓单播方式(向指定的设备发送消息)
     *
     * @param pushVo    pushVo.getTicker()    设置通知栏提示文字    不是必输
     *                  pushVo.getTitle()     设置通知标题         必输，但是可以为空字符串
     *                  pushVo.getText()     设置通知文字描述      必输，但是可以为空字符串
     *                  pushVo.getDeviceToken()  设置设备Token     必输
     *                  pushVo.getUrl()       当点击通知消息需要跳转URL时 传此值
     *                  pushVo.getActivity()   当点击通知消息需要跳转activity时 传此值
     *                  pushVo.getCustom()  设置点击"通知"的后续行为为用户自定义内容 传此值
     *                  pushVo.getDisplayType()  消息送达到用户设备后，消息内容透传给应用自身进行解析处 传此值 否则交由友盟进行处理
     *                  pushVo.getCustomMsg()    如果传了pushVo.getCustom() 那么此值必传
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public Result sendAndroidMsgUniCast(PushVo pushVo) {
        //校验消息推送相同的参数
        Result result = validParam(pushVo);
        if (result != null) {
            return result;
        }

        if (StringUtils.isEmpty(pushVo.getDeviceToken())) {
            return Result.failed("设备Token不能为空");
        }

        UpushRecord upushMessage = new UpushRecord();
        upushMessage.setCastType(2);

        //单播方式
        final String castType = "unicast";

        PushEntity pushEntity = new PushEntity();
        Result jsonResult = null;
        try {
            //设置设备Token
            pushEntity.setPredefinedKeyValueAndroid("device_tokens", pushVo.getDeviceToken());

            //设置播送的基本参数
            jsonResult = setMsgAction(pushEntity, pushVo, upushMessage, pushVo.getExtralInfo(), castType);
            Integer code = 200;
            if (!code.equals(jsonResult.getCode())) {
                return jsonResult;
            }
            //往推送表中插入数据
//            upushMsgService.insertUpushMsg(upushMessage);
        } catch (Exception e) {
            logger.error("\n 安卓单播推送失败:" + e.getMessage());
            jsonResult = Result.failed(e.getLocalizedMessage());
        }
        return jsonResult;
    }

    /**
     * 安卓自定义播(开发者通过自有的alias进行推送，可以针对单个或者一批alias进行推送，也可以将alias存放到文件进行发送。)
     *
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public Result sendAndroidMsgCustomizedCast(PushVo pushVo) {
        //校验消息推送相同的参数
        Result result = validParam(pushVo);
        if (result != null) {
            return result;
        }
        String userIds = pushVo.getUserIds();

        if (StringUtils.isEmpty(userIds)) {
            return Result.failed("用户ID不能为空");
        }
        PushEntity pushEntity = new PushEntity();
        UpushRecord upushMessage = new UpushRecord();
        upushMessage.setCastType(3);

        Result jsonResult = null;
        //自定义播方式
        final String castType = "customizedcast";

        try {
            //设置别名和别名类型
            //不能超过500个 多个别名以逗号分割
            pushEntity.setPredefinedKeyValueAndroid("alias", userIds);
            //TODO 别名类型定义
            pushEntity.setPredefinedKeyValueAndroid("alias_type", "group");

            //设置播送的基本参数
            jsonResult = setMsgAction(pushEntity, pushVo, upushMessage, pushVo.getExtralInfo(), castType);
            Integer code = 200;
            if (!code.equals(jsonResult.getCode())) {
                return jsonResult;
            }
            String[] userIdss = userIds.split(",");
            List<UpushRecord> upushRecordList = new ArrayList<>();
            if (userIdss != null) {
                UpushRecord upushRecord = null;
                for (String userIdStr : userIdss) {
                    upushRecord = new UpushRecord();
                    BeanUtils.copyProperties(upushMessage, upushRecord);
                    long userId = Long.parseLong(userIdStr);
                    upushRecord.setUserId(userId);
                    upushRecordList.add(upushRecord);
                }
            }
            //往推送表中批量插入数据
//            upushMsgService.insertUpushRecords(upushRecordList);
        } catch (Exception e) {
            logger.error("\n 安卓自定义播推送失败:" + e.getMessage());
            jsonResult = Result.failed(e.getLocalizedMessage());
        }
        return jsonResult;

    }

//    /**
//     * 安卓组播方式
//     *
//     * @throws Exception
//     */
//    public static void sendAndroidMsgGroupCast(String ticker, String title, String text, JSONObject jsonObject) throws
//            Exception {
//        PushEntity pushEntity = new PushEntity();
//        //1.设置appkey
//        pushEntity.setPredefinedKeyValueAndroid("appkey", appkey);
//        //2.appMasterSecret
//        pushEntity.setAppMasterSecret(appMasterSecret);
//        //播送类型
//        pushEntity.setPredefinedKeyValueAndroid("type", "groupcast");
//
//        /**
//         * "where":
//         * {
//         * 	"and":
//         * 	[
//         *                {"tag":"test"},
//         *        {"tag":"Test"}
//         * 	]
//         * }
//         */
//        //发送json数据(json数据得为以上格式)
//        pushEntity.setPredefinedKeyValueAndroid("filter", jsonObject);
//        //2.设置通知栏提示文字
//        pushEntity.setPredefinedKeyValueAndroid("ticker", ticker);
//        //3.设置通知标题
//        pushEntity.setPredefinedKeyValueAndroid("title", title);
//        //4.设置通知文字描述
//        pushEntity.setPredefinedKeyValueAndroid("text", text);
//        //5.设置点击"通知"的后续行为，默认为打开app。
//        pushEntity.setPredefinedKeyValueAndroid("after_open", PushEntity.AfterOpenAction.go_app.toString());
//        //6.设置DisplayType
//        pushEntity.setPredefinedKeyValueAndroid("display_type", PushEntity.DisplayType.NOTIFICATION.getValue());
//        //7.设置模式(正式模式和测试模式)
//        pushEntity.setPredefinedKeyValueAndroid("production_mode", moType);
//        //向友盟发送请求，并返回响应
//        pushEntity.sendRequestToUmeng();
//    }
//
//
//
//    /**
//     * 安卓自定义文件播
//     *
//     * @param ticker
//     * @param title
//     * @param text
//     * @param aliasType
//     * @throws Exception
//     */
//    public void sendAndroidCustomizedCastFile(String ticker, String title, String text, String aliasType, String[]
//            alias) throws Exception {
//        PushEntity pushEntity = new PushEntity();
////        //1.设置appkey
////        pushEntity.setPredefinedKeyValueAndroid("appkey", appkey);
////        //2.appMasterSecret
////        pushEntity.setAppMasterSecret(appMasterSecret);
////        //播送类型
////        pushEntity.setPredefinedKeyValueAndroid("type", "customizedcast");
////        // TODO Set your alias here, and use comma to split them if there are multiple alias.
////        String fileId = data.getString("file_id");
////        JsonResult jsonResult = pushEntity.uploadContents(appkey, "aa" + "\n" + "bb" + "\n" + "alias");
////        String fileId = (String) jsonResult;//
////        //设置别名和别名类型
////        pushEntity.setPredefinedKeyValueAndroid("alias", fileId);
////        pushEntity.setPredefinedKeyValueAndroid("alias_type", aliasType);
////        //2.设置通知栏提示文字
////        pushEntity.setPredefinedKeyValueAndroid("ticker", ticker);
////        //3.设置通知标题
////        pushEntity.setPredefinedKeyValueAndroid("title", title);
////        //4.设置通知文字描述
////        pushEntity.setPredefinedKeyValueAndroid("text", text);
////        //5.设置点击"通知"的后续行为，默认为打开app。
////        pushEntity.setPredefinedKeyValueAndroid("after_open", PushEntity.AfterOpenAction.go_app.toString());
////        //7.设置模式(正式模式和测试模式)
////        pushEntity.setPredefinedKeyValueAndroid("production_mode", moType);
////        //向友盟发送请求，并返回响应
////        pushEntity.sendRequestToUmeng();
//    }
//
//    /**
//     * 安卓文件播
//     *
//     * @param ticker
//     * @param title
//     * @param text
//     * @param aliasType
//     * @throws Exception
//     */
//    public static void sendAndroidMsgFilecast(String ticker, String title, String text, String aliasType) throws
//            Exception {
////        PushEntity pushEntity = new PushEntity();
////        //1.设置appkey
////        pushEntity.setPredefinedKeyValueAndroid("appkey", appkey);
////        //2.appMasterSecret
////        pushEntity.setAppMasterSecret(appMasterSecret);
////        pushEntity.setPredefinedKeyValueAndroid("type", "filecast");
////        // TODO upload your device tokens, and use '\n' to split them if there are multiple tokens
////        String fileId = pushEntity.uploadContents(appkey, "aa" + "\n" + "bb");
////        //设置别名和别名类型
////        pushEntity.setPredefinedKeyValueAndroid("file_id", fileId);
////        pushEntity.setPredefinedKeyValueAndroid("alias_type", aliasType);
////        //2.设置通知栏提示文字
////        pushEntity.setPredefinedKeyValueAndroid("ticker", ticker);
////        //3.设置通知标题
////        pushEntity.setPredefinedKeyValueAndroid("title", title);
////        //4.设置通知文字描述
////        pushEntity.setPredefinedKeyValueAndroid("text", text);
////        //5.设置点击"通知"的后续行为，默认为打开app。
////        pushEntity.setPredefinedKeyValueAndroid("after_open", PushEntity.AfterOpenAction.go_app.toString());
////        //6.设置DisplayType
////        pushEntity.setPredefinedKeyValueAndroid("display_type", PushEntity.DisplayType.NOTIFICATION.getValue());
////        //向友盟发送请求，并返回响应
////        pushEntity.sendRequestToUmeng();
//    }

    //FIXME  有待完善
    public Result sendIOSMsgBroadCast() throws Exception {
        PushEntity pushEntity = new PushEntity();

        //1.设置appkey
        pushEntity.setPredefinedKeyValueIOS("appkey", appkey);
        //2.设置appMasterSecret
        pushEntity.setAppMasterSecret(appMasterSecret);
        //3.设置播送类型
        pushEntity.setPredefinedKeyValueIOS("type", "broadcast");


        pushEntity.setPredefinedKeyValueIOS("alert", "IOS 广播测试");

        pushEntity.setPredefinedKeyValueIOS("badge", 0);

        pushEntity.setPredefinedKeyValueIOS("sound", "default");

        pushEntity.setPredefinedKeyValueIOS("body", "body");


        pushEntity.setPredefinedKeyValueIOS("display_type", PushEntity.DisplayType.MESSAGE.getValue());
        return null;
    }
    public  Result sendIOSMsgBroadCast(PushVo pushVo) throws Exception {
        //校验消息推送相同的参数
        Result result = validParam(pushVo);
        if (result != null) {
            return result;
        }

        UpushRecord upushMessage = new UpushRecord();
        upushMessage.setCastType(1);

        Result jsonResult = null;
        //广播方式
        final String castType = "broadcast";
        PushEntity pushEntity = new PushEntity();

        pushEntity.setCustomizedField("test", "helloworld");
        //设置播送的基本参数
        jsonResult = setMsgActionIOS(pushEntity, pushVo, upushMessage, pushVo.getExtralInfo(), castType);

//        pushEntity.setPredefinedKeyValueIOS("alert", "IOS 广播测试");
//
//        pushEntity.setPredefinedKeyValueIOS("badge", 0);
//
//        pushEntity.setPredefinedKeyValueIOS("sound", "default");
//
//        pushEntity.setPredefinedKeyValueIOS("body", "body");
//
//
//
//
//
//
//        pushEntity.setCustomizedField("test", "helloworld");

         jsonResult = pushEntity.sendRequestToUmeng();
        return jsonResult;
    }

    //FIXME  有待完善
    public Result sendIOSMsgUniCast() throws Exception {
        PushEntity pushEntity = new PushEntity();
        //1.设置appkey
        pushEntity.setPredefinedKeyValueIOS("appkey", appkey);
        //2.设置appMasterSecret
        pushEntity.setAppMasterSecret(appMasterSecret);
        //3.设置播送类型
        pushEntity.setPredefinedKeyValueIOS("type", "unicast");
        pushEntity.setPredefinedKeyValueIOS("device_tokens", "xxx");
        pushEntity.setPredefinedKeyValueIOS("alert", "IOS 广播测试");

        pushEntity.setPredefinedKeyValueIOS("badge", 0);

        pushEntity.setPredefinedKeyValueIOS("sound", "default");
        //7.设置模式(正式模式和测试模式)
        pushEntity.setPredefinedKeyValueIOS("production_mode", moType);

        Result jsonResult = pushEntity.sendRequestToUmeng();
        return jsonResult;
    }
//    public void sendIOSCustomizedcast() throws Exception {
//        IOSCustomizedcast customizedcast = new IOSCustomizedcast(appkey,appMasterSecret);
//        // TODO Set your alias and alias_type here, and use comma to split them if there are multiple alias.
//        // And if you have many alias, you can also upload a file containing these alias, then
//        // use file_id to send customized notification.
//        customizedcast.setAlias("alias", "alias_type");
//        customizedcast.setAlert("IOS 个性化测试");
//        customizedcast.setBadge( 0);
//        customizedcast.setSound( "default");
//        // TODO set 'production_mode' to 'true' if your app is under production mode
//        customizedcast.setTestMode();
//        client.send(customizedcast);
//    }


    public static void main(String[] args) {
        PushVo pushVo = new PushVo();
        pushVo.setText("安卓广播一个消息");
        pushVo.setTitle("安卓标题");
        pushVo.setTicker("安卓");
        pushVo.setCustomMsg("asfasf");
        pushVo.setDescription("安卓推送描述");
        pushVo.setUserIds("15555555555");
//        pushVo.setStartDate(new Date(1555592027819L));
//        pushVo.setEndDate(new Date(1555692127819L));
        pushVo.setUrl("http://www.baidu.com");
//        pushVo.setDisplayType("");
        Map<String, String> map = new HashMap<String, String>() {{
            put("key", "value");
            put("key1", "value1");
            put("key2", "value12");
        }};
        Result jsonResult = new PushClient().sendAndroidMsgCustomizedCast(pushVo);

        System.out.println();
        System.out.println("<<<<<<<<<<结果是<<<<<<<<<<<<<" + jsonResult);
    }
}
