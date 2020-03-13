package com.qf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ResultData<T> {

    private String code;//状态码
    private String msg;//状态信息
    private T data;//需要返回的数据部分

    /**
     * 响应的状态码列表
     * interface接口的所有属性默认加上public static final
     */
    public static interface ResultCodeList {
        String OK = "200";
        String ERROR = "600";
    }

}
