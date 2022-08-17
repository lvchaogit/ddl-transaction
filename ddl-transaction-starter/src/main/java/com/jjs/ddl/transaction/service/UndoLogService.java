package com.jjs.ddl.transaction.service;

import java.util.List;

/**
 * @Description:
 * @Version: 1.0
 * @Author: zhangrenhua
 * @Date: Created in 10:16 2022/1/14
 */
public interface UndoLogService {

    /**
     * 保存回滚日志
     * @param sqlList
     */
    void saveUndoLog(List<String> sqlList);

    /**
     * 执行回滚操作
     */
    void execUndoLog();

    /**
     * 获取回滚语句
     * @param sql
     * @return
     */
    List<String> generateRollbackSql(String sql);
}
