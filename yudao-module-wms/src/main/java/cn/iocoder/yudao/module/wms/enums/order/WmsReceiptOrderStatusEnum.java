package cn.iocoder.yudao.module.wms.enums.order;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * WMS 入库单状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum WmsReceiptOrderStatusEnum implements ArrayValuable<Integer> {

    CANCELED(-1, "作废"),
    PENDING(0, "暂存"),
    COMPLETED(1, "完成入库");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(WmsReceiptOrderStatusEnum::getStatus).toArray(Integer[]::new);

    /**
     * 状态
     */
    private final Integer status;
    /**
     * 名称
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
