package com.coderman.business.service.imp;

import com.coderman.business.converter.ProductConverter;
import com.coderman.business.mapper.ProductMapper;
import com.coderman.business.mapper.ProductStockMapper;
import com.coderman.business.service.ProductCategoryService;
import com.coderman.business.service.ProductService;
import com.coderman.common.enums.buisiness.ProductStatusEnum;
import com.coderman.common.error.BusinessCodeEnum;
import com.coderman.common.error.BusinessException;
import com.coderman.common.model.business.Product;
import com.coderman.common.model.business.ProductCategory;
import com.coderman.common.utils.ConstantUtil;
import com.coderman.common.utils.DateUtil;
import com.coderman.common.vo.business.ProductStockVO;
import com.coderman.common.vo.business.ProductVO;
import com.coderman.common.vo.system.PageVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author zhangyukang
 * @Date 2020/3/16 17:19
 * @Version 1.0
 **/
@Service
@Transactional
public class ProductServiceImpl implements ProductService {


    @Autowired
    private ProductMapper productMapper;


    @Autowired
    private ProductStockMapper productStockMapper;

    @Autowired
    private ProductCategoryService productCategoryService;


    /**
     * 商品列表
     *
     * @param pageNum
     * @param pageSize
     * @param productVO
     * @return
     */
    @Override
    public PageVO<ProductVO> findProductList(Integer pageNum, Integer pageSize, ProductVO productVO) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products;
        Example o = new Example(Product.class);
        Example.Criteria criteria = o.createCriteria();
        if (productVO.getThreeCategoryId() != null) {
            criteria.andEqualTo("oneCategoryId", productVO.getOneCategoryId()).andEqualTo("twoCategoryId", productVO.getTwoCategoryId()).andEqualTo("threeCategoryId", productVO.getThreeCategoryId());
            products = productMapper.selectByExample(o);
            List<ProductVO> categoryVOS = ProductConverter.converterToVOList(products);
            PageInfo<Product> info = new PageInfo<>(products);
            return new PageVO<>(info.getTotal(), categoryVOS);
        }
        if (productVO.getName() != null && !"".equals(productVO.getName())) {
            criteria.andLike("name", "%" + productVO.getName() + "%");
        }
        if (productVO.getHead() != null && !"".equals(productVO.getHead())) {
            criteria.andLike("head", "%" + productVO.getHead() + "%");
        }
        if (productVO.getTwoCategoryId() != null) {
            criteria.andEqualTo("oneCategoryId", productVO.getOneCategoryId()).andEqualTo("twoCategoryId", productVO.getTwoCategoryId());
            products = productMapper.selectByExample(o);
            List<ProductVO> categoryVOS = ProductConverter.converterToVOList(products);
            PageInfo<Product> info = new PageInfo<>(products);
            return new PageVO<>(info.getTotal(), categoryVOS);
        }
        if (productVO.getOneCategoryId() != null) {
            criteria.andEqualTo("oneCategoryId", productVO.getOneCategoryId());
            products = productMapper.selectByExample(o);
            List<ProductVO> categoryVOS = ProductConverter.converterToVOList(products);
            PageInfo<Product> info = new PageInfo<>(products);
            return new PageVO<>(info.getTotal(), categoryVOS);
        }
        if (productVO.getStatus() != null) {
            if (ProductStatusEnum.ONE_MONTHS.getStatus().equals(productVO.getStatus())) {
                criteria.andIn("status", Arrays.asList(ProductStatusEnum.ONE_MONTHS.getStatus()));
            } else if (ProductStatusEnum.THREE_MONTHS.getStatus().equals(productVO.getStatus())) {
                criteria.andIn("status", Arrays.asList(ProductStatusEnum.ONE_MONTHS.getStatus(), ProductStatusEnum.THREE_MONTHS.getStatus()));
            } else if (ProductStatusEnum.HALF_YEAR.getStatus().equals(productVO.getStatus())) {
                criteria.andIn("status", Arrays.asList(ProductStatusEnum.HALF_YEAR.getStatus(),
                        ProductStatusEnum.ONE_MONTHS.getStatus(), ProductStatusEnum.THREE_MONTHS.getStatus()));
            } else if (ProductStatusEnum.EXPIRED.getStatus().equals(productVO.getStatus())) {
                criteria.andEqualTo("status", ProductStatusEnum.ONE_MONTHS.getStatus());
            } else {
                criteria.andIn("status", ProductStatusEnum.getStatusAll());
            }
        }
        if (!StringUtils.isEmpty(productVO.getFilter())) {
            o.setOrderByClause(productVO.getFilter() + " " + productVO.getFilterType());
        } else {
            o.setOrderByClause("create_time desc, id asc");
        }

        products = productMapper.selectByExample(o);
        List<ProductVO> categoryVOS = converterToVOList(products);
        PageInfo<Product> info = new PageInfo<>(products);
        return new PageVO<>(info.getTotal(), categoryVOS);
    }


    private List<ProductVO> converterToVOList(List<Product> products) {
        List<ProductVO> productVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(products)) {
            products.stream().forEach(product -> {
                ProductVO productVO = converterToProductVO(product);
                productVOS.add(productVO);
            });
        }
        return productVOS;
    }

    /**
     * 转VO
     *
     * @param product
     * @return
     */
    private ProductVO converterToProductVO(Product product) {
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product, productVO);
        if (product.getThreeCategoryId() != null) {
            ProductCategory productCategory = productCategoryService.findById(product.getThreeCategoryId());
            productVO.setModel(productCategory.getName());
        } else if (product.getTwoCategoryId() != null) {
            ProductCategory productCategory = productCategoryService.findById(product.getThreeCategoryId());
            productVO.setModel(productCategory.getName());
        } else if (product.getOneCategoryId() != null) {
            ProductCategory productCategory = productCategoryService.findById(product.getThreeCategoryId());
            productVO.setModel(productCategory.getName());
        }
        return productVO;
    }

    private void getStatuesByDay(ProductVO productVO) {
        Long day = DateUtil.getDateCalculatedDay(productVO.getExpectTime());
        if (day > ConstantUtil.HALF_YEAR) {
            productVO.setStatus(ProductStatusEnum.ENABLE.getStatus());
        } else if (day > ConstantUtil.THREE_MONTHS) {
            productVO.setStatus(ProductStatusEnum.HALF_YEAR.getStatus());
        } else if (day > ConstantUtil.ONE_MONTHS) {
            productVO.setStatus(ProductStatusEnum.THREE_MONTHS.getStatus());
        } else if (day > 0) {
            productVO.setStatus(ProductStatusEnum.ONE_MONTHS.getStatus());
        } else {
            productVO.setStatus(ProductStatusEnum.EXPIRED.getStatus());
        }
    }

    /**
     * 添加商品
     *
     * @param ProductVO
     */
    @Override
    public void add(ProductVO ProductVO) {
        Product product = new Product();
        getStatuesByDay(ProductVO);
        BeanUtils.copyProperties(ProductVO, product);
        product.setCreateTime(new Date());
        @NotNull(message = "类别不能为空") Long[] categoryKeys = ProductVO.getCategoryKeys();
        if (categoryKeys.length == 3) {
            product.setOneCategoryId(categoryKeys[0]);
            product.setTwoCategoryId(categoryKeys[1]);
            product.setThreeCategoryId(categoryKeys[2]);
        }
        product.setStatus(0);
        productMapper.insert(product);
    }

    /**
     * 编辑商品
     *
     * @param id
     * @return
     */
    @Override
    public ProductVO edit(Long id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return ProductConverter.converterToProductVO(product);
    }

    @Override
    @Transactional
    public void editBatch(List<Product> productList) {
        productMapper.editBatch(productList);
    }

    public ProductVO findById(Long id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return ProductConverter.converterToProductVO(product);
    }

    /**
     * 更新商品
     *
     * @param id
     * @param ProductVO
     */
    @Override
    public void update(Long id, ProductVO ProductVO) {
        Product product = new Product();
        getStatuesByDay(ProductVO);
        BeanUtils.copyProperties(ProductVO, product);
        @NotNull(message = "分类不能为空") Long[] categoryKeys = ProductVO.getCategoryKeys();
        if (categoryKeys.length == 3) {
            product.setOneCategoryId(categoryKeys[0]);
            product.setTwoCategoryId(categoryKeys[1]);
            product.setThreeCategoryId(categoryKeys[2]);
        }
        productMapper.updateByPrimaryKey(product);
    }

    /**
     * 删除商品
     *
     * @param id
     */
    @Override
    public void delete(Long id) throws BusinessException {
        Product t = new Product();
        t.setId(id);
        Product product = productMapper.selectByPrimaryKey(t);
        product.setStatus(ProductStatusEnum.DELETE.getStatus());
        productMapper.updateByPrimaryKey(product);
    }

    /**
     * 物资库存列表
     *
     * @param pageNum
     * @param pageSize
     * @param productVO
     * @return
     */
    @Override
    public PageVO<ProductStockVO> findProductStocks(Integer pageNum, Integer pageSize, ProductVO productVO) {
        PageHelper.startPage(pageNum, pageSize);
        List<ProductStockVO> productStockVOList = productStockMapper.findProductStocks(productVO);
        PageInfo<ProductStockVO> info = new PageInfo<>(productStockVOList);
        return new PageVO<>(info.getTotal(), productStockVOList);
    }

    /**
     * 所有库存信息
     *
     * @return
     */
    @Override
    public List<ProductStockVO> findAllStocks(Integer pageNum, Integer pageSize, ProductVO productVO) {
        PageHelper.startPage(pageNum, pageSize);
        return productStockMapper.findAllStocks(productVO);
    }

    @Override
    public Integer findAllCount() {
        return productMapper.selectCount(null);
    }

    /**
     * 移入回收站
     *
     * @param id
     */
    @Override
    public void remove(Long id) throws BusinessException {
        Product t = new Product();
        t.setId(id);
        Product product = productMapper.selectByPrimaryKey(t);
        if (product.getStatus() != 0) {
            throw new BusinessException(BusinessCodeEnum.PRODUCT_STATUS_ERROR);
        } else {
            t.setStatus(1);
            productMapper.updateByPrimaryKeySelective(t);
        }
    }

    /**
     * 从回收站恢复数据
     *
     * @param id
     */
    @Override
    public void back(Long id) throws BusinessException {
        Product t = new Product();
        t.setId(id);
        Product product = productMapper.selectByPrimaryKey(t);
        if (product.getStatus() != 1) {
            throw new BusinessException(BusinessCodeEnum.PRODUCT_STATUS_ERROR);
        } else {
            t.setStatus(0);
            productMapper.updateByPrimaryKeySelective(t);
        }
    }

    /**
     * 物资审核
     *
     * @param id
     */
    @Override
    public void publish(Long id) throws BusinessException {
        Product t = new Product();
        t.setId(id);
        Product product = productMapper.selectByPrimaryKey(t);
        if (product.getStatus() != 2) {
            throw new BusinessException(BusinessCodeEnum.PRODUCT_STATUS_ERROR);
        } else {
            t.setStatus(0);
            productMapper.updateByPrimaryKeySelective(t);
        }
    }


}
