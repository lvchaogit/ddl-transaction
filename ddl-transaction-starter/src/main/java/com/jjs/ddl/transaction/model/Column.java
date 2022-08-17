package com.jjs.ddl.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: com.yunli.govaffair.das.entity-> Column
 * @Description:
 * @Author: zhouyabing
 * @CreateDate: 2021-12-30 10:07
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Column {
    
    /**
     * 数据库名称
     */
    private String tableSchema;
    
    /**
     * 表名称
     */
    private String tableName;
    
    /**
     * 字段名称
     */
    private String columnName;
    
    /**
     * 原始位置
     */
    private Integer ordinalPosition;
    
    /**
     * 数据类型
     */
    private String dataType;
    
    /**
     * 字段类型
     */
    private String columnType;
    
    /**
     * 字段备注
     */
    private String columnComment;

}
