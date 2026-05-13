package cn.iocoder.yudao.module.wms.service.inventory.dto;

import cn.iocoder.yudao.module.wms.enums.inventory.WmsInventoryOrderTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * WMS 库存变更请求 DTO
 *
 * @author 芋道源码
 */
@Data
public class WmsInventoryChangeReqDTO {

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

    /**
     * 库存变更明细
     */
    private List<Item> items;

    /**
     * WMS 库存变更明细
     */
    @Data
    public static class Item {

        /**
         * SKU 编号
         */
        private Long skuId;
        /**
         * 仓库编号
         */
        private Long warehouseId;
        /**
         * 库区编号
         */
        private Long areaId;
        /**
         * 库存明细编号
         */
        private Long inventoryDetailId;
        /**
         * 变更数量
         */
        private BigDecimal quantity;

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
         * 金额（小计、非单价）
         */
        private BigDecimal amount;
        /**
         * 备注
         */
        private String remark;

    }

}
