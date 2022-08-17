package com.jjs.ddl.transaction.service.impl;


import com.jjs.ddl.transaction.dao.UndoLogMapper;
import com.jjs.ddl.transaction.model.Column;
import com.jjs.ddl.transaction.model.UndoLog;
import com.jjs.ddl.transaction.service.UndoLogService;
import com.jjs.ddl.transaction.support.ContextHolder;
import com.jjs.ddl.transaction.util.SqlHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 回滚日志
 * @Version: 1.0
 * @Author: zhangrenhua
 * @Date: Created in 10:17 2022/1/14
 */
@Service
@Slf4j
public class UndoLogServiceImpl implements UndoLogService {

    @Resource
    private UndoLogMapper undoLogMapper;

    @Override
    public void saveUndoLog(List<String> sqlList) {
        if (CollectionUtils.isEmpty(sqlList)) {
            return;
        }
        for (String str : sqlList) {
            UndoLog record = UndoLog.builder()
                    .txId(ContextHolder.getTxId())
                    .sql(str)
                    .build();
            undoLogMapper.insertSelective(record);
        }
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void execUndoLog() {
        String txId = ContextHolder.getTxId();
        List<UndoLog> undoLogList = undoLogMapper.selectByTxId(txId);
        if (CollectionUtils.isEmpty(undoLogList)) {
            return;
        }
        for (UndoLog undoLog : undoLogList) {
            try {
                undoLogMapper.execSql(undoLog.getSql());
                undoLog.setStatus(1);
            } catch (Exception e) {
                undoLog.setStatus(2);
                log.error("执行回滚操作[txId:{}]失败:{}", txId, e.getMessage(), e);
            }
            try {
                undoLogMapper.updateByPrimaryKeySelective(undoLog);
            } catch (Exception e) {
                log.warn("更新回滚日志状态[txId:{}]失败:{}", txId, e.getMessage(), e);
            }
        }
        ContextHolder.clearTxId();
    }


    @Override
    public List<String> generateRollbackSql(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return Collections.emptyList();
        }
        try {
            sql = sql.trim().toLowerCase();
            if (sql.contains(SqlHelper.CREATE_TABLE_PREFIX)) {
                return getCreateTableRollbackSql(sql);
            } else if (sql.contains(SqlHelper.DROP_TABLE_PREFIX)) {
                return getDropTableRollbackSql(sql);
            } else if (sql.contains(SqlHelper.ALTER_TABLE_PREFIX)) {
                if (sql.contains(SqlHelper.ADD_COLUMN_PREFIX)) {
                    return getAddColumnRollbackSql(sql);
                } else if (sql.contains(SqlHelper.MODIFY_COLUMN_PREFIX)) {
                    return getModifyColumnRollbackSql(sql);
                } else if (sql.contains(SqlHelper.DROP_COLUMN_PREFIX)) {
                    return getDropColumnRollbackSql(sql);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    public List<String> getCreateTableRollbackSql(String sql) {
        String tableName = sql.substring(sql.indexOf(SqlHelper.TABLE_PREFIX) + 5, sql.indexOf("(")).replace("`", "").trim();
        String tableSql = SqlHelper.joinDropTableSql(tableName);
        return Collections.singletonList(tableSql);
    }

    public List<String> getDropTableRollbackSql(String sql) {
        List<String> sqlList = new ArrayList<>();
        String tableName;
        if (sql.contains(SqlHelper.EXIST_PREFIX)) {
            tableName = sql.substring(sql.indexOf(SqlHelper.EXIST_PREFIX) + 6).replace(";", "").replace("`", "").trim();
        } else {
            tableName = sql.substring(sql.indexOf(SqlHelper.TABLE_PREFIX) + 5).replace(";", "").replace("`", "").trim();
        }
        Map<String, String> resultMap = undoLogMapper.queryCreateTableSql(tableName);
        sqlList.add(resultMap.get(SqlHelper.SHOW_TABLE_NAME));
        List<Map<String, Object>> dataList = undoLogMapper.list(tableName);
        if (CollectionUtils.isNotEmpty(dataList)) {
            List<Column> columns = undoLogMapper.getColumns(tableName);
            dataList.forEach(data -> sqlList.add(SqlHelper.joinInsertSql(tableName, columns, data)));
        }
        return sqlList;
    }

    public List<String> getAddColumnRollbackSql(String sql) {
        List<String> sqlList = new ArrayList<>();
        String[] split = sql.split(BLANK_STR);
        List<String> collect = Arrays.stream(split).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        String tableName = null;
        String columnName = null;
        for (int i = 0; i < collect.size(); i++) {
            if (collect.get(i).equals(SqlHelper.TABLE_PREFIX)) {
                tableName = collect.get(i + 1);
            }
            if (collect.get(i).equals(SqlHelper.ADD_COLUMN_PREFIX)) {
                columnName = collect.get(i + 1);
            }
        }
        sqlList.add(SqlHelper.joinDropColumnSql(tableName, columnName));
        return sqlList;
    }

    public List<String> getModifyColumnRollbackSql(String sql) {
        List<String> sqlList = new ArrayList<>();
        String[] split = sql.split(BLANK_STR);
        List<String> collect = Arrays.stream(split).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        String tableName = null;
        String columnName = null;
        for (int i = 0; i < collect.size(); i++) {
            if (collect.get(i).equals(SqlHelper.TABLE_PREFIX)) {
                tableName = collect.get(i + 1).replace("`", "");
            }
            if (collect.get(i).equals(SqlHelper.MODIFY_COLUMN_PREFIX)) {
                columnName = collect.get(i + 1).replace("`", "");
            }
        }
        Column column = undoLogMapper.getColumn(tableName, columnName);
        sqlList.add(SqlHelper.joinModifyColumnSql(tableName, columnName, column));
        List<Map<String, Object>> dataList = undoLogMapper.list(tableName);
        if (CollectionUtils.isNotEmpty(dataList)) {
            for (Map<String, Object> data : dataList) {
                sqlList.add(SqlHelper.joinUpdateSql(tableName, columnName, column, data));
            }
        }
        return sqlList;
    }

    private static final String BLANK_STR = " ";

    public List<String> getDropColumnRollbackSql(String sql) {
        List<String> sqlList = new ArrayList<>();
        String[] split = sql.split(BLANK_STR);
        List<String> collect = Arrays.stream(split).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        String tableName = null;
        String columnName = null;
        for (int i = 0; i < collect.size(); i++) {
            if (collect.get(i).equals(SqlHelper.TABLE_PREFIX)) {
                tableName = collect.get(i + 1).replace("`", StringUtils.EMPTY);
            }
            if (collect.get(i).equals(SqlHelper.DROP_COLUMN_PREFIX)) {
                columnName = collect.get(i + 1).replace("`", StringUtils.EMPTY);
            }
        }
        Column column = undoLogMapper.getColumn(tableName, columnName);
        Column beforeColumn = undoLogMapper.getColumnByPosition(tableName, column.getOrdinalPosition() - 1);
        sqlList.add(SqlHelper.joinAddColumnSql(tableName, columnName, column, beforeColumn));
        List<Map<String, Object>> dataList = undoLogMapper.list(tableName);
        if (CollectionUtils.isNotEmpty(dataList)) {
            for (Map<String, Object> data : dataList) {
                sqlList.add(SqlHelper.joinUpdateSql(tableName, columnName, column, data));
            }
        }
        return sqlList;
    }
}
