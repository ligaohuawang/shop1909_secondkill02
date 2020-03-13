package com.qf.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("address")
public class Address extends BaseEntity {
    private Integer uid;
    private String person;
    private String address;
    private String phone;
    private String code;
    private Integer isdefault = 0;
}
