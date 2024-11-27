-- ----------------------------
-- Table structure for t_auth_element_operation
-- ----------------------------
DROP TABLE IF EXISTS `t_auth_element_operation`;
CREATE TABLE `t_auth_element_operation`
(
    `id`            bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `elementName`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '页面元素名称',
    `elementCode`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '唯一编码',
    `operationType` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作类型：0可点击 1可见',
    `createTime`    datetime NULL DEFAULT NULL COMMENT '创建时间',
    `updateTime`    datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限控制-页面元素操作表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_auth_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_auth_menu`;
CREATE TABLE `t_auth_menu`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '页面菜单名称',
    `code`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '唯一编码',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `updateTime` datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限控制-页面访问表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_auth_role
-- ----------------------------
DROP TABLE IF EXISTS `t_auth_role`;
CREATE TABLE `t_auth_role`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色名称',
    `code`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '唯一编码',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `updateTime` datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限控制-角色表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_auth_role_element_operation
-- ----------------------------
DROP TABLE IF EXISTS `t_auth_role_element_operation`;
CREATE TABLE `t_auth_role_element_operation`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `roleId`             bigint NULL DEFAULT NULL COMMENT '角色ID',
    `elementOperationId` bigint NULL DEFAULT NULL COMMENT '元素操作ID',
    `createTime`         datetime NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限控制-角色与元素操作关联表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_auth_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_auth_role_menu`;
CREATE TABLE `t_auth_role_menu`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `roleId`     bigint NULL DEFAULT NULL COMMENT '角色ID',
    `menuId`     bigint NULL DEFAULT NULL COMMENT '页面菜单ID',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限控制-角色与页面菜单关联表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_bullet_comment
-- ----------------------------
DROP TABLE IF EXISTS `t_bullet_comment`;
CREATE TABLE `t_bullet_comment`
(
    `id`                bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`            bigint NULL DEFAULT NULL COMMENT '用户ID',
    `videoId`           bigint NULL DEFAULT NULL COMMENT '视频ID',
    `content`           text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci        NULL COMMENT '弹幕内容',
    `bulletCommentTime` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '出现时间',
    `createTime`        datetime NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb3
  COLLATE = utf8mb3_general_ci COMMENT = '弹幕记录表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_collection_group
-- ----------------------------
DROP TABLE IF EXISTS `t_collection_group`;
CREATE TABLE `t_collection_group`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`     bigint NULL DEFAULT NULL COMMENT '用户ID',
    `name`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收藏分组名称',
    `type`       varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收藏分组类型：0默认分组 1自定义分组',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `updateTime` datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '收藏分组表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_file
-- ----------------------------
DROP TABLE IF EXISTS `t_file`;
CREATE TABLE `t_file`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `url`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件访问路径',
    `type`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件类型',
    `md5`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件MD5加密结果',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_following_group
-- ----------------------------
DROP TABLE IF EXISTS `t_following_group`;
CREATE TABLE `t_following_group`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`     bigint NULL DEFAULT NULL COMMENT '用户ID',
    `name`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关注分组名称',
    `type`       varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关注分组类型：0特别关注 1悄悄关注 2默认分组 3自定义分组',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `updateTime` datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户关注分组表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_refresh_token
-- ----------------------------
DROP TABLE IF EXISTS `t_refresh_token`;
CREATE TABLE `t_refresh_token`
(
    `id`           bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`       bigint NULL DEFAULT NULL COMMENT '用户ID',
    `refreshToken` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '刷新令牌',
    `createTime`   datetime NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '刷新令牌记录表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签名称',
    `createTime` datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `phone`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号码',
    `email`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
    `password`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
    `salt`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '盐值',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `updateTime` datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_coin
-- ----------------------------
DROP TABLE IF EXISTS `t_user_coin`;
CREATE TABLE `t_user_coin`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`     bigint NULL DEFAULT NULL COMMENT '用户ID',
    `amount`     bigint NULL DEFAULT NULL COMMENT '硬币总数',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `updateTime` datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户硬币表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_following
-- ----------------------------
DROP TABLE IF EXISTS `t_user_following`;
CREATE TABLE `t_user_following`
(
    `id`          bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`      bigint NULL DEFAULT NULL COMMENT '用户ID',
    `followingId` int NULL DEFAULT NULL COMMENT '关注用户ID',
    `groupId`     int NULL DEFAULT NULL COMMENT '关注分组ID',
    `createTime`  datetime NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户关注表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_info
-- ----------------------------
DROP TABLE IF EXISTS `t_user_info`;
CREATE TABLE `t_user_info`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`     bigint NULL DEFAULT NULL COMMENT '用户ID',
    `nick`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
    `avatar`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
    `sign`       text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '签名',
    `gender`     varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别：0男 1女 2未知',
    `birth`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生日',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `updateTime` datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_moments
-- ----------------------------
DROP TABLE IF EXISTS `t_user_moments`;
CREATE TABLE `t_user_moments`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`     bigint NULL DEFAULT NULL COMMENT '用户ID',
    `type`       varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '动态类型：0视频 1直播 2专栏',
    `contentId`  bigint NULL DEFAULT NULL COMMENT '内容详情ID',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `updateTime` datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户动态表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`     bigint NULL DEFAULT NULL COMMENT '用户ID',
    `roleId`     bigint NULL DEFAULT NULL COMMENT '角色ID',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_video
-- ----------------------------
DROP TABLE IF EXISTS `t_video`;
CREATE TABLE `t_video`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`      bigint                                                        NOT NULL COMMENT '用户ID',
    `url`         varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频链接',
    `thumbnail`   varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '封面链接',
    `title`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频标题',
    `type`        varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   NOT NULL COMMENT '视频类型：0原创 1转载',
    `duration`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频时长',
    `area`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频分区：0鬼畜 1音乐 2电影',
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '视频简介',
    `createTime`  datetime NULL DEFAULT NULL COMMENT '创建时间',
    `updateTime`  datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频投稿记录表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_video_coin
-- ----------------------------
DROP TABLE IF EXISTS `t_video_coin`;
CREATE TABLE `t_video_coin`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`     bigint NULL DEFAULT NULL COMMENT '用户ID',
    `videoId`    bigint NULL DEFAULT NULL COMMENT '视频ID',
    `amount`     int NULL DEFAULT NULL COMMENT '投币数',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `updateTime` datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频硬币表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_video_collection
-- ----------------------------
DROP TABLE IF EXISTS `t_video_collection`;
CREATE TABLE `t_video_collection`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `videoId`    bigint NULL DEFAULT NULL COMMENT '视频ID',
    `userId`     bigint NULL DEFAULT NULL COMMENT '用户ID',
    `groupId`    bigint NULL DEFAULT NULL COMMENT '收藏分组ID',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频收藏记录表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_video_comment
-- ----------------------------
DROP TABLE IF EXISTS `t_video_comment`;
CREATE TABLE `t_video_comment`
(
    `id`          bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `videoId`     bigint NOT NULL COMMENT '视频ID',
    `userId`      bigint NOT NULL COMMENT '用户ID',
    `comment`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论',
    `replyUserId` bigint NULL DEFAULT NULL COMMENT '回复用户ID',
    `rootId`      bigint NULL DEFAULT NULL COMMENT '根节点评论ID',
    `createTime`  datetime NULL DEFAULT NULL COMMENT '创建时间',
    `updateTime`  datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频评论表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_video_like
-- ----------------------------
DROP TABLE IF EXISTS `t_video_like`;
CREATE TABLE `t_video_like`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`     bigint NOT NULL COMMENT '用户ID',
    `videoId`    bigint NOT NULL COMMENT '视频投稿ID',
    `createTime` datetime NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频点赞记录表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_video_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_video_tag`;
CREATE TABLE `t_video_tag`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `videoId`    bigint NOT NULL COMMENT '视频ID',
    `tagId`      bigint NOT NULL COMMENT '标签ID',
    `createTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频标签关联表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_auth_role
-- ----------------------------
INSERT INTO `t_auth_role`
VALUES (1, '等级0', 'Lv0', NULL, NULL);
INSERT INTO `t_auth_role`
VALUES (2, '等级1', 'Lv1', NULL, NULL);
INSERT INTO `t_auth_role`
VALUES (3, '等级2', 'Lv2', NULL, NULL);
INSERT INTO `t_auth_role`
VALUES (4, '等级3', 'Lv3', NULL, NULL);
INSERT INTO `t_auth_role`
VALUES (5, '等级4', 'Lv4', NULL, NULL);
INSERT INTO `t_auth_role`
VALUES (6, '等级5', 'Lv5', NULL, NULL);
INSERT INTO `t_auth_role`
VALUES (7, '等级6', 'Lv6', NULL, NULL);

-- ----------------------------
-- Records of t_following_group
-- ----------------------------
INSERT INTO `t_following_group`
VALUES (1, NULL, '特别关注', '0', NULL, NULL);
INSERT INTO `t_following_group`
VALUES (2, NULL, '悄悄关注', '1', NULL, NULL);
INSERT INTO `t_following_group`
VALUES (3, NULL, '默认关注', '2', NULL, NULL);

-- ----------------------------
-- Records of t_collection_group
-- ----------------------------
INSERT INTO `t_collection_group`
VALUES (1, NULL, '默认收藏', 0, now(), NULL);

-- ----------------------------
-- Records of t_tag
-- ----------------------------
INSERT INTO `t_tag`
VALUES (1, '鬼畜', now());
INSERT INTO `t_tag`
VALUES (2, '生活', now());
INSERT INTO `t_tag`
VALUES (3, '健身', now());
INSERT INTO `t_tag`
VALUES (4, '游戏', now());
INSERT INTO `t_tag`
VALUES (5, '健身', now());

-- ----------------------------
-- Records of t_auth_element_operation
-- ----------------------------
INSERT INTO `t_auth_element_operation`
VALUES (1, '视频投稿操作', 'postVideoButton', '0', now(), NULL);

-- ----------------------------
-- Records of t_auth_role_element_operation
-- ----------------------------
INSERT INTO `t_auth_role_element_operation`
VALUES (1, 2, 1, now());

-- ----------------------------
-- Records of t_auth_menu
-- ----------------------------
INSERT INTO `t_auth_menu`
VALUES (1, '页面访问权限', '0001', now(), NULL);

-- ----------------------------
-- Records of t_auth_role_menu
-- ----------------------------
INSERT INTO `t_auth_role_menu`
VALUES (1, 2, 1, now());