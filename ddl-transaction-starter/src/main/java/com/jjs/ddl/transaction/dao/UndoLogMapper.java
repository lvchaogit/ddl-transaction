package com.jjs.ddl.transaction.dao;


import com.jjs.ddl.transaction.model.Column;
import com.jjs.ddl.transaction.model.UndoLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UndoLogMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UndoLog record);

    int insertSelective(UndoLog record);

    UndoLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UndoLog record);

    int updateByPrimaryKey(UndoLog record);

    List<UndoLog> selectByTxId(String txId);

    void execSql(@Param("sql") String sql);

    Map<String,String> queryCreateTableSql(@Param("tableName")String tableName);

    List<Map<String, Object>> list(@Param("tbName") String tbName);

    List<Column> getColumns(@Param("tableName") String tableName);

    Column getColumn(@Param("tableName") String tableName,@Param("columnName")String columnName);

    Column getColumnByPosition(@Param("tableName") String tableName,@Param("ordinalPosition") Integer ordinalPosition);
}