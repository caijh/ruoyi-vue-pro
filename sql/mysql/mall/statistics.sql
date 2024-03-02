drop table if exists product_statistics;
CREATE TABLE `product_statistics`
(
    `id`                      BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号，主键自增',
    `time`                    DATE   NOT NULL COMMENT '统计日期',
    `spu_id`                  BIGINT NOT NULL COMMENT '商品 SPU 编号',
    `browse_count`            INT    NOT NULL COMMENT '浏览量',
    `browse_user_count`       INT    NOT NULL COMMENT '访客量',
    `favorite_count`          INT    NOT NULL COMMENT '收藏数量',
    `cart_count`              INT    NOT NULL COMMENT '加购数量',
    `order_count`             INT    NOT NULL COMMENT '下单件数',
    `order_pay_count`         INT    NOT NULL COMMENT '支付件数',
    `order_pay_price`         INT    NOT NULL COMMENT '支付金额，单位：分',
    `after_sale_count`        INT    NOT NULL COMMENT '退款件数',
    `after_sale_refund_price` INT    NOT NULL COMMENT '退款金额，单位：分',
    `browse_convert_percent`  INT    NOT NULL COMMENT '访客支付转化率（百分比）',
    create_time               DATETIME COMMENT '创建时间',
    update_time               DATETIME COMMENT '最后更新时间',
    creator                   VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater                   VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted                   TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id                 BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品统计';

drop table if exists trade_statistics;
CREATE TABLE `trade_statistics`
(
    `id`                         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '编号，主键自增',
    `time`                       DATETIME NOT NULL COMMENT '统计日期',
    `order_create_count`         INT      NOT NULL COMMENT '创建订单数',
    `order_pay_count`            INT      NOT NULL COMMENT '支付订单商品数',
    `order_pay_price`            INT      NOT NULL COMMENT '总支付金额，单位：分',
    `after_sale_count`           INT      NOT NULL COMMENT '退款订单数',
    `after_sale_refund_price`    INT      NOT NULL COMMENT '总退款金额，单位：分',
    `brokerage_settlement_price` INT      NOT NULL COMMENT '佣金金额（已结算），单位：分',
    `wallet_pay_price`           INT      NOT NULL COMMENT '总支付金额（余额），单位：分',
    `recharge_pay_count`         INT      NOT NULL COMMENT '充值订单数',
    `recharge_pay_price`         INT      NOT NULL COMMENT '充值金额，单位：分',
    `recharge_refund_count`      INT      NOT NULL COMMENT '充值退款订单数',
    `recharge_refund_price`      INT      NOT NULL COMMENT '充值退款金额，单位：分',
    create_time                  DATETIME COMMENT '创建时间',
    update_time                  DATETIME COMMENT '最后更新时间',
    creator                      VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater                      VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted                      TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id                    BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='交易统计';
