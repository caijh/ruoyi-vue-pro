drop table if exists `product_category`;
CREATE TABLE `product_category`
(
    `id`        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类编号',
    `parent_id` BIGINT       NOT NULL COMMENT '父分类编号',
    `name`      VARCHAR(255) NOT NULL COMMENT '分类名称',
    `pic_url`   VARCHAR(255) NOT NULL COMMENT '移动端分类图',
    `sort`      INT          NOT NULL COMMENT '分类排序',
    `status`    INT          NOT NULL COMMENT '开启状态',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品分类 DO';

drop table if exists `product_brand`;
CREATE TABLE `product_brand`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '品牌编号',
    `name`        VARCHAR(255) NOT NULL COMMENT '品牌名称',
    `pic_url`     VARCHAR(255) NOT NULL COMMENT '品牌图片',
    `sort`        INT          NOT NULL COMMENT '品牌排序',
    `description` TEXT         NOT NULL COMMENT '品牌描述',
    `status`      INT          NOT NULL COMMENT '状态',
    create_time   datetime comment '创建时间',
    update_time   datetime comment '最后更新时间',
    creator       varchar(255) comment '创建者，目前使用 SysUser 的 id 编号',
    updater       varchar(255) comment '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品品牌 DO';

drop table if exists `product_comment`;
CREATE TABLE `product_comment`
(
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '评论编号，主键自增',
    `user_id`            BIGINT       NOT NULL COMMENT '评价人的用户编号',
    `user_nickname`      VARCHAR(255) NOT NULL COMMENT '评价人名称',
    `user_avatar`        VARCHAR(255) NOT NULL COMMENT '评价人头像',
    `anonymous`          TINYINT(1)   NOT NULL COMMENT '是否匿名',
    `order_id`           BIGINT       NOT NULL COMMENT '交易订单编号',
    `order_item_id`      BIGINT       NOT NULL COMMENT '交易订单项编号',
    `spu_id`             BIGINT       NOT NULL COMMENT '商品 SPU 编号',
    `spu_name`           VARCHAR(255) NOT NULL COMMENT '商品 SPU 名称',
    `sku_id`             BIGINT       NOT NULL COMMENT '商品 SKU 编号',
    `sku_pic_url`        VARCHAR(255) NOT NULL COMMENT '商品 SKU 图片地址',
    `sku_properties`     JSON         NOT NULL COMMENT '属性数组，JSON 格式',
    `visible`            TINYINT(1)   NOT NULL COMMENT '是否可见',
    `scores`             INT          NOT NULL COMMENT '评分星级',
    `description_scores` INT          NOT NULL COMMENT '描述星级',
    `benefit_scores`     INT          NOT NULL COMMENT '服务星级',
    `content`            TEXT         NOT NULL COMMENT '评论内容',
    `pic_urls`           JSON         NOT NULL COMMENT '评论图片地址数组',
    `reply_status`       TINYINT(1)   NOT NULL COMMENT '商家是否回复',
    `reply_user_id`      BIGINT       NOT NULL COMMENT '回复管理员编号',
    `reply_content`      TEXT         NOT NULL COMMENT '商家回复内容',
    `reply_time`         DATETIME     NOT NULL COMMENT '商家回复时间',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品评论 DO';

drop table if exists `product_favorite`;
CREATE TABLE `product_favorite`
(
    `id`        BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号，主键自增',
    `user_id`   BIGINT NOT NULL COMMENT '用户编号',
    `spu_id`    BIGINT NOT NULL COMMENT '商品 SPU 编号',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品收藏';

drop table if exists `product_browse_history`;
CREATE TABLE `product_browse_history`
(
    `id`           BIGINT     NOT NULL AUTO_INCREMENT COMMENT '记录编号',
    `spu_id`       BIGINT     NOT NULL COMMENT '商品 SPU 编号',
    `user_id`      BIGINT     NOT NULL COMMENT '用户编号',
    `user_deleted` TINYINT(1) NOT NULL COMMENT '用户是否删除',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品浏览记录';

drop table if exists `product_property`;
CREATE TABLE `product_property`
(
    `id`        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`      VARCHAR(255) NOT NULL COMMENT '名称',
    `status`    INT          NOT NULL COMMENT '状态',
    `remark`    VARCHAR(255) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品属性项';

drop table if exists `product_property_value`;
CREATE TABLE `product_property_value`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `property_id` BIGINT       NOT NULL COMMENT '属性项的编号',
    `name`        VARCHAR(255) NOT NULL COMMENT '名称',
    `remark`      VARCHAR(255) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品属性值';

drop table if exists `product_sku`;
CREATE TABLE `product_sku`
(
    `id`                     BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品 SKU 编号，自增',
    `spu_id`                 BIGINT NOT NULL COMMENT 'SPU 编号',
    `properties`             JSON COMMENT '属性数组，JSON 格式',
    `price`                  INT    NOT NULL COMMENT '商品价格，单位：分',
    `market_price`           INT COMMENT '市场价，单位：分',
    `cost_price`             INT COMMENT '成本价，单位：分',
    `bar_code`               VARCHAR(255) COMMENT '商品条码',
    `pic_url`                VARCHAR(255) COMMENT '图片地址',
    `stock`                  INT COMMENT '库存',
    `weight`                 DOUBLE COMMENT '商品重量，单位：kg 千克',
    `volume`                 DOUBLE COMMENT '商品体积，单位：m^3 平米',
    `first_brokerage_price`  INT COMMENT '一级分销的佣金，单位：分',
    `second_brokerage_price` INT COMMENT '二级分销的佣金，单位：分',
    `sales_count`            INT COMMENT '商品销量',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品 SKU';

drop table if exists `product_spu`;
CREATE TABLE `product_spu`
(
    `id`                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '商品 SPU 编号，自增',
    `name`                 VARCHAR(255) NOT NULL COMMENT '商品名称',
    `keyword`              VARCHAR(255) NOT NULL COMMENT '关键字',
    `introduction`         VARCHAR(255) NOT NULL COMMENT '商品简介',
    `description`          TEXT         NOT NULL COMMENT '商品详情',
    `category_id`          BIGINT       NOT NULL COMMENT '商品分类编号',
    `brand_id`             BIGINT       NOT NULL COMMENT '商品品牌编号',
    `pic_url`              VARCHAR(255) NOT NULL COMMENT '商品封面图',
    `slider_pic_urls`      JSON         NOT NULL COMMENT '商品轮播图',
    `sort`                 INT          NOT NULL COMMENT '排序字段',
    `status`               INT          NOT NULL COMMENT '商品状态',
    `spec_type`            TINYINT(1)   NOT NULL COMMENT '规格类型',
    `price`                INT          NOT NULL COMMENT '商品价格，单位使用：分',
    `market_price`         INT          NOT NULL COMMENT '市场价，单位使用：分',
    `cost_price`           INT          NOT NULL COMMENT '成本价，单位使用：分',
    `stock`                INT          NOT NULL COMMENT '库存',
    `delivery_types`       JSON         NOT NULL COMMENT '配送方式数组',
    `delivery_template_id` BIGINT       NOT NULL COMMENT '物流配置模板编号',
    `give_integral`        INT          NOT NULL COMMENT '赠送积分',
    `sub_commission_type`  TINYINT(1)   NOT NULL COMMENT '分销类型',
    `sales_count`          INT          NOT NULL COMMENT '商品销量',
    `virtual_sales_count`  INT          NOT NULL COMMENT '虚拟销量',
    `browse_count`         INT          NOT NULL COMMENT '浏览量',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品 SPU';
