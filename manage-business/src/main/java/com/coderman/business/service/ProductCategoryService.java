package com.coderman.business.service;


import com.coderman.common.error.BusinessException;
import com.coderman.common.model.business.ProductCategory;
import com.coderman.common.vo.business.ProductCategoryTreeNodeVO;
import com.coderman.common.vo.business.ProductCategoryVO;
import com.coderman.common.vo.system.PageVO;

import java.util.List;

/**
 * @Author zhangyukang
 * @Date 2020/3/16 17:18
 * @Version 1.0
 **/
public interface ProductCategoryService {

    /**
     * 添加物资类别
     *
     * @param ProductCategoryVO
     */
    void add(ProductCategoryVO ProductCategoryVO);


    /**
     * 部门列表
     *
     * @param pageNum
     * @param pageSize
     * @param ProductCategoryVO
     * @return
     */
    PageVO<ProductCategoryVO> findProductCategoryList(Integer pageNum, Integer pageSize, ProductCategoryVO ProductCategoryVO);


    /**
     * 编辑物资类别
     *
     * @param id
     * @return
     */
    ProductCategoryVO edit(Long id);

    /**
     * 更新物资类别
     *
     * @param id
     * @param ProductCategoryVO
     */
    void update(Long id, ProductCategoryVO ProductCategoryVO);

    /**
     * 删除物资类别
     *
     * @param id
     */
    void delete(Long id) throws BusinessException;

    /**
     * 查询所物资类别
     *
     * @return
     */
    List<ProductCategoryVO> findAll();

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    ProductCategory findById(Long id);

    /**
     * 分类树形
     *
     * @return
     */
    PageVO<ProductCategoryTreeNodeVO> categoryTree(Integer pageNum, Integer pageSize);

    /**
     * 获取父级分类（2级树）
     *
     * @return
     */
    List<ProductCategoryTreeNodeVO> getParentCategoryTree();

}
