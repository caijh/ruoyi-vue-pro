package cn.iocoder.yudao.module.wms.dal.dataobject.order.movement;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse.WmsWarehouseAreaDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse.WmsWarehouseDO;
import cn.iocoder.yudao.module.wms.enums.DictTypeConstants;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * WMS 移库单 DO
 *
 * @author 芋道源码
 */
@TableName("wms_movement_order")
@KeySequence("wms_movement_order_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WmsMovementOrderDO extends BaseDO {

    /**
     * 主键编号
     */
    @TableId
    private Long id;
    /**
     * 移库单号
     */
    private String no;
    /**
     * 移库状态
     *
     * 字典 {@link DictTypeConstants#ORDER_STATUS}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

    // ========= 来源仓库库区字段 =========

    /**
     * 来源仓库编号
     *
     * 关联 {@link WmsWarehouseDO#getId()}
     */
    private Long sourceWarehouseId;
    /**
     * 来源库区编号
     *
     * 关联 {@link WmsWarehouseAreaDO#getId()}
     */
    private Long sourceAreaId;

    // ========= 目标仓库库区字段 =========

    /**
     * 目标仓库编号
     *
     * 关联 {@link WmsWarehouseDO#getId()}
     */
    private Long targetWarehouseId;
    /**
     * 目标库区编号
     *
     * 关联 {@link WmsWarehouseAreaDO#getId()}
     */
    private Long targetAreaId;

    // ========= 汇总金额字段 =========

    /**
     * 总数量
     */
    private BigDecimal totalQuantity;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;

}
