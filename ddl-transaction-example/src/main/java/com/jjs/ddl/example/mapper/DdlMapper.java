package com.jjs.ddl.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * DDL Mapper 接口
 * </p>
 *
 * @author lvchao
 * @since 2021-12-30
 */
@Mapper
public interface DdlMapper {

    /**
     * 从指定数据库中，查询是否存在某张表
     *
     * @param dataBaseName
     * @param tableName
     * @return
     */
    String isTargetTableExist(@Param("dataBaseName") String dataBaseName,
                              @Param("tableName") String tableName);

    /**
     * 新建表
     */
    void createNewTable(@Param("tableName") String tableName);

    /**
     * 新增字段
     *
     * @param tableName 表名称
     */
    void addColumn(@Param("tableName") String tableName);

}
 