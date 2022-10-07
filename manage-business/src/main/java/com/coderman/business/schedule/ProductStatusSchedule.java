package com.coderman.business.schedule;

import com.coderman.business.service.ProductService;
import com.coderman.common.enums.buisiness.ProductStatusEnum;
import com.coderman.common.model.business.Product;
import com.coderman.common.utils.ConstantUtil;
import com.coderman.common.utils.DateUtil;
import com.coderman.common.vo.business.ProductVO;
import com.coderman.common.vo.system.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
@Component
@Slf4j
public class ProductStatusSchedule {

    @Autowired
    private ProductService productService;

    /**
     * 目前没有考虑大数据情况
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void calculationState() {
        log.info("开启定时任务修改状态：");
        List<Product> list = new ArrayList<>();
        ProductVO productVO = new ProductVO();
        productVO.setStatus(ProductStatusEnum.ENABLE.getStatus());
        int pn = 1;
        while (true) {

            PageVO<ProductVO> productList = productService.findProductList(pn, ConstantUtil.PAGE_SIZE, productVO);
            List<ProductVO> rows = productList.getRows();
            if (CollectionUtils.isEmpty(rows)) {
                break;
            }
            pn++;
            rows.stream().forEach(row -> {
                Product product = new Product();
                BeanUtils.copyProperties(row, product);
                getStatuesByDay(product, list);
            });
        }
        if (!CollectionUtils.isEmpty(list)) {
            productService.editBatch(list);
        }
    }

    private void getStatuesByDay(Product product, List<Product> list) {
        Long day = DateUtil.getDateCalculatedDay(product.getExpectTime());
        if (ProductStatusEnum.HALF_YEAR.getStatus().equals(product.getStatus()) && day < ConstantUtil.THREE_MONTHS) {
            product.setStatus(ProductStatusEnum.THREE_MONTHS.getStatus());
            list.add(product);
            return;
        }

        if (ProductStatusEnum.THREE_MONTHS.getStatus().equals(product.getStatus()) && day < ConstantUtil.ONE_MONTHS) {
            product.setStatus(ProductStatusEnum.ONE_MONTHS.getStatus());
            list.add(product);
            return;
        }

        if (ProductStatusEnum.ONE_MONTHS.getStatus().equals(product.getStatus()) && day < 0) {
            product.setStatus(ProductStatusEnum.EXPIRED.getStatus());
            list.add(product);
            return;
        }
        if (ProductStatusEnum.ENABLE.getStatus().equals(product.getStatus()) && day < ConstantUtil.HALF_YEAR) {
            product.setStatus(ProductStatusEnum.HALF_YEAR.getStatus());
            list.add(product);
        }
    }
}
