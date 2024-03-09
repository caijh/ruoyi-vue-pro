drop table if exists member_config;
CREATE TABLE member_config
(
    id                            BIGINT PRIMARY KEY COMMENT '自增主键',
    point_trade_deduct_enable     BOOLEAN COMMENT '积分抵扣开关',
    point_trade_deduct_unit_price INT COMMENT '积分抵扣，单位：分',
    point_trade_deduct_max_price  INT COMMENT '积分抵扣最大值',
    point_trade_give_point        INT COMMENT '1 元赠送多少分',
    create_time                   DATETIME COMMENT '创建时间',
    update_time                   DATETIME COMMENT '最后更新时间',
    creator                       VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater                       VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted                       TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id                     BIGINT comment '租户id'
) COMMENT ='会员配置';


drop table if exists member_group;
CREATE TABLE member_group
(
    id          BIGINT PRIMARY KEY COMMENT '编号',
    name        VARCHAR(255) COMMENT '名称',
    remark      VARCHAR(255) COMMENT '备注',
    status      INT COMMENT '状态',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id'
) COMMENT ='用户分组';

drop table if exists member_level;
CREATE TABLE member_level
(
    id               BIGINT PRIMARY KEY COMMENT '编号',
    name             VARCHAR(255) COMMENT '等级名称',
    level            INT COMMENT '等级',
    experience       INT COMMENT '升级经验',
    discount_percent INT COMMENT '享受折扣',
    icon             VARCHAR(255) COMMENT '等级图标',
    background_url   VARCHAR(255) COMMENT '等级背景图',
    status           INT COMMENT '状态',
    create_time      DATETIME COMMENT '创建时间',
    update_time      DATETIME COMMENT '最后更新时间',
    creator          VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater          VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted          TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id        BIGINT comment '租户id'
) COMMENT ='会员等级';

drop table if exists member_level_record;
CREATE TABLE member_level_record
(
    id               BIGINT PRIMARY KEY COMMENT '编号',
    user_id          BIGINT COMMENT '用户编号',
    level_id         BIGINT COMMENT '等级编号',
    level            INT COMMENT '会员等级',
    discount_percent INT COMMENT '享受折扣',
    experience       INT COMMENT '升级经验',
    user_experience  INT COMMENT '会员此时的经验',
    remark           VARCHAR(255) COMMENT '备注',
    description      VARCHAR(255) COMMENT '描述',
    create_time      DATETIME COMMENT '创建时间',
    update_time      DATETIME COMMENT '最后更新时间',
    creator          VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater          VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted          TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id        BIGINT comment '租户id'
) COMMENT ='会员等级记录';

drop table if exists member_experience_record;
CREATE TABLE member_experience_record
(
    id               BIGINT PRIMARY KEY COMMENT '编号',
    user_id          BIGINT COMMENT '用户编号',
    biz_type         INT COMMENT '业务类型',
    biz_id           VARCHAR(255) COMMENT '业务编号',
    title            VARCHAR(255) COMMENT '标题',
    description      TEXT COMMENT '描述',
    experience       INT COMMENT '经验',
    total_experience INT COMMENT '变更后的经验',
    create_time      DATETIME COMMENT '创建时间',
    update_time      DATETIME COMMENT '最后更新时间',
    creator          VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater          VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted          TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id        BIGINT comment '租户id'
) COMMENT ='会员经验记录';

drop table if exists member_point_record;
CREATE TABLE member_point_record
(
    id          BIGINT PRIMARY KEY COMMENT '自增主键',
    user_id     BIGINT COMMENT '用户编号',
    biz_id      VARCHAR(255) COMMENT '业务编码',
    biz_type    INT COMMENT '业务类型',
    title       VARCHAR(255) COMMENT '积分标题',
    description TEXT COMMENT '积分描述',
    point       INT COMMENT '变动积分',
    total_point INT COMMENT '变动后的积分',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id'
) COMMENT ='用户积分记录';

drop table if exists member_sign_in_config;
CREATE TABLE member_sign_in_config
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '规则自增主键',
    day         INT COMMENT '签到第 x 天',
    point       INT COMMENT '奖励积分',
    experience  INT COMMENT '奖励经验',
    status      INT COMMENT '状态',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id'
) COMMENT ='签到规则';

drop table if exists member_sign_in_record;
CREATE TABLE member_sign_in_record
(
    id          BIGINT PRIMARY KEY COMMENT '编号',
    user_id     BIGINT COMMENT '签到用户',
    day         INT COMMENT '第几天签到',
    point       INT COMMENT '签到的积分',
    experience  INT COMMENT '签到的经验',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id'
) COMMENT ='签到记录';

drop table if exists member_tag;
CREATE TABLE member_tag
(
    id          BIGINT PRIMARY KEY COMMENT '编号',
    name        VARCHAR(255) COMMENT '标签名称',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id'
);

drop table if exists member_address;
CREATE TABLE member_address
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '编号',
    user_id        BIGINT COMMENT '用户编号',
    name           VARCHAR(255) COMMENT '收件人名称',
    mobile         VARCHAR(255) COMMENT '手机号',
    area_id        BIGINT COMMENT '地区编号',
    detail_address VARCHAR(255) COMMENT '收件详细地址',
    default_status BOOLEAN COMMENT '是否默认',
    create_time    DATETIME COMMENT '创建时间',
    update_time    DATETIME COMMENT '最后更新时间',
    creator        VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater        VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted        TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id'
) COMMENT ='用户收件地址';

drop table if exists member_user;
CREATE TABLE member_user
(
    id                BIGINT PRIMARY KEY COMMENT '用户ID',
    mobile            VARCHAR(255) COMMENT '手机',
    password          VARCHAR(255) COMMENT '加密后的密码',
    status            INT COMMENT '帐号状态',
    register_ip       VARCHAR(255) COMMENT '注册 IP',
    register_terminal INT COMMENT '注册终端',
    login_ip          VARCHAR(255) COMMENT '最后登录IP',
    login_date        DATETIME COMMENT '最后登录时间',
    nickname          VARCHAR(255) COMMENT '用户昵称',
    avatar            VARCHAR(255) COMMENT '用户头像',
    name              VARCHAR(255) COMMENT '真实名字',
    sex               INT COMMENT '性别',
    birthday          DATETIME COMMENT '出生日期',
    area_id           INT COMMENT '所在地',
    mark              VARCHAR(255) COMMENT '用户备注',
    point             INT COMMENT '积分',
    tag_ids           VARCHAR(255) COMMENT '会员标签列表，以逗号分隔',
    level_id          BIGINT COMMENT '会员级别编号',
    experience        INT COMMENT '会员经验',
    group_id          BIGINT COMMENT '用户分组编号',
    create_time       DATETIME COMMENT '创建时间',
    update_time       DATETIME COMMENT '最后更新时间',
    creator           VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater           VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted           TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id         BIGINT comment '租户id'
) comment = '会员用户';
