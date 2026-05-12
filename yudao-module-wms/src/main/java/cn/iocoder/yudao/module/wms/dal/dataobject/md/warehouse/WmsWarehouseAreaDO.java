package cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * WMS 库区 DO
 *
 * @author 芋道源码
 */
@TableName("wms_warehouse_area")
@KeySequence("wms_warehouse_area_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WmsWarehouseAreaDO extends BaseDO {

    /**
     * 空库区编号
     */
    public static final Long ID_EMPTY = 0L;

    /**
     * 主键编号
     */
    @TableId
    private Long id;
    /**
     * 库区编号
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 所属仓库编号
     *
     * 关联 {@link WmsWarehouseDO#getId()}
     */
    private Long warehouseId;
    /**
     * 备注
     */
    private String remark;

}
