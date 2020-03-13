package com.qf.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
@TableName("goods")
public class Goods extends BaseEntity {
    private String subject;
    private String info;
    private BigDecimal price;
    private Integer save;
    private Integer type = 1;

    @TableField(exist = false)
    private String fmurl;

    @TableField(exist = false)
    private List<String> otherurls = new ArrayList<>();

    @TableField(exist = false)
    private GoodsSecondKill goodsKill;//商品的秒杀信息

    public void setOtherUrls(String url) {
        otherurls.add(url);
    }
}
