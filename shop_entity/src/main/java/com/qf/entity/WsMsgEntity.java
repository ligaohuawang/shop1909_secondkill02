package com.qf.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class WsMsgEntity<T> implements Serializable {
    private Integer fromid = -1;//发送方
    private Integer toid;//接收方
    private Integer type;//1初始化，2心跳，3秒杀提醒
    private T data;
}
