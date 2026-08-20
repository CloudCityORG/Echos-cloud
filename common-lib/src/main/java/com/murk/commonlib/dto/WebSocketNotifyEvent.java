package com.murk.commonlib.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WebSocketNotifyEvent {

    /** 全服广播 */
    public static final int TYPE_PUBLIC = 1;
    /** 私信 */
    public static final int TYPE_PERSONAL = 2;

    private int type;
    private int targetUserId;
    private String title;
    private String content;
    private int bizId;
    private LocalDateTime timestamp;
}
