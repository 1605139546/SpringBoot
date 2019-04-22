package com.yh.cn.util;

/**
 * 返回结果的工具类封装
 */
public class ResultUtils {
    public static Result success(Object obj) {
        Result result = new Result();
        result.setCode(0);
        result.setMsg("成功");
        result.setData(obj);
        return result;
    }

    public static Result success() {
        return success(null);
    }

    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}