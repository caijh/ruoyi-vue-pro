package cn.iocoder.yudao.module.wms.dal.dataobject.order.receipt;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
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
 * WMS 入库单明细 DO
 *
 * @author 芋道源码
 */
@TableName("wms_receipt_order_detail")
@KeySequence("wms_receipt_order_detail_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WmsReceiptOrderDetailDO extends BaseDO {

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
     * 入库单编号
     *
     * 关联 {@link WmsReceiptOrderDO#getId()}
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

    // ========= 汇总金额字段 =========

    /**
     * 入库数量
     */
    private BigDecimal quantity;
    /**
     * 金额（小计、非单价）
     */
    private BigDecimal amount;

}
