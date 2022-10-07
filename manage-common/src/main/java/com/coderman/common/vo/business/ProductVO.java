package com.coderman.common.vo.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author zhangyukang
 * @Date 2020/3/17 09:16
 * @Version 1.0
 **/
@Data
public class ProductVO {

    private Long id;

    /**
     * 设备名称
     */
    @NotBlank
    private String name;

    /**
     * 设备类型
     */
    private String model;

    /**
     * 设备进度
     */
    private String plan;

    /**
     * 总包
     */
    @NotBlank
    private String unit;

    /**
     * 分包
     */
    @NotBlank
    private String remark;

    /**
     * 负责人
     */
    @NotBlank
    private String head;

    /**
     * 连续人电话
     */
    @NotBlank
    private String mobilePhone;

    /**
     * 制造厂家
     */
    private String manufacturer;

    /**
     * 制造环节
     */
    private String manufacturing;


    /**
     * 运输周期
     */
    private String period;

    /**
     * 是否包含进口物资
     */
    private String importGoods;

    /**
     * 是否满足到货需求
     */
    private String meetDemand;

    /**
     * 未到货原因
     */
    private String goodsReason;

    /**
     * 图纸变更
     */
    private String drawingChanges;

    /**
     * 图纸变更
     */
    private String changesReason;
    /**
     * 图纸变更
     */
    private String drawingReview;
    /**
     * 催交任务完成情况
     */
    private String completion;

    /**
     * 目前状态
     */
    private String currentState;


    /**
     * 付款进度
     */
    private String paymentSchedule;

    /**
     * 备注
     */
    private String comment;


    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date createTime;

    /**
     * 实际到货时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date actualTime;
    /**
     * 开始发货日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date arrivalTime;

    /**
     * 预计到货日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date expectTime;

    /**
     * 到货时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date shippingTime;

    /**
     * 预计完工日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date completionTime;

    /**
     * 进入生产工序时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date productionTime;

    /**
     * 预计图纸收到日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date expectedTime;

    /**
     * 技术协议签署日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date agreementTime;

    /**
     * 合同签署日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date signingTime;

    /**
     * 催交任务下达日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date issuedTime;

    /**
     * 催交任务下达日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date requirementsTime;


    /**
     * 分级
     */
    private Long[] categoryKeys;

    private Long oneCategoryId;

    private Long twoCategoryId;

    private Long threeCategoryId;

    /**
     * 用于排序的字段
     */
    private String filter;
    private String filterType;

    /**
     * 状态
     */
    private Integer status;//是否已经进入回收站:1:逻辑删除,0:正常数据,2:添加待审核

}
