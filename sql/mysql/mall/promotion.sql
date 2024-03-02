drop table if exists promotion_article_category;
CREATE TABLE `promotion_article_category`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文章分类编号',
    `name`      varchar(255) DEFAULT NULL COMMENT '文章分类名称',
    `pic_url`   varchar(255) DEFAULT NULL COMMENT '图标地址',
    `status`    int(11)      DEFAULT NULL COMMENT '状态',
    `sort`      int(11)      DEFAULT NULL COMMENT '排序',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1)   DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='文章分类 DO';

drop table if exists promotion_article;
CREATE TABLE promotion_article
(
    id               BIGINT NOT NULL AUTO_INCREMENT,
    category_id      BIGINT COMMENT '分类编号',
    spu_id           BIGINT COMMENT '关联商品编号',
    title            VARCHAR(255) COMMENT '文章标题',
    author           VARCHAR(255) COMMENT '文章作者',
    pic_url          VARCHAR(255) COMMENT '文章封面图片地址',
    introduction     VARCHAR(255) COMMENT '文章简介',
    browse_count     INT        DEFAULT 0 COMMENT '浏览次数',
    sort             INT        DEFAULT 0 COMMENT '排序',
    status           INT        DEFAULT 0 COMMENT '状态',
    recommend_hot    TINYINT(1) DEFAULT 0 COMMENT '是否热门(小程序)',
    recommend_banner TINYINT(1) DEFAULT 0 COMMENT '是否轮播图(小程序)',
    content          TEXT COMMENT '文章内容',
    create_time      DATETIME COMMENT '创建时间',
    update_time      DATETIME COMMENT '最后更新时间',
    creator          VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater          VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted          TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id        BIGINT comment '租户id',
    PRIMARY KEY (id)
) COMMENT '文章管理 DO';

drop table if exists promotion_banner;
CREATE TABLE promotion_banner
(
    id           BIGINT NOT NULL AUTO_INCREMENT,
    title        VARCHAR(255) COMMENT '标题',
    url          VARCHAR(255) COMMENT '跳转链接',
    pic_url      VARCHAR(255) COMMENT '图片链接',
    sort         INT COMMENT '排序',
    status       INT COMMENT '状态',
    position     INT COMMENT '定位',
    memo         VARCHAR(255) COMMENT '备注',
    browse_count INT COMMENT '点击次数',
    create_time  DATETIME COMMENT '创建时间',
    update_time  DATETIME COMMENT '最后更新时间',
    creator      VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater      VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted      TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id    BIGINT comment '租户id',
    PRIMARY KEY (id)
) COMMENT 'banner DO';

drop table if exists promotion_bargain_activity;
CREATE TABLE promotion_bargain_activity
(
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    name                VARCHAR(255) COMMENT '砍价活动名称',
    start_time          DATETIME COMMENT '活动开始时间',
    end_time            DATETIME COMMENT '活动结束时间',
    status              INT COMMENT '活动状态',
    spu_id              BIGINT COMMENT '商品 SPU 编号',
    sku_id              BIGINT COMMENT '商品 SKU 编号',
    bargain_first_price INT COMMENT '砍价起始价格，单位：分',
    bargain_min_price   INT COMMENT '砍价底价，单位：分',
    stock               INT COMMENT '砍价库存(剩余库存砍价时扣减)',
    total_stock         INT COMMENT '砍价总库存',
    help_max_count      INT COMMENT '砍价人数',
    bargain_count       INT COMMENT '帮砍次数',
    total_limit_count   INT COMMENT '总限购数量',
    random_min_price    INT COMMENT '用户每次砍价的最小金额，单位：分',
    random_max_price    INT COMMENT '用户每次砍价的最大金额，单位：分',
    create_time         DATETIME COMMENT '创建时间',
    update_time         DATETIME COMMENT '最后更新时间',
    creator             VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater             VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted             TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id           BIGINT comment '租户id',
    PRIMARY KEY (id)
) COMMENT '砍价活动 DO';

drop table if exists promotion_bargain_help;
CREATE TABLE promotion_bargain_help
(
    id           BIGINT NOT NULL AUTO_INCREMENT,
    activity_id  BIGINT NOT NULL COMMENT '砍价活动编号',
    record_id    BIGINT NOT NULL COMMENT '砍价记录编号',
    user_id      BIGINT NOT NULL COMMENT '用户编号',
    reduce_price INT    NOT NULL COMMENT '减少价格，单位：分',
    create_time  DATETIME COMMENT '创建时间',
    update_time  DATETIME COMMENT '最后更新时间',
    creator      VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater      VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted      TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id    BIGINT comment '租户id',
    PRIMARY KEY (id)
) COMMENT '砍价助力';

drop table if exists promotion_bargain_record;
CREATE TABLE promotion_bargain_record
(
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    user_id             BIGINT NOT NULL COMMENT '用户编号',
    activity_id         BIGINT NOT NULL COMMENT '砍价活动编号',
    spu_id              BIGINT COMMENT '商品 SPU 编号',
    sku_id              BIGINT COMMENT '商品 SKU 编号',
    bargain_first_price INT COMMENT '砍价起始价格，单位：分',
    bargain_price       INT COMMENT '当前砍价，单位：分',
    status              INT COMMENT '砍价状态',
    end_time            DATETIME COMMENT '结束时间',
    order_id            BIGINT COMMENT '订单编号',
    create_time         DATETIME COMMENT '创建时间',
    update_time         DATETIME COMMENT '最后更新时间',
    creator             VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater             VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted             TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id           BIGINT comment '租户id',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='砍价记录';

DROP TABLE IF EXISTS promotion_combination_activity;
CREATE TABLE promotion_combination_activity
(
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    name               VARCHAR(255) NOT NULL COMMENT '拼团名称',
    spu_id             BIGINT       NOT NULL COMMENT '商品 SPU 编号',
    total_limit_count  INT COMMENT '总限购数量',
    single_limit_count INT COMMENT '单次限购数量',
    start_time         DATETIME COMMENT '开始时间',
    end_time           DATETIME COMMENT '结束时间',
    user_size          INT COMMENT '几人团',
    virtual_group      BOOLEAN COMMENT '虚拟成团',
    status             INT COMMENT '活动状态',
    limit_duration     INT COMMENT '限制时长（小时）',
    create_time        DATETIME COMMENT '创建时间',
    update_time        DATETIME COMMENT '最后更新时间',
    creator            VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater            VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted            TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id          BIGINT comment '租户id',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='拼团活动';

DROP TABLE IF EXISTS promotion_combination_product;
CREATE TABLE promotion_combination_product
(
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    activity_id         BIGINT NOT NULL COMMENT '拼团活动编号',
    spu_id              BIGINT NOT NULL COMMENT '商品 SPU 编号',
    sku_id              BIGINT NOT NULL COMMENT '商品 SKU 编号',
    combination_price   INT COMMENT '拼团价格，单位分',
    activity_status     INT COMMENT '拼团商品状态',
    activity_start_time DATETIME COMMENT '活动开始时间点',
    activity_end_time   DATETIME COMMENT '活动结束时间点',
    create_time         DATETIME COMMENT '创建时间',
    update_time         DATETIME COMMENT '最后更新时间',
    creator             VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater             VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted             TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id           BIGINT comment '租户id',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='拼团商品';


DROP TABLE IF EXISTS promotion_combination_record;
CREATE TABLE promotion_combination_record
(
    id                BIGINT NOT NULL AUTO_INCREMENT,
    activity_id       BIGINT NOT NULL COMMENT '拼团活动编号',
    combination_price INT COMMENT '拼团商品单价',
    spu_id            BIGINT COMMENT 'SPU 编号',
    spu_name          VARCHAR(255) COMMENT '商品名字',
    pic_url           VARCHAR(255) COMMENT '商品图片',
    sku_id            BIGINT COMMENT 'SKU 编号',
    count             INT COMMENT '购买的商品数量',
    user_id           BIGINT COMMENT '用户编号',
    nickname          VARCHAR(255) COMMENT '用户昵称',
    avatar            VARCHAR(255) COMMENT '用户头像',
    head_id           BIGINT COMMENT '团长编号',
    status            INT COMMENT '开团状态',
    order_id          BIGINT COMMENT '订单编号',
    user_size         INT COMMENT '开团需要人数',
    user_count        INT COMMENT '已加入拼团人数',
    virtual_group     BOOLEAN COMMENT '是否虚拟成团',
    expire_time       DATETIME COMMENT '过期时间',
    start_time        DATETIME COMMENT '开始时间 (订单付款后开始的时间)',
    end_time          DATETIME COMMENT '结束时间（成团时间/失败时间）',
    create_time       DATETIME COMMENT '创建时间',
    update_time       DATETIME COMMENT '最后更新时间',
    creator           VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater           VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted           TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id         BIGINT comment '租户id',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='拼团记录';

DROP TABLE IF EXISTS promotion_coupon;
CREATE TABLE promotion_coupon
(
    id                   BIGINT NOT NULL AUTO_INCREMENT,
    template_id          BIGINT NOT NULL COMMENT '优惠劵模板编号',
    name                 VARCHAR(255) COMMENT '优惠劵名',
    status               INT COMMENT '优惠码状态',
    user_id              BIGINT COMMENT '用户编号',
    take_type            INT COMMENT '领取类型',
    use_price            INT COMMENT '是否设置满多少金额可用，单位：分',
    valid_start_time     DATETIME COMMENT '生效开始时间',
    valid_end_time       DATETIME COMMENT '生效结束时间',
    product_scope        INT COMMENT '商品范围',
    productScopeValues   varchar(255) COMMENT '商品范围编号的数组',
    discount_type        INT COMMENT '折扣类型',
    discount_percent     INT COMMENT '折扣百分比',
    discount_price       INT COMMENT '优惠金额，单位：分',
    discount_limit_price INT COMMENT '折扣上限',
    use_order_id         BIGINT COMMENT '使用订单号',
    use_time             DATETIME COMMENT '使用时间',
    create_time          DATETIME COMMENT '创建时间',
    update_time          DATETIME COMMENT '最后更新时间',
    creator              VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater              VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted              TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id            BIGINT comment '租户id',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='优惠劵';

DROP TABLE IF EXISTS promotion_coupon_template;
CREATE TABLE promotion_coupon_template
(
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    name                 VARCHAR(255) NOT NULL COMMENT '优惠劵名',
    status               INT COMMENT '状态',
    total_count          INT COMMENT '发放数量',
    take_limit_count     INT COMMENT '每人限领个数',
    take_type            INT COMMENT '领取方式',
    use_price            INT COMMENT '是否设置满多少金额可用，单位：分',
    product_scope        INT COMMENT '商品范围',
    product_scope_values VARCHAR(255) COMMENT '商品范围编号的数组',
    validity_type        INT COMMENT '生效日期类型',
    valid_start_time     DATETIME COMMENT '固定日期 - 生效开始时间',
    valid_end_time       DATETIME COMMENT '固定日期 - 生效结束时间',
    fixed_start_term     INT COMMENT '领取日期 - 开始天数',
    fixed_end_term       INT COMMENT '领取日期 - 结束天数',
    discount_type        INT COMMENT '折扣类型',
    discount_percent     INT COMMENT '折扣百分比',
    discount_price       INT COMMENT '优惠金额，单位：分',
    discount_limit_price INT COMMENT '折扣上限',
    take_count           INT COMMENT '领取优惠券的数量',
    use_count            INT COMMENT '使用优惠券的次数',
    create_time          DATETIME COMMENT '创建时间',
    update_time          DATETIME COMMENT '最后更新时间',
    creator              VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater              VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted              TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id            BIGINT comment '租户id',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='优惠劵模板';

DROP TABLE IF EXISTS promotion_discount_activity;
CREATE TABLE promotion_discount_activity
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL COMMENT '活动标题',
    status      INT COMMENT '状态',
    start_time  DATETIME     NOT NULL COMMENT '开始时间',
    end_time    DATETIME     NOT NULL COMMENT '结束时间',
    remark      VARCHAR(255) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='限时折扣活动';


DROP TABLE IF EXISTS promotion_discount_product;
CREATE TABLE promotion_discount_product
(
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    activity_id         BIGINT NOT NULL COMMENT '限时折扣活动的编号',
    spu_id              BIGINT NOT NULL COMMENT '商品 SPU 编号',
    sku_id              BIGINT NOT NULL COMMENT '商品 SKU 编号',
    discount_type       INT COMMENT '折扣类型',
    discount_percent    INT COMMENT '折扣百分比',
    discount_price      INT COMMENT '优惠金额，单位：分',
    activity_status     INT COMMENT '活动状态',
    activity_start_time DATETIME COMMENT '活动开始时间点',
    activity_end_time   DATETIME COMMENT '活动结束时间点',
    create_time         DATETIME COMMENT '创建时间',
    update_time         DATETIME COMMENT '最后更新时间',
    creator             VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater             VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted             TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id           BIGINT comment '租户id',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='限时折扣商品';

DROP TABLE IF EXISTS promotion_diy_page;
CREATE TABLE promotion_diy_page
(
    id               BIGINT NOT NULL AUTO_INCREMENT,
    template_id      BIGINT COMMENT '装修模板编号',
    name             VARCHAR(255) COMMENT '页面名称',
    remark           VARCHAR(255) COMMENT '备注',
    preview_pic_urls VARCHAR(1024) COMMENT '预览图，多个逗号分隔',
    property         TEXT COMMENT '页面属性，JSON 格式',
    create_time      DATETIME COMMENT '创建时间',
    update_time      DATETIME COMMENT '最后更新时间',
    creator          VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater          VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted          TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id        BIGINT comment '租户id',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='装修页面';


DROP TABLE IF EXISTS promotion_diy_template;
CREATE TABLE promotion_diy_template
(
    id               BIGINT NOT NULL AUTO_INCREMENT,
    name             VARCHAR(255) COMMENT '模板名称',
    used             BOOLEAN COMMENT '是否使用',
    used_time        DATETIME COMMENT '使用时间',
    remark           VARCHAR(255) COMMENT '备注',
    preview_pic_urls VARCHAR(1024) COMMENT '预览图',
    property         TEXT COMMENT 'uni-app 底部导航属性，JSON 格式',
    create_time      DATETIME COMMENT '创建时间',
    update_time      DATETIME COMMENT '最后更新时间',
    creator          VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater          VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted          TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id        BIGINT comment '租户id',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='装修模板';


DROP TABLE IF EXISTS promotion_reward_activity;
CREATE TABLE promotion_reward_activity
(
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    name            VARCHAR(255) NOT NULL COMMENT '活动标题',
    status          INT COMMENT '状态',
    start_time      DATETIME     NOT NULL COMMENT '开始时间',
    end_time        DATETIME     NOT NULL COMMENT '结束时间',
    remark          VARCHAR(255) COMMENT '备注',
    condition_type  INT COMMENT '条件类型',
    product_scope   INT COMMENT '商品范围',
    product_spu_ids VARCHAR(1024) COMMENT '商品 SPU 编号的数组',
    rules           TEXT COMMENT '优惠规则的数组（JSON 格式）',
    create_time     DATETIME COMMENT '创建时间',
    update_time     DATETIME COMMENT '最后更新时间',
    creator         VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater         VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted         TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id       BIGINT comment '租户id',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='满减送活动';


DROP TABLE IF EXISTS promotion_seckill_activity;
CREATE TABLE promotion_seckill_activity
(
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    spu_id             BIGINT       NOT NULL COMMENT '秒杀活动商品',
    name               VARCHAR(255) NOT NULL COMMENT '秒杀活动名称',
    status             INT COMMENT '活动状态',
    remark             VARCHAR(255) COMMENT '备注',
    start_time         DATETIME     NOT NULL COMMENT '活动开始时间',
    end_time           DATETIME     NOT NULL COMMENT '活动结束时间',
    sort               INT COMMENT '排序',
    config_ids         VARCHAR(1024) COMMENT '秒杀时段 id',
    total_limit_count  INT COMMENT '总限购数量',
    single_limit_count INT COMMENT '单次限购数量',
    stock              INT COMMENT '秒杀库存(剩余库存秒杀时扣减)',
    total_stock        INT COMMENT '秒杀总库存',
    create_time        DATETIME COMMENT '创建时间',
    update_time        DATETIME COMMENT '最后更新时间',
    creator            VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater            VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted            TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id          BIGINT comment '租户id',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='秒杀活动';


DROP TABLE IF EXISTS promotion_seckill_config;
CREATE TABLE promotion_seckill_config
(
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    name            VARCHAR(255) NOT NULL COMMENT '秒杀时段名称',
    start_time      VARCHAR(10) COMMENT '开始时间点',
    end_time        VARCHAR(10) COMMENT '结束时间点',
    slider_pic_urls VARCHAR(1024) COMMENT '秒杀轮播图',
    status          INT COMMENT '状态',
    create_time     DATETIME COMMENT '创建时间',
    update_time     DATETIME COMMENT '最后更新时间',
    creator         VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater         VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted         TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id       BIGINT comment '租户id',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='秒杀时段';

DROP TABLE IF EXISTS promotion_seckill_product;
CREATE TABLE promotion_seckill_product
(
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    activity_id         BIGINT NOT NULL COMMENT '秒杀活动 id',
    config_ids          VARCHAR(1024) COMMENT '秒杀时段 id',
    spu_id              BIGINT COMMENT '商品 SPU 编号',
    sku_id              BIGINT COMMENT '商品 SKU 编号',
    seckill_price       INT COMMENT '秒杀金额，单位：分',
    stock               INT COMMENT '秒杀库存',
    activity_status     INT COMMENT '秒杀商品状态',
    activity_start_time DATETIME COMMENT '活动开始时间点',
    activity_end_time   DATETIME COMMENT '活动结束时间点',
    create_time     DATETIME COMMENT '创建时间',
    update_time     DATETIME COMMENT '最后更新时间',
    creator         VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater         VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted         TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id       BIGINT comment '租户id',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='秒杀参与商品';
