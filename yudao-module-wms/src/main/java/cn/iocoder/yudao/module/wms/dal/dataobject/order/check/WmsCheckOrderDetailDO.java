package cn.iocoder.yudao.module.wms.dal.dataobject.order.check;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemSkuDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse.WmsWarehouseAreaDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse.WmsWarehouseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * WMS 盘库单明细 DO
 *
 * @author 芋道源码
 */
@TableName("wms_check_order_detail")
@KeySequence("wms_check_order_detail_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WmsCheckOrderDetailDO extends BaseDO {

    /**
     * 主键编号
     */
    @TableId
    private Long id;
    /**
     * 备注
     */
    private String remark;

    // ========= 单据商品字段 =========

    /**
     * 盘库单编号
     *
     * 关联 {@link WmsCheckOrderDO#getId()}
     */
    private Long orderId;
    /**
     * 商品 SKU 编号
     *
     * 关联 {@link WmsItemSkuDO#getId()}
     */
    private Long skuId;

    // ========= 仓库库区字段 =========

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
     * 库存编号
     *
     * 关联 {@link WmsInventoryDO#getId()}
     */
    private Long inventoryId;

    // ========= 批次效期字段 =========

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
    /**
     * 入库时间
     */
    private LocalDateTime receiptTime;

    // ========= 汇总金额字段 =========

    /**
     * 账面数量
     */
    private BigDecimal quantity;
    /**
     * 实盘数量
     */
    private BigDecimal checkQuantity;
    /**
     * 金额（小计、非单价）
     */
    private BigDecimal amount;

}
