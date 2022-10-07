package com.coderman.business.mapper;


import com.coderman.common.model.business.Product;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author zhangyukang
 * @Date 2020/3/17 09:18
 * @Version 1.0
 **/
public interface ProductMapper extends Mapper<Product> {

    /**
     * 批量修改
     *
     * @param list
     */
    void editBatch(@Param("list") List<Product> list);
}
