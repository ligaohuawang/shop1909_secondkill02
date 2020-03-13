package com.qf.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("goods_images")
public class GoodsImages extends BaseEntity {
    private Integer gid;
    private String info;
    private String url;
    private Integer isfengmian;
}
