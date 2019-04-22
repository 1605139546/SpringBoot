package com.yh.cn.umeng.push;

import lombok.Data;

import java.util.Date;

/**
 * @Author 易煌
 */
@Data
public class PushVo {

    /**
     * 设置通知栏提示文字
     */
    private String ticker;

    /**
     * 设置通知标题
     */
    private String title;

    /**
     * 设置通知文字描述
     */
    private String text;

    /**
     * 设置点击通知消息需要跳转URL
     */
    private String url;

    /**
     * 点击通知消息需要跳转activity
     */
    private String activity;

    /**
     * 设置点击"通知"的后续行为为用户自定义内容
     */
    private String custom;

    /**
     * 消息送达到用户设备后，消息内容透传给应用自身进行解析处
     */
    private String displayType;

    /**
     * 设置点击"通知"的后续行为为用户自定义内容时，传送自定义内容
     */
    private String customMsg;

    /**
     * 设备Token 当指定设备传送(例如单播)时,需传该值
     */
    private String deviceToken;

    //用户id(多用户,以逗号分割)
    private String userIds;

    /**
     * 消息推送描述
     */
    private String description;

    /**
     * 推送时间(定时推送需传该参数)
     */
    private Date startDate;

    /**
     * 过期时间(定时推送需传该参数)
     */
    private Date endDate;

    /**
     * 额外信息(json串的形式{"key":"value","key1":"key1"})
     */
    private String extralInfo;


}
