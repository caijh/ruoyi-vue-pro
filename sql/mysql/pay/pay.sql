drop table if exists pay_app;
CREATE TABLE pay_app
(
    id                  BIGINT PRIMARY KEY COMMENT '应用编号',
    name                VARCHAR(255) COMMENT '应用名',
    status              INT COMMENT '状态',
    remark              VARCHAR(255) COMMENT '备注',
    order_notify_url    VARCHAR(255) COMMENT '支付结果的回调地址',
    refund_notify_url   VARCHAR(255) COMMENT '退款结果的回调地址',
    transfer_notify_url VARCHAR(255) COMMENT '转账结果的回调地址',
    create_time         datetime comment '创建时间',
    update_time         datetime comment '最后更新时间',
    creator             varchar(255) comment '创建者，目前使用 SysUser 的 id 编号',
    updater             varchar(255) comment '更新者，目前使用 SysUser 的 id 编号',
    deleted             tinyint(1) default 0 comment '是否删除',
    tenant_id           BIGINT comment '租户id'
) comment = '支付应用';

drop table if exists pay_channel;
CREATE TABLE pay_channel
(
    id          BIGINT PRIMARY KEY COMMENT '渠道编号，数据库自增',
    code        VARCHAR(255) COMMENT '渠道编码',
    status      INT COMMENT '状态',
    fee_rate    DOUBLE COMMENT '渠道费率，单位：百分比',
    remark      VARCHAR(255) COMMENT '备注',
    app_id      BIGINT COMMENT '应用编号',
    config      JSON COMMENT '支付渠道配置',
    create_time datetime comment '创建时间',
    update_time datetime comment '最后更新时间',
    creator     varchar(255) comment '创建者，目前使用 SysUser 的 id 编号',
    updater     varchar(255) comment '更新者，目前使用 SysUser 的 id 编号',
    deleted     tinyint(1) default 0 comment '是否删除',
    tenant_id   BIGINT comment '租户id'

) comment = '支付渠道';

drop table if exists pay_notify_log;
CREATE TABLE pay_notify_log
(
    id           BIGINT PRIMARY KEY COMMENT '日志编号，自增',
    task_id      BIGINT COMMENT '通知任务编号',
    notify_times INT COMMENT '第几次被通知',
    response     TEXT COMMENT 'HTTP 响应结果',
    status       INT COMMENT '支付通知状态',
    create_time  datetime comment '创建时间',
    update_time  datetime comment '最后更新时间',
    creator      varchar(255) comment '创建者，目前使用 SysUser 的 id 编号',
    updater      varchar(255) comment '更新者，目前使用 SysUser 的 id 编号',
    deleted      tinyint(1) default 0 comment '是否删除',
    tenant_id    BIGINT comment '租户id'
) comment = '商户支付、退款等的通知 Log';

drop table if exists pay_notify_task;
CREATE TABLE pay_notify_task
(
    id                   BIGINT PRIMARY KEY COMMENT '编号，自增',
    app_id               BIGINT COMMENT '应用编号',
    type                 INT COMMENT '通知类型',
    data_id              BIGINT COMMENT '数据编号，根据不同 type 进行关联',
    merchant_order_id    VARCHAR(255) COMMENT '商户订单编号',
    merchant_transfer_id VARCHAR(255) COMMENT '商户转账单编号',
    status               INT COMMENT '通知状态',
    next_notify_time     DATETIME COMMENT '下一次通知时间',
    last_execute_time    DATETIME COMMENT '最后一次执行时间',
    notify_times         INT COMMENT '当前通知次数',
    max_notify_times     INT COMMENT '最大可通知次数',
    notify_url           VARCHAR(255) COMMENT '通知地址',
    create_time          datetime comment '创建时间',
    update_time          datetime comment '最后更新时间',
    creator              varchar(255) comment '创建者，目前使用 SysUser 的 id 编号',
    updater              varchar(255) comment '更新者，目前使用 SysUser 的 id 编号',
    deleted              tinyint(1) default 0 comment '是否删除',
    tenant_id            BIGINT comment '租户id'
) comment = '支付通知';

drop table if exists pay_order;
CREATE TABLE pay_order
(
    id                BIGINT PRIMARY KEY COMMENT '订单编号，数据库自增',
    app_id            BIGINT COMMENT '应用编号',
    channel_id        BIGINT COMMENT '渠道编号',
    channel_code      VARCHAR(255) COMMENT '渠道编码',
    merchant_order_id VARCHAR(255) COMMENT '商户订单编号',
    subject           VARCHAR(255) COMMENT '商品标题',
    body              VARCHAR(255) COMMENT '商品描述信息',
    notify_url        VARCHAR(255) COMMENT '异步通知地址',
    price             INT COMMENT '支付金额，单位：分',
    channel_fee_rate  DOUBLE COMMENT '渠道手续费，单位：百分比',
    channel_fee_price INT COMMENT '渠道手续金额，单位：分',
    status            INT COMMENT '支付状态',
    user_ip           VARCHAR(255) COMMENT '用户 IP',
    expire_time       DATETIME COMMENT '订单失效时间',
    success_time      DATETIME COMMENT '订单支付成功时间',
    extension_id      BIGINT COMMENT '支付成功的订单拓展单编号',
    no                VARCHAR(255) COMMENT '支付成功的外部订单号',
    refund_price      INT COMMENT '退款总金额，单位：分',
    channel_user_id   VARCHAR(255) COMMENT '渠道用户编号',
    channel_order_no  VARCHAR(255) COMMENT '渠道订单号',
    create_time       datetime comment '创建时间',
    update_time       datetime comment '最后更新时间',
    creator           varchar(255) comment '创建者，目前使用 SysUser 的 id 编号',
    updater           varchar(255) comment '更新者，目前使用 SysUser 的 id 编号',
    deleted           tinyint(1) default 0 comment '是否删除',
    tenant_id         BIGINT comment '租户id'
) comment = '支付订单';

drop table if exists pay_order_extension;
CREATE TABLE pay_order_extension
(
    id                  BIGINT PRIMARY KEY COMMENT '订单拓展编号，数据库自增',
    no                  VARCHAR(255) COMMENT '外部订单号',
    order_id            BIGINT COMMENT '订单号',
    channel_id          BIGINT COMMENT '渠道编号',
    channel_code        VARCHAR(255) COMMENT '渠道编码',
    user_ip             VARCHAR(255) COMMENT '用户 IP',
    status              INT COMMENT '支付状态',
    channel_extras      TEXT COMMENT '支付渠道的额外参数',
    channel_error_code  VARCHAR(255) COMMENT '调用渠道的错误码',
    channel_error_msg   VARCHAR(255) COMMENT '调用渠道报错时，错误信息',
    channel_notify_data TEXT COMMENT '支付渠道的同步/异步通知的内容',
    create_time         datetime comment '创建时间',
    update_time         datetime comment '最后更新时间',
    creator             varchar(255) comment '创建者，目前使用 SysUser 的 id 编号',
    updater             varchar(255) comment '更新者，目前使用 SysUser 的 id 编号',
    deleted             tinyint(1) default 0 comment '是否删除',
    tenant_id           BIGINT comment '租户id'
) comment = '支付订单拓展';

drop table if exists pay_refund;
CREATE TABLE pay_refund
(
    id                  BIGINT PRIMARY KEY COMMENT '退款单编号，数据库自增',
    no                  VARCHAR(255) COMMENT '外部退款号',
    app_id              BIGINT COMMENT '应用编号',
    channel_id          BIGINT COMMENT '渠道编号',
    channel_code        VARCHAR(255) COMMENT '商户编码',
    order_id            BIGINT COMMENT '订单编号',
    order_no            VARCHAR(255) COMMENT '支付订单编号',
    merchant_order_id   VARCHAR(255) COMMENT '商户订单编号',
    merchant_refund_id  VARCHAR(255) COMMENT '商户退款订单号',
    notify_url          VARCHAR(255) COMMENT '异步通知地址',
    status              INT COMMENT '退款状态',
    pay_price           INT COMMENT '支付金额，单位：分',
    refund_price        INT COMMENT '退款金额，单位：分',
    reason              VARCHAR(255) COMMENT '退款原因',
    user_ip             VARCHAR(255) COMMENT '用户 IP',
    channel_order_no    VARCHAR(255) COMMENT '渠道订单号',
    channel_refund_no   VARCHAR(255) COMMENT '渠道退款单号',
    success_time        DATETIME COMMENT '退款成功时间',
    channel_error_code  VARCHAR(255) COMMENT '调用渠道的错误码',
    channel_error_msg   VARCHAR(255) COMMENT '调用渠道的错误提示',
    channel_notify_data TEXT COMMENT '支付渠道的同步/异步通知的内容',
    create_time         datetime comment '创建时间',
    update_time         datetime comment '最后更新时间',
    creator             varchar(255) comment '创建者，目前使用 SysUser 的 id 编号',
    updater             varchar(255) comment '更新者，目前使用 SysUser 的 id 编号',
    deleted             tinyint(1) default 0 comment '是否删除',
    tenant_id           BIGINT comment '租户id'
) comment = '支付退款单';

drop table if exists pay_transfer;
CREATE TABLE pay_transfer
(
    id                   BIGINT PRIMARY KEY COMMENT '编号',
    no                   VARCHAR(255) COMMENT '转账单号',
    app_id               BIGINT COMMENT '应用编号',
    channel_id           BIGINT COMMENT '转账渠道编号',
    channel_code         VARCHAR(255) COMMENT '转账渠道编码',
    merchant_transfer_id VARCHAR(255) COMMENT '商户转账单编号',
    type                 INT COMMENT '类型',
    subject              VARCHAR(255) COMMENT '转账标题',
    price                INT COMMENT '转账金额，单位：分',
    user_name            VARCHAR(255) COMMENT '收款人姓名',
    status               INT COMMENT '转账状态',
    success_time         DATETIME COMMENT '订单转账成功时间',
    alipay_logon_id      VARCHAR(255) COMMENT '支付宝登录号',
    openid               VARCHAR(255) COMMENT '微信 openId',
    notify_url           VARCHAR(255) COMMENT '异步通知地址',
    user_ip              VARCHAR(255) COMMENT '用户 IP',
    channel_extras       TEXT COMMENT '渠道的额外参数',
    channel_transfer_no  VARCHAR(255) COMMENT '渠道转账单号',
    channel_error_code   VARCHAR(255) COMMENT '调用渠道的错误码',
    channel_error_msg    VARCHAR(255) COMMENT '调用渠道的错误提示',
    channel_notify_data  TEXT COMMENT '渠道的同步/异步通知的内容',
    create_time          datetime comment '创建时间',
    update_time          datetime comment '最后更新时间',
    creator              varchar(255) comment '创建者，目前使用 SysUser 的 id 编号',
    updater              varchar(255) comment '更新者，目前使用 SysUser 的 id 编号',
    deleted              tinyint(1) default 0 comment '是否删除',
    tenant_id            BIGINT comment '租户id'
) comment = '转账单';

drop table if exists pay_wallet;
CREATE TABLE pay_wallet
(
    id             BIGINT AUTO_INCREMENT COMMENT '编号',
    user_id        BIGINT COMMENT '用户 id',
    user_type      INT COMMENT '用户类型',
    balance        INT COMMENT '余额，单位分',
    freeze_price   INT COMMENT '冻结金额，单位分',
    total_expense  INT COMMENT '累计支出，单位分',
    total_recharge INT COMMENT '累计充值，单位分',
    create_time    datetime comment '创建时间',
    update_time    datetime comment '最后更新时间',
    creator        varchar(255) comment '创建者，目前使用 SysUser 的 id 编号',
    updater        varchar(255) comment '更新者，目前使用 SysUser 的 id 编号',
    deleted        tinyint(1) default 0 comment '是否删除',
    tenant_id      BIGINT comment '租户id',
    primary key(`id`)
) comment = '会员钱包';

drop table if exists pay_wallet_recharge;
CREATE TABLE pay_wallet_recharge
(
    id                 BIGINT PRIMARY KEY COMMENT '编号',
    wallet_id          BIGINT COMMENT '钱包编号',
    total_price        INT COMMENT '用户实际到账余额',
    pay_price          INT COMMENT '实际支付金额',
    bonus_price        INT COMMENT '钱包赠送金额',
    package_id         BIGINT COMMENT '充值套餐编号',
    pay_status         BOOLEAN COMMENT '是否已支付',
    pay_order_id       BIGINT COMMENT '支付订单编号',
    pay_channel_code   VARCHAR(255) COMMENT '支付成功的支付渠道',
    pay_time           DATETIME COMMENT '订单支付时间',
    pay_refund_id      BIGINT COMMENT '支付退款单编号',
    refund_total_price INT COMMENT '退款金额，包含赠送金额',
    refund_pay_price   INT COMMENT '退款支付金额',
    refund_bonus_price INT COMMENT '退款钱包赠送金额',
    refund_time        DATETIME COMMENT '退款时间',
    refund_status      INT COMMENT '退款状态',
    create_time        datetime comment '创建时间',
    update_time        datetime comment '最后更新时间',
    creator            varchar(255) comment '创建者，目前使用 SysUser 的 id 编号',
    updater            varchar(255) comment '更新者，目前使用 SysUser 的 id 编号',
    deleted            tinyint(1) default 0 comment '是否删除',
    tenant_id          BIGINT comment '租户id'
) comment = '会员钱包充值';

drop table if exists pay_wallet_recharge_package;
CREATE TABLE `pay_wallet_recharge_package`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `name`        VARCHAR(255) NOT NULL COMMENT '套餐名',
    `pay_price`   INT          NOT NULL COMMENT '支付金额',
    `bonus_price` INT          NOT NULL COMMENT '赠送金额',
    `status`      INT          NOT NULL COMMENT '状态',
    create_time   datetime comment '创建时间',
    update_time   datetime comment '最后更新时间',
    creator       varchar(255) comment '创建者，目前使用 SysUser 的 id 编号',
    updater       varchar(255) comment '更新者，目前使用 SysUser 的 id 编号',
    deleted       tinyint(1) default 0 comment '是否删除',
    tenant_id     BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='会员钱包充值套餐 DO';

drop table if exists pay_wallet_transaction;
CREATE TABLE `pay_wallet_transaction`
(
    `id`        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `no`        VARCHAR(255) NOT NULL COMMENT '流水号',
    `wallet_id` BIGINT       NOT NULL COMMENT '钱包编号',
    `biz_type`  INT          NOT NULL COMMENT '关联业务分类',
    `biz_id`    VARCHAR(255) NOT NULL COMMENT '关联业务编号',
    `title`     VARCHAR(255) NOT NULL COMMENT '流水说明',
    `price`     INT          NOT NULL COMMENT '交易金额，单位分',
    `balance`   INT          NOT NULL COMMENT '交易后余额，单位分',
    create_time datetime comment '创建时间',
    update_time datetime comment '最后更新时间',
    creator     varchar(255) comment '创建者，目前使用 SysUser 的 id 编号',
    updater     varchar(255) comment '更新者，目前使用 SysUser 的 id 编号',
    deleted     tinyint(1) default 0 comment '是否删除',
    tenant_id   BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='会员钱包流水 DO';

