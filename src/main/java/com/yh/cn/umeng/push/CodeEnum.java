package com.yh.cn.umeng.push;

public enum CodeEnum {

    SUCCESS(200,"操作成功"),
    ERROR(-1,"操作失败"),
    REQUEST_GET_SUPPORTED(1001, "请求不支持GET，请使用POST"),
    REQUEST_POST_SUPPORTED(1002, "请求不支持POST，请使用GET"),

    /**
     * 用户登录注册验证
     * @author      FZ
     * @date        2019/4/15 19:06
     */
    USERNAME_NOT_NULL(1003, "用户名不能为空"),
    /**
     * 用户登录注册验证
     * @author      FZ
     * @date        2019/4/15 19:06
     */
    PASSWORD_NOT_NULL(1004, "密码不能为空"),
    /**
     * 用户登录注册验证
     * @author      FZ
     * @date        2019/4/15 19:06
     */
    PASSWORD_CODE_ERROR(1005, "密码有误"),
    /**
     * 用户登录注册验证
     * @author      FZ
     * @date        2019/4/15 19:06
     */
    VERIFY_CODE_NOT_NULL(1006, "验证码不能为空"),
    /**
     * 用户登录注册验证
     * @author      FZ
     * @date        2019/4/15 19:06
     */
    VERIFY_CODE_ERROR(1007, "验证码有误"),
    /**
     * 用户登录注册验证
     * @author      FZ
     * @date        2019/4/15 19:06
     */
    USER_NOT_FOUND_ERROR(1008, "用户不存在"),

    /**
     * 用户登录注册验证
     * @author      FZ
     * @date        2019/4/15 19:06
     */
    USER_NOT_EMPTY(1009, "用户已存在"),
    /**
     * 用户登录注册验证
     * @author      FZ
     * @date        2019/4/15 19:06
     */
    GET_ACCESS_TOKEN_FAIL(1010, "获取token失败"),
    /**
     * 用户登录注册验证
     * @author      FZ
     * @date        2019/4/18 19:06
     */
    ACCESS_TOKEN_NULLITY(1011, "token失效"),

    /**
     * 手机号校验
     * @author      xq
     * @date        2019/4/18 19:06
     */
    USER_NOT_MOBLIE_EMPTY(1011, "手机号码不能为空！"),

    /**
     * 密码校验
     * @author      xq
     * @date        2019/4/18 19:06
     */
    USER_ANGIN_PASSWORD(1012, "两次输入的密码不一致！"),





    /**
     * 短信发送失败
     */
    SMS_SEND_FAIL(2001,"短信发送失败"),

    /**
     * 短信平台信息在数据库中不存在
     */
    SMS_PLATFORM_NOTEXISTS(2002,"第三方短信平台信息不存在"),

    /**
     * 在指定时间内已发送过验证码
     */
    SMS_VERIFY_SENDED(2003,"已发送验证码，请稍后再试"),

    /**
     * 消息模板不存在
     */
    SMS_TEMPLATE_NOTEXISTS(2004,"消息模板不存在"),

    /**
     * 手机号为空
     */
    USER_MOBILE_EMPTY(2005,"手机号不能为空"),

    /**
     * 验证码发送类型为空
     */
    VERIFY_TYPE_EMPTY(2006,"验证码发送类型不能为空"),


    EXCEPTION(9999, "接口请求异常");

    private Integer code;
    private String msg;

    CodeEnum(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }}
