-- liquibase formatted sql
-- changeset lvchao:20220624
-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
`tx_id` varchar(64) NOT NULL COMMENT '事务id',
`sql` varchar(1000) NOT NULL COMMENT 'SQL语句',
`status` tinyint(1) NULL DEFAULT 0 COMMENT '0 未执行  1 已执行 2 执行失败',
`run_time` datetime NULL DEFAULT NULL COMMENT '执行时间',
`is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
`create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
`creator` varchar(32) NULL DEFAULT NULL COMMENT '创建者',
`update_time` datetime NULL DEFAULT NULL COMMENT '修改者',
`modifier` varchar(32) NULL DEFAULT NULL COMMENT '修改者',
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '回滚日志表' ROW_FORMAT = Dynamic;

