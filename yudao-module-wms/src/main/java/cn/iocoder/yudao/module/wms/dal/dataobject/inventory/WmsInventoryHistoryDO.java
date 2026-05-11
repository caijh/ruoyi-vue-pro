package cn.iocoder.yudao.module.wms.dal.dataobject.inventory;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemSkuDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse.WmsWarehouseAreaDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse.WmsWarehouseDO;
import cn.iocoder.yudao.module.wms.enums.inventory.WmsInventoryOrderTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * WMS 库存流水 DO
 *
 * @author 芋道源码
 */
@TableName("wms_inventory_history")
@KeySequence("wms_inventory_history_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WmsInventoryHistoryDO extends BaseDO {

    /**
     * 主键编号
     */
    @TableId
    private Long id;

    // ========= 库存维度相关字段 =========

    /**
     * 仓库编号
     *
     * 关联 {@link WmsWarehouseDO#getId()}
     */
    private Long warehouseId;
    /**
     * 库区编号
     *
     * 关联 {@link WmsWarehouseAreaDO#getId()}
     */
    private Long areaId;
    /**
     * 商品 SKU 编号
     *
     * 关联 {@link WmsItemSkuDO#getId()}
     */
    private Long skuId;
    /**
     * 库存变化数量
     */
    private BigDecimal quantity;
    /**
     * 变化前库存数量
     */
    private BigDecimal beforeQuantity;
    /**
     * 变化后库存数量
     */
    private BigDecimal afterQuantity;

    // ========= 批次效期相关字段 =========

    /**
     * 批号
     */
    private String batchNo;
    /**
     * 生产日期
     */
    private LocalDateTime productionDate;
    /**
     * 过期日期
     */
    private LocalDateTime expirationDate;

    // ========= 金额备注相关字段 =========

    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 备注
     */
    private String remark;

    // ========= 来源单据相关字段 =========

    /**
     * 操作单编号
     */
    private Long orderId;
    /**
     * 操作单号
     */
    private String orderNo;
    /**
     * 操作类型
     *
     * 枚举 {@link WmsInventoryOrderTypeEnum#getType()}
     */
    private Integer orderType;

}
