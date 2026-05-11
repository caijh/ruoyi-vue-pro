package cn.iocoder.yudao.module.wms.enums.inventory;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * WMS 库存操作类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum WmsInventoryOrderTypeEnum implements ArrayValuable<Integer> {

    RECEIPT(1, "入库"),
    SHIPMENT(2, "出库"),
    MOVEMENT(3, "移库"),
    CHECK(4, "盘库");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(WmsInventoryOrderTypeEnum::getType).toArray(Integer[]::new);

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 名称
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
