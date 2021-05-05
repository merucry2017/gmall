package com.merc.gmall.cart.mapper;

import com.merc.gmall.bean.OmsCartItem;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface OmsCartItemMapper extends Mapper<OmsCartItem> {

    int deleteBySkuIdAndMemberId(@Param("memberId") String memberId, @Param("skuId") String skuId);
}
