package com.yh.cn.umeng.push;

import lombok.Data;

import java.util.Date;

/**
 * @author 易煌
 */
@Data
public class UpushRecord {

    private int castType;
    private long userId;
    private String ticker;
    private String text;
    private String title;
    private int actionType;
    private String action;
    private int displayType;
    private String description;
    private Date startDate;
    private Date endDate;
    private String extraInfo;


}
