drop table if exists mp_account;
CREATE TABLE mp_account
(
    id          BIGINT PRIMARY KEY comment '编号',
    name        VARCHAR(255) comment '公众号名称',
    account     VARCHAR(255) comment '公众号账号',
    app_id      VARCHAR(255) comment '公众号 appid',
    app_secret  VARCHAR(255) comment '公众号密钥',
    token       VARCHAR(255) comment '公众号token',
    aes_key     VARCHAR(255) comment '消息加解密密钥',
    qr_code_url VARCHAR(255) comment '二维码图片 URL',
    remark      VARCHAR(255) comment '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id'
) comment = '公众号账号';

drop table if exists mp_material;
CREATE TABLE mp_material
(
    id           BIGINT PRIMARY KEY, -- 主键
    account_id   BIGINT,             -- 公众号账号的编号
    app_id       VARCHAR(255),       -- 公众号 appId
    media_id     VARCHAR(255),       -- 公众号素材 id
    type         VARCHAR(255),       -- 文件类型
    permanent    BOOLEAN,            -- 是否永久
    url          VARCHAR(255),       -- 文件服务器的 URL
    name         VARCHAR(255),       -- 名字
    mp_url       VARCHAR(255),       -- 公众号文件 URL
    title        VARCHAR(255),       -- 视频素材的标题
    introduction VARCHAR(255),       -- 视频素材的描述
    create_time  DATETIME COMMENT '创建时间',
    update_time  DATETIME COMMENT '最后更新时间',
    creator      VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater      VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted      TINYINT(1) DEFAULT 0 COMMENT '是否删除'
) comment = '公众号素材';

drop table if exists mp_menu;
CREATE TABLE mp_menu
(
    id                     BIGINT PRIMARY KEY COMMENT '编号',
    account_id             BIGINT COMMENT '公众号账号的编号',
    app_id                 VARCHAR(255) COMMENT '公众号 appId',
    name                   VARCHAR(255) COMMENT '菜单名称',
    menu_key               VARCHAR(255) COMMENT '菜单标识',
    parent_id              BIGINT COMMENT '父菜单编号',
    type                   VARCHAR(255) COMMENT '按钮类型',
    url                    VARCHAR(1024) COMMENT '网页链接',
    mini_program_app_id    VARCHAR(255) COMMENT '小程序的 appId',
    mini_program_page_path VARCHAR(255) COMMENT '小程序的页面路径',
    article_id             VARCHAR(255) COMMENT '跳转图文的媒体编号',
    reply_message_type     VARCHAR(255) COMMENT '消息类型',
    reply_content          TEXT COMMENT '回复的消息内容',
    reply_media_id         VARCHAR(255) COMMENT '回复的媒体 id',
    reply_media_url        VARCHAR(255) COMMENT '回复的媒体 URL',
    reply_title            VARCHAR(255) COMMENT '回复的标题',
    reply_description      TEXT COMMENT '回复的描述',
    reply_thumb_media_id   VARCHAR(255) COMMENT '回复的缩略图的媒体 id',
    reply_thumb_media_url  VARCHAR(255) COMMENT '回复的缩略图的媒体 URL',
    reply_music_url        VARCHAR(255) COMMENT '回复的音乐链接',
    reply_hq_music_url     VARCHAR(255) COMMENT '回复的高质量音乐链接',
    create_time            DATETIME COMMENT '创建时间',
    update_time            DATETIME COMMENT '最后更新时间',
    creator                VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater                VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted                TINYINT(1) DEFAULT 0 COMMENT '是否删除'
) COMMENT ='公众号菜单';

drop table if exists mp_user;
CREATE TABLE mp_user
(
    id               BIGINT PRIMARY KEY COMMENT '编号',
    openid           VARCHAR(255) COMMENT '粉丝标识',
    subscribe_status INT COMMENT '关注状态',
    subscribe_time   DATETIME COMMENT '关注时间',
    unsubscribe_time DATETIME COMMENT '取消关注时间',
    nickname         VARCHAR(255) COMMENT '昵称',
    head_image_url   VARCHAR(255) COMMENT '头像地址',
    language         VARCHAR(255) COMMENT '语言',
    country          VARCHAR(255) COMMENT '国家',
    province         VARCHAR(255) COMMENT '省份',
    city             VARCHAR(255) COMMENT '城市',
    remark           VARCHAR(255) COMMENT '备注',
    tagIds           VARCHAR(255) COMMENT '标签编号数组',
    account_id       BIGINT COMMENT '公众号账号的编号',
    app_id           VARCHAR(255) COMMENT '公众号 appId',
    create_time      DATETIME COMMENT '创建时间',
    update_time      DATETIME COMMENT '最后更新时间',
    creator          VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater          VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted          TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id   BIGINT comment '租户id'
) COMMENT ='微信公众号粉丝';

drop table if exists mp_tag;
CREATE TABLE mp_tag
(
    id          BIGINT PRIMARY KEY COMMENT '主键',
    tag_id      BIGINT COMMENT '公众号标签 id',
    name        VARCHAR(255) COMMENT '标签名',
    count       INT COMMENT '此标签下粉丝数',
    account_id  BIGINT COMMENT '公众号账号的编号',
    app_id      VARCHAR(255) COMMENT '公众号 appId',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator     VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater     VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted     TINYINT(1) DEFAULT 0 COMMENT '是否删除'
) COMMENT ='公众号标签';

drop table if exists mp_message;
CREATE TABLE mp_message
(
    id              BIGINT PRIMARY KEY COMMENT '主键',
    msg_id          BIGINT COMMENT '微信公众号消息 id',
    account_id      BIGINT COMMENT '公众号账号的 ID',
    app_id          VARCHAR(255) COMMENT '公众号 appid',
    user_id         BIGINT COMMENT '公众号粉丝的编号',
    openid          VARCHAR(255) COMMENT '公众号粉丝标志',
    type            VARCHAR(255) COMMENT '消息类型',
    send_from       INT COMMENT '消息来源',

    -- 普通消息内容字段
    content         TEXT COMMENT '消息内容（仅限文本消息）',
    media_id        VARCHAR(255) COMMENT '媒体文件的编号',
    media_url       VARCHAR(255) COMMENT '媒体文件的 URL',
    recognition     VARCHAR(255) COMMENT '语音识别后文本',
    format          VARCHAR(255) COMMENT '语音格式',
    title           VARCHAR(255) COMMENT '标题',
    description     TEXT COMMENT '描述',
    thumb_media_id  VARCHAR(255) COMMENT '缩略图的媒体 id',
    thumb_media_url VARCHAR(255) COMMENT '缩略图的媒体 URL',
    url             VARCHAR(255) COMMENT '点击图文消息跳转链接',
    location_x      DOUBLE COMMENT '地理位置维度',
    location_y      DOUBLE COMMENT '地理位置经度',
    scale           DOUBLE COMMENT '地图缩放大小',
    label           VARCHAR(255) COMMENT '详细地址',
    articles        TEXT COMMENT '图文消息数组（存储为 JSON 字符串）',

    -- 音乐链接字段
    music_url       VARCHAR(255) COMMENT '音乐链接',
    hq_music_url    VARCHAR(255) COMMENT '高质量音乐链接',

    -- 事件推送字段
    event           VARCHAR(255) COMMENT '事件类型',
    event_key       VARCHAR(255) COMMENT '事件 Key',
    create_time     DATETIME COMMENT '创建时间',
    update_time     DATETIME COMMENT '最后更新时间',
    creator         VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater         VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted         TINYINT(1) DEFAULT 0 COMMENT '是否删除'
) COMMENT ='公众号消息';

drop table if exists mp_auto_reply;
CREATE TABLE mp_auto_reply
(
    id                       BIGINT PRIMARY KEY COMMENT '主键',
    account_id               BIGINT COMMENT '公众号账号的编号',
    app_id                   VARCHAR(255) COMMENT '公众号 appId',
    type                     INT COMMENT '回复类型',

    -- 请求消息字段
    request_keyword          VARCHAR(255) COMMENT '请求的关键字',
    request_match            INT COMMENT '请求的关键字的匹配',
    request_message_type     VARCHAR(255) COMMENT '请求的消息类型',

    -- 响应消息字段
    response_message_type    VARCHAR(255) COMMENT '回复的消息类型',
    response_content         TEXT COMMENT '回复的消息内容（仅限文本消息）',
    response_media_id        VARCHAR(255) COMMENT '回复的媒体 id',
    response_media_url       VARCHAR(255) COMMENT '回复的媒体 URL',
    response_title           VARCHAR(255) COMMENT '回复的标题',
    response_description     TEXT COMMENT '回复的描述',
    response_thumb_media_id  VARCHAR(255) COMMENT '回复的缩略图的媒体 id',
    response_thumb_media_url VARCHAR(255) COMMENT '回复的缩略图的媒体 URL',
    response_articles        TEXT COMMENT '回复的图文消息（存储为 JSON 字符串）',
    response_music_url       VARCHAR(255) COMMENT '回复的音乐链接',
    response_hq_music_url    VARCHAR(255) COMMENT '回复的高质量音乐链接',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '最后更新时间',
    creator VARCHAR(255) COMMENT '创建者，目前使用 SysUser 的 id 编号',
    updater VARCHAR(255) COMMENT '更新者，目前使用 SysUser 的 id 编号',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除'
) COMMENT ='公众号消息自动回复';
