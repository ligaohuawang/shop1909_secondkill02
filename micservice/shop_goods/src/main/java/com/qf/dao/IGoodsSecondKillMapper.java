package com.qf.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qf.entity.GoodsSecondKill;
import org.apache.ibatis.annotations.Param;

public interface IGoodsSecondKillMapper extends BaseMapper<GoodsSecondKill> {
    void updateKillSave(@Param("gid") Integer gid, @Param("gnumber") Integer gnumber);
}
