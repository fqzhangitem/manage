package com.coderman.common.enums.buisiness;

import com.coderman.common.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author
 */

public enum ProductStatusEnum {
    DELETE(1, "删除"),
    ENABLE(0, "全部"),
    EXPIRED(2, "已到期"),
    THREE_MONTHS(3, "三个月内"),
    HALF_YEAR(4, "半年内"),
    ONE_MONTHS(5, "一个月内"),
    ;

    private Integer status;

    private String desc;

    ProductStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static List<Integer> getStatusAll() {
        List<Integer> list = new ArrayList<>();
        Arrays.stream(ProductStatusEnum.values()).forEach(state -> {
            if (!DELETE.getStatus().equals(state.getStatus())) {
                list.add(state.getStatus());
            }
        });
        return list;
    }
}
