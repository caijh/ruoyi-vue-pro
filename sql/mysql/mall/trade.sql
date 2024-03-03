drop table if exists `trade_config`;
CREATE TABLE `trade_config`
(
    `id`                             BIGINT     NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    -- ========== 售后相关 ==========
    `after_sale_refund_reasons`      TEXT       NOT NULL COMMENT '售后的退款理由',
    `after_sale_return_reasons`      TEXT       NOT NULL COMMENT '售后的退货理由',

    -- ========== 配送相关 ==========
    `delivery_express_free_enabled`  TINYINT(1) NOT NULL COMMENT '是否启用全场包邮',
    `delivery_express_free_price`    INT        NOT NULL COMMENT '全场包邮的最小金额，单位：分',
    `delivery_pick_up_enabled`       TINYINT(1) NOT NULL COMMENT '是否开启自提',

    -- ========== 分销相关 ==========
    `brokerage_enabled`              TINYINT(1) NOT NULL COMMENT '是否启用分佣',
    `brokerage_enabled_condition`    INT        NOT NULL COMMENT '分佣模式',
    `brokerage_bind_mode`            INT        NOT NULL COMMENT '分销关系绑定模式',
    `brokerage_poster_urls`          TEXT       NOT NULL COMMENT '分销海报图地址数组',
    `brokerage_first_percent`        INT        NOT NULL COMMENT '一级返佣比例',
    `brokerage_second_percent`       INT        NOT NULL COMMENT '二级返佣比例',
    `brokerage_withdraw_min_price`   INT        NOT NULL COMMENT '用户提现最低金额',
    `brokerage_withdraw_fee_percent` INT        NOT NULL COMMENT '用户提现手续费百分比',
    `brokerage_frozen_days`          INT        NOT NULL COMMENT '佣金冻结时间(天)',
    `brokerage_withdraw_types`       TEXT       NOT NULL COMMENT '提现方式',
    create_time               DATETIME COMMENT '创建时间',
    update_time               DATETIME COMMENT '最后更新时间',
    creator                   VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater                   VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted                   TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id                 BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 comment = '交易中心配置';


drop table if exists `trade_order`;
CREATE TABLE `trade_order`
(
    `id`                      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '订单编号，主键自增',
    `no`                      VARCHAR(255) NOT NULL COMMENT '订单流水号',
    `type`                    INT          NOT NULL COMMENT '订单类型',
    `terminal`                INT          NOT NULL COMMENT '订单来源',
    `user_id`                 BIGINT       NOT NULL COMMENT '用户编号',
    `user_ip`                 VARCHAR(255) NOT NULL COMMENT '用户 IP',
    `user_remark`             VARCHAR(255) NOT NULL COMMENT '用户备注',
    `status`                  INT          NOT NULL COMMENT '订单状态',
    `product_count`           INT          NOT NULL COMMENT '购买的商品数量',
    `finish_time`             DATETIME     NOT NULL COMMENT '订单完成时间',
    `cancel_time`             DATETIME     NOT NULL COMMENT '订单取消时间',
    `cancel_type`             INT          NOT NULL COMMENT '取消类型',
    `remark`                  VARCHAR(255) NOT NULL COMMENT '商家备注',
    `comment_status`          TINYINT(1)   NOT NULL COMMENT '是否评价',
    `brokerage_user_id`       BIGINT       NOT NULL COMMENT '推广人编号',
    `pay_order_id`            BIGINT       NOT NULL COMMENT '支付订单编号',
    `pay_status`              TINYINT(1)   NOT NULL COMMENT '是否已支付',
    `pay_time`                DATETIME     NOT NULL COMMENT '付款时间',
    `pay_channel_code`        VARCHAR(255) NOT NULL COMMENT '支付渠道',
    `total_price`             INT          NOT NULL COMMENT '商品原价，单位：分',
    `discount_price`          INT          NOT NULL COMMENT '优惠金额，单位：分',
    `delivery_price`          INT          NOT NULL COMMENT '运费金额，单位：分',
    `adjust_price`            INT          NOT NULL COMMENT '订单调价，单位：分',
    `pay_price`               INT          NOT NULL COMMENT '应付金额（总），单位：分',
    `delivery_type`           INT          NOT NULL COMMENT '配送方式',
    `logistics_id`            BIGINT       NOT NULL COMMENT '发货物流公司编号',
    `logistics_no`            VARCHAR(255) NOT NULL COMMENT '发货物流单号',
    `delivery_time`           DATETIME     NOT NULL COMMENT '发货时间',
    `receive_time`            DATETIME     NOT NULL COMMENT '收货时间',
    `receiver_name`           VARCHAR(255) NOT NULL COMMENT '收件人名称',
    `receiver_mobile`         VARCHAR(255) NOT NULL COMMENT '收件人手机',
    `receiver_area_id`        INT          NOT NULL COMMENT '收件人地区编号',
    `receiver_detail_address` VARCHAR(255) NOT NULL COMMENT '收件人详细地址',
    `pick_up_store_id`        BIGINT       NOT NULL COMMENT '自提门店编号',
    `pick_up_verify_code`     VARCHAR(255) NOT NULL COMMENT '自提核销码',
    `refund_status`           INT          NOT NULL COMMENT '售后状态',
    `refund_price`            INT          NOT NULL COMMENT '退款金额，单位：分',
    `coupon_id`               BIGINT       NOT NULL COMMENT '优惠劵编号',
    `coupon_price`            INT          NOT NULL COMMENT '优惠劵减免金额，单位：分',
    `use_point`               INT          NOT NULL COMMENT '使用的积分',
    `point_price`             INT          NOT NULL COMMENT '积分抵扣的金额，单位：分',
    `give_point`              INT          NOT NULL COMMENT '赠送的积分',
    `refund_point`            INT          NOT NULL COMMENT '退还的使用的积分',
    `vip_price`               INT          NOT NULL COMMENT 'VIP 减免金额，单位：分',
    `seckill_activity_id`     BIGINT       NOT NULL COMMENT '秒杀活动编号',
    `bargain_activity_id`     BIGINT       NOT NULL COMMENT '砍价活动编号',
    `bargain_record_id`       BIGINT       NOT NULL COMMENT '砍价记录编号',
    `combination_activity_id` BIGINT       NOT NULL COMMENT '拼团活动编号',
    `combination_head_id`     BIGINT       NOT NULL COMMENT '拼团团长编号',
    `combination_record_id`   BIGINT       NOT NULL COMMENT '拼团记录编号',
    create_time               DATETIME COMMENT '创建时间',
    update_time               DATETIME COMMENT '最后更新时间',
    creator                   VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater                   VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted                   TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id                 BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='交易订单';

drop table if exists `trade_after_sale`;
CREATE TABLE `trade_after_sale`
(
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '售后编号，主键自增',
    `no`                VARCHAR(255) NOT NULL COMMENT '售后单号',
    `status`            INT          NOT NULL COMMENT '退款状态',
    `way`               INT          NOT NULL COMMENT '售后方式',
    `type`              INT          NOT NULL COMMENT '售后类型',
    `user_id`           BIGINT       NOT NULL COMMENT '用户编号',
    `apply_reason`      VARCHAR(255) NOT NULL COMMENT '申请原因',
    `apply_description` TEXT         NOT NULL COMMENT '补充描述',
    `apply_pic_urls`    TEXT         NOT NULL COMMENT '补充凭证图片（JSON格式）',

    `order_id`          BIGINT       NOT NULL COMMENT '交易订单编号',
    `order_no`          VARCHAR(255) NOT NULL COMMENT '订单流水号',
    `order_item_id`     BIGINT       NOT NULL COMMENT '交易订单项编号',
    `spu_id`            BIGINT       NOT NULL COMMENT '商品SPU编号',
    `spu_name`          VARCHAR(255) NOT NULL COMMENT '商品SPU名称',
    `sku_id`            BIGINT       NOT NULL COMMENT '商品SKU编号',
    `properties`        TEXT         NOT NULL COMMENT '属性数组（JSON格式）',
    `pic_url`           VARCHAR(255) NOT NULL COMMENT '商品图片',
    `count`             INT          NOT NULL COMMENT '退货商品数量',

    `audit_time`        DATETIME     NOT NULL COMMENT '审批时间',
    `audit_user_id`     BIGINT       NOT NULL COMMENT '审批人',
    `audit_reason`      VARCHAR(255) NOT NULL COMMENT '审批备注（只有审批不通过才会填写）',

    `refund_price`      INT          NOT NULL COMMENT '退款金额，单位：分',
    `pay_refund_id`     BIGINT       NOT NULL COMMENT '支付退款编号',
    `refund_time`       DATETIME     NOT NULL COMMENT '退款时间',

    `logistics_id`      BIGINT       NOT NULL COMMENT '退货物流公司编号',
    `logistics_no`      VARCHAR(255) NOT NULL COMMENT '退货物流单号',
    `delivery_time`     DATETIME     NOT NULL COMMENT '退货时间',
    `receive_time`      DATETIME     NOT NULL COMMENT '收货时间',
    `receive_reason`    VARCHAR(255) NOT NULL COMMENT '收货备注（只有拒绝收货才会填写）',
    create_time         DATETIME COMMENT '创建时间',
    update_time         DATETIME COMMENT '最后更新时间',
    creator             VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater             VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted             TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id           BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='售后订单';

drop table if exists `trade_after_sale_log`;
CREATE TABLE `trade_after_sale_log`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `user_id`       BIGINT       NOT NULL COMMENT '用户编号',
    `user_type`     INT          NOT NULL COMMENT '用户类型',
    `after_sale_id` BIGINT       NOT NULL COMMENT '售后编号',
    `before_status` INT          NOT NULL COMMENT '操作前状态',
    `after_status`  INT          NOT NULL COMMENT '操作后状态',
    `operate_type`  INT          NOT NULL COMMENT '操作类型',
    `content`       VARCHAR(255) NOT NULL COMMENT '操作明细',
    create_time     DATETIME COMMENT '创建时间',
    update_time     DATETIME COMMENT '最后更新时间',
    creator         VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater         VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted         TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id       BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 comment = '交易售后日志';

drop table if exists `trade_delivery_express`;
CREATE TABLE `trade_delivery_express`
(
    `id`        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号，自增',
    `code`      VARCHAR(255) NOT NULL COMMENT '快递公司 code',
    `name`      VARCHAR(255) NOT NULL COMMENT '快递公司名称',
    `logo`      VARCHAR(255) NOT NULL COMMENT '快递公司 logo',
    `sort`      INT          NOT NULL COMMENT '排序',
    `status`    INT          NOT NULL COMMENT '状态',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='快递公司 DO';

drop table if exists `trade_delivery_express_template_charge`;
CREATE TABLE `trade_delivery_express_template_charge`
(
    `id`          BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号，自增',
    `template_id` BIGINT NOT NULL COMMENT '配送模板编号',
    `area_ids`    TEXT   NOT NULL COMMENT '配送区域编号列表',
    `charge_mode` INT    NOT NULL COMMENT '配送计费方式',
    `start_count` DOUBLE NOT NULL COMMENT '首件数量(件数,重量，或体积)',
    `start_price` INT    NOT NULL COMMENT '起步价，单位：分',
    `extra_count` DOUBLE NOT NULL COMMENT '续件数量(件, 重量，或体积)',
    `extra_price` INT    NOT NULL COMMENT '额外价，单位：分',
    create_time   DATETIME COMMENT '创建时间',
    update_time   DATETIME COMMENT '最后更新时间',
    creator       VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater       VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted       TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id     BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 comment = '快递运费模板计费配置';

drop table if exists `trade_delivery_express_template`;
CREATE TABLE `trade_delivery_express_template`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号，自增',
    `name`        VARCHAR(255) NOT NULL COMMENT '模板名称',
    `charge_mode` INT          NOT NULL COMMENT '配送计费方式',
    `sort`        INT          NOT NULL COMMENT '排序',
    create_time   DATETIME COMMENT '创建时间',
    update_time   DATETIME COMMENT '最后更新时间',
    creator       VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater       VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted       TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id     BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 comment = '快递运费模板';

drop table if exists `trade_delivery_express_template_free`;
CREATE TABLE `trade_delivery_express_template_free`
(
    `id`          BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    `template_id` BIGINT NOT NULL COMMENT '配送模板编号',
    `area_ids`    TEXT   NOT NULL COMMENT '配送区域编号列表',
    `free_price`  INT    NOT NULL COMMENT '包邮金额，单位：分',
    `free_count`  INT    NOT NULL COMMENT '包邮件数',
    create_time   DATETIME COMMENT '创建时间',
    update_time   DATETIME COMMENT '最后更新时间',
    creator       VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater       VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted       TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id     BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 comment ='快递运费模板包邮配置';

drop table if exists `trade_delivery_pick_up_store`;
CREATE TABLE `trade_delivery_pick_up_store`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `name`           VARCHAR(255) NOT NULL COMMENT '门店名称',
    `introduction`   TEXT         NOT NULL COMMENT '门店简介',
    `phone`          VARCHAR(255) NOT NULL COMMENT '门店手机',
    `area_id`        INT          NOT NULL COMMENT '区域编号',
    `detail_address` VARCHAR(255) NOT NULL COMMENT '门店详细地址',
    `logo`           VARCHAR(255) NOT NULL COMMENT '门店 logo',
    `opening_time`   TIME         NOT NULL COMMENT '营业开始时间',
    `closing_time`   TIME         NOT NULL COMMENT '营业结束时间',
    `latitude`       DOUBLE       NOT NULL COMMENT '纬度',
    `longitude`      DOUBLE       NOT NULL COMMENT '经度',
    `status`         INT          NOT NULL COMMENT '门店状态',
    create_time      DATETIME COMMENT '创建时间',
    update_time      DATETIME COMMENT '最后更新时间',
    creator          VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater          VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted          TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id        BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='自提门店';

drop table if exists `trade_delivery_pick_up_store_staff`;
CREATE TABLE `trade_delivery_pick_up_store_staff`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号，自增',
    `store_id`      bigint(20) DEFAULT NULL COMMENT '自提门店编号',
    `admin_user_id` bigint(20) DEFAULT NULL COMMENT '管理员用户id',
    `status`        int(11)    DEFAULT NULL COMMENT '状态',
    create_time     DATETIME COMMENT '创建时间',
    update_time     DATETIME COMMENT '最后更新时间',
    creator         VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater         VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted         TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id       BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='自提门店店员';

drop table if exists `trade_order_item`;
CREATE TABLE `trade_order_item`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
    `user_id`           bigint(20)   DEFAULT NULL COMMENT '用户编号',
    `order_id`          bigint(20)   DEFAULT NULL COMMENT '订单编号',
    `cart_id`           bigint(20)   DEFAULT NULL COMMENT '购物车项编号',
    `spu_id`            bigint(20)   DEFAULT NULL COMMENT '商品 SPU 编号',
    `spu_name`          varchar(255) DEFAULT NULL COMMENT '商品 SPU 名称',
    `sku_id`            bigint(20)   DEFAULT NULL COMMENT '商品 SKU 编号',
    `properties`        json         DEFAULT NULL COMMENT '属性数组，JSON 格式',
    `pic_url`           varchar(255) DEFAULT NULL COMMENT '商品图片',
    `count`             int(11)      DEFAULT NULL COMMENT '购买数量',
    `comment_status`    tinyint(1)   DEFAULT NULL COMMENT '是否评价',
    `price`             int(11)      DEFAULT NULL COMMENT '商品原价（单），单位：分',
    `discount_price`    int(11)      DEFAULT NULL COMMENT '优惠金额（总），单位：分',
    `delivery_price`    int(11)      DEFAULT NULL COMMENT '运费金额（总），单位：分',
    `adjust_price`      int(11)      DEFAULT NULL COMMENT '订单调价（总），单位：分',
    `pay_price`         int(11)      DEFAULT NULL COMMENT '应付金额（总），单位：分',
    `coupon_price`      int(11)      DEFAULT NULL COMMENT '优惠劵减免金额，单位：分',
    `point_price`       int(11)      DEFAULT NULL COMMENT '积分抵扣的金额，单位：分',
    `use_point`         int(11)      DEFAULT NULL COMMENT '使用的积分',
    `give_point`        int(11)      DEFAULT NULL COMMENT '赠送的积分',
    `vip_price`         int(11)      DEFAULT NULL COMMENT 'VIP 减免金额，单位：分',
    `after_sale_id`     bigint(20)   DEFAULT NULL COMMENT '售后单编号',
    `after_sale_status` int(11)      DEFAULT NULL COMMENT '售后状态',
    create_time         DATETIME COMMENT '创建时间',
    update_time         DATETIME COMMENT '最后更新时间',
    creator             VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater             VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted             TINYINT(1)   DEFAULT 0 COMMENT '是否删除',
    tenant_id           BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='交易订单项 DO';

DROP TABLE IF EXISTS `trade_brokerage_user`;
CREATE TABLE IF NOT EXISTS `trade_brokerage_user`
(
    `id`                bigint   NOT NULL AUTO_INCREMENT,
    `bind_user_id`      bigint   NOT NULL,
    `bind_user_time`    varchar(255),
    `brokerage_enabled` bit(1)      NOT NULL,
    `brokerage_time`    varchar(255),
    `price`             int      NOT NULL,
    `frozen_price`      int      NOT NULL,
    `creator`           varchar(255)           DEFAULT '',
    `create_time`       datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updater`           varchar(255),
    `update_time`       datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`           bit      NOT NULL DEFAULT FALSE,
    `tenant_id`         bigint   NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) COMMENT '分销用户';


DROP TABLE IF EXISTS `trade_brokerage_record`;
CREATE TABLE IF NOT EXISTS `trade_brokerage_record`
(
    `id`            int      NOT NULL AUTO_INCREMENT,
    `user_id`       bigint   NOT NULL,
    `biz_id`        varchar(255)  NOT NULL,
    `biz_type`      varchar(255)  NOT NULL,
    `title`         varchar(255)  NOT NULL,
    `price`         int      NOT NULL,
    `total_price`   int      NOT NULL,
    `description`   varchar(255)  NOT NULL,
    `status`        varchar(255)  NOT NULL,
    `frozen_days`   int      NOT NULL,
    `unfreeze_time` varchar(255),
    `creator`       varchar(255)           DEFAULT '',
    `create_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updater`       varchar(255)           DEFAULT '',
    `update_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`       bit      NOT NULL DEFAULT FALSE,
    `tenant_id`      bigint   NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) COMMENT '佣金记录';

DROP TABLE IF EXISTS `trade_brokerage_withdraw`;
CREATE TABLE IF NOT EXISTS `trade_brokerage_withdraw`
(
    `id`                  int      NOT NULL AUTO_INCREMENT,
    `user_id`             bigint   NOT NULL,
    `price`               int      NOT NULL,
    `fee_price`           int      NOT NULL,
    `total_price`         int      NOT NULL,
    `type`                varchar(255)  NOT NULL,
    `name`                varchar(255),
    `account_no`          varchar(255),
    `bank_name`           varchar(255),
    `bank_address`        varchar(255),
    `account_qr_code_url` varchar(255),
    `status`              varchar(255)  NOT NULL,
    `audit_reason`        varchar(255),
    `audit_time`          varchar(255),
    `remark`              varchar(255),
    `creator`             varchar(255)           DEFAULT '',
    `create_time`         datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updater`             varchar(255)           DEFAULT '',
    `update_time`         datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`             bit      NOT NULL DEFAULT FALSE,
    `tenant_id`      bigint   NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) COMMENT '佣金提现';

DROP TABLE IF EXISTS `trade_cart`;
CREATE TABLE `trade_cart`
(
    `id`          bigint   NOT NULL AUTO_INCREMENT,
    `user_id`     bigint   NOT NULL,
    `spu_id`      bigint   NOT NULL,
    `sku_id`      bigint   NOT NULL,
    `count`       int      NOT NULL,
    `selected`    bit      NOT NULL,
    `creator`             varchar(255)           DEFAULT '',
    `create_time`         datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updater`             varchar(255)           DEFAULT '',
    `update_time`         datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`             bit      NOT NULL DEFAULT FALSE,
    `tenant_id`      bigint   NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='购物车的商品信息';

