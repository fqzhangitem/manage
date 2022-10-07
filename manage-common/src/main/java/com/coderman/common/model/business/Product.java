package com.coderman.common.model.business;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "biz_product")
public class Product {

    @Id
    private Long id;

    /**
     * 设备名称
     */
    private String name;


    /**
     * 设备进度
     */
    private String plan;

    /**
     * 总包
     */
    private String unit;

    /**
     * 分包
     */
    private String remark;

    /**
     * 负责人
     */
    private String head;

    /**
     * 连续人电话
     */
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
    private Date createTime;

    /**
     * 实际到货时间
     */
    private Date actualTime;
    /**
     * 开始发货日期
     */
    private Date arrivalTime;

    /**
     * 预计到货日期
     */
    private Date expectTime;

    /**
     * 到货时间
     */
    private Date shippingTime;

    /**
     * 预计完工日期
     */
    private Date completionTime;

    /**
     * 进入生产工序时间
     */
    private Date productionTime;

    /**
     * 预计图纸收到日期
     */
    private Date expectedTime;

    /**
     * 技术协议签署日期
     */
    private Date agreementTime;

    /**
     * 合同签署日期
     */
    private Date signingTime;

    /**
     * 催交任务下达日期
     */
    private Date issuedTime;

    /**
     * 催交任务下达日期
     */
    private Date requirementsTime;



    private Long oneCategoryId;

    private Long twoCategoryId;

    private Long threeCategoryId;

    private Integer status;
}
