package com.yh.cn.exception;

public enum ResultEnum {
    UNKNOWN(-1, "未知错误"),
    SUCCESS(0, "成功"),
    PRIMARY_SCHOOL(100, "还是小学生吧"),
    MIDDLE_SCHOOL(101, "是中学生吧");
    private Integer code;
    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}