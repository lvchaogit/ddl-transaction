package com.jjs.ddl.transaction.util;

import com.jjs.ddl.transaction.model.Column;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.List;
import java.util.Map;

/**
 * Mybatis - 获取Mybatis查询sql工具
 *
 * @author zhangrenhua
 * @datetime 2022/1/14
 */
public final class SqlHelper {

    private SqlHelper() {
    }

    private static final String DROP_TABLE_SQL = "drop table if exists %s;";

    private static final String DROP_COLUMN_SQL = "alter table %s drop %s";

    private static final String MODIFY_COLUMN_SQL = "alter table %s modify %s  %s default null comment \"%s\"";

    private static final String ADD_COLUMN_SQL = "alter table %s add %s  %s default null comment \"%s\"";


    /**
     * 通过命名空间方式获取sql
     *
     * @param sqlSessionFactory
     * @param namespace
     * @param params
     * @return
     */
    public static String getNamespaceSql(SqlSessionFactory sqlSessionFactory, String namespace, Object params) {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        MappedStatement mappedStatement = configuration.getMappedStatement(namespace);
        TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        BoundSql boundSql = mappedStatement.getBoundSql(params);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        StringBuilder sqlStringBuilder = new StringBuilder(boundSql.getSql().replace("\n", ""));
        if (params != null && parameterMappings != null && !parameterMappings.isEmpty()) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (typeHandlerRegistry.hasTypeHandler(params.getClass())) {
                        value = params;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(params);
                        value = metaObject.getValue(propertyName);
                    }
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    if (jdbcType == null) {
                        jdbcType = JdbcType.VARCHAR;
                    }
                    replaceParameter(sqlStringBuilder, value, jdbcType);
                }
            }
        }
        return sqlStringBuilder.toString();
    }

    /**
     * 根据类型替换参数
     * 仅作为数字和字符串两种类型进行处理，需要特殊处理的可以继续完善这里
     *
     * @param sqlStringBuilder
     * @param value
     * @param jdbcType
     * @return
     */
    private static void replaceParameter(StringBuilder sqlStringBuilder, Object value, JdbcType jdbcType) {
        if (value == null) {
            return;
        }

        String strValue = String.valueOf(value);
        switch (jdbcType) {
            //数字
            case BIT:
                break;
            case TINYINT:
                break;
            case SMALLINT:
                break;
            case INTEGER:
                break;
            case BIGINT:
                break;
            case FLOAT:
                break;
            case REAL:
                break;
            case DOUBLE:
                break;
            case NUMERIC:
                break;
            case DECIMAL:
                break;
            //其他，包含字符串和其他特殊类型，加单引号
            default:
                strValue = "'" + strValue + "'";
        }
        int index = sqlStringBuilder.indexOf("?");
        sqlStringBuilder.replace(index, index + 1, strValue);
    }



    private static final String SMALLINT = "smallint";
    private static final String TINYINT = "tinyint";
    private static final String INT = "int";
    private static final String BIGINT = "bigint";
    private static final String FLOAT = "float";
    private static final String DOUBLE = "double";
    private static final String NUMERIC = "numeric";
    private static final String DECIMAL = "decimal";
    private static final String CHAR = "char";
    private static final String VARCHAR = "varchar";
    private static final String LONGVARCHAR = "longvarchar";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String TIMESTAMP = "timestamp";

    public static boolean isNumberType(Column column) {
        String dataType = column.getDataType();
        return (dataType.equalsIgnoreCase(SMALLINT) || dataType.equalsIgnoreCase(TINYINT)
                || dataType.equalsIgnoreCase(INT) || dataType.equalsIgnoreCase(BIGINT)
                || dataType.equalsIgnoreCase(FLOAT) || dataType.equalsIgnoreCase(DOUBLE)
                || dataType.equalsIgnoreCase(NUMERIC) || dataType.equalsIgnoreCase(DECIMAL));
    }

    public static boolean isCharType(Column column) {
        String dataType = column.getDataType();
        return dataType.equalsIgnoreCase(CHAR) || dataType.equalsIgnoreCase(VARCHAR)
                || dataType.equalsIgnoreCase(LONGVARCHAR);
    }

    public static boolean isTImeType(Column column) {
        String dataType = column.getDataType();
        return dataType.equalsIgnoreCase(DATE) || dataType.equalsIgnoreCase(TIME)
                || dataType.equalsIgnoreCase(TIMESTAMP);
    }


    public static String joinInsertSql(String tableName, List<Column> columns, Map<String, Object> data) {
        StringBuilder sql = new StringBuilder(StringUtils.EMPTY);
        if (MapUtils.isEmpty(data)) {
            return sql.toString();
        }
        sql.append("insert into ").append(tableName).append(" VALUES (");
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            if (isNumberType(column)) {
                sql.append(data.get(column.getColumnName()));
            } else if (isCharType(column)) {
                sql.append("'").append(data.get(column.getColumnName())).append("'");
            } else if (isTImeType(column)) {
                sql.append("timestamp'").append(data.get(column.getColumnName())).append("'");
            } else {
                sql.append("'").append(data.get(column.getColumnName())).append("'");
            }
            if (i != columns.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(");");
        return sql.toString();
    }

    public static String joinDropTableSql(String tableName) {
        return String.format(DROP_TABLE_SQL, tableName);
    }

    public static String joinDropColumnSql(String tableName, String columnName) {
        return String.format(DROP_COLUMN_SQL, tableName, columnName);
    }


    public static String joinUpdateSql(String tableName, String columnName, Column column, Map<String, Object> data) {
        StringBuilder sql = new StringBuilder(StringUtils.EMPTY);
        sql.append("update ").append(tableName).append(" set ").append(columnName).append("=");
        if (isNumberType(column)) {
            sql.append(data.get(column.getColumnName()));
        } else if (isCharType(column)) {
            sql.append("'").append(data.get(column.getColumnName())).append("'");
        } else if (isTImeType(column)) {
            sql.append("timestamp'").append(data.get(column.getColumnName())).append("'");
        } else {
            sql.append("'").append(data.get(column.getColumnName())).append("'");
        }
        sql.append(" WHERE `id` = " + data.get("id"));
        return sql.toString();
    }

    public static String joinModifyColumnSql(String tableName, String columnName, Column column) {
        return String.format(MODIFY_COLUMN_SQL, tableName, columnName, column.getColumnType(), column.getColumnComment());
    }

    public static String joinAddColumnSql(String tableName, String columnName, Column column, Column beforeColumn) {
        String sql = String.format(ADD_COLUMN_SQL, tableName, columnName, column.getColumnType(), column.getColumnComment());
        if (beforeColumn == null) {
            sql += " first";
        } else {
            sql += " after " + beforeColumn.getColumnName();
        }
        return sql;
    }


    public static final String CREATE_TABLE_PREFIX = "create table";
    public static final String DROP_TABLE_PREFIX = "drop table";
    public static final String ALTER_TABLE_PREFIX = "alter table";

    public static final String EXIST_PREFIX = "exists";

    public static final String TABLE_PREFIX = "table";
    public static final String ADD_COLUMN_PREFIX = "add";
    public static final String MODIFY_COLUMN_PREFIX = "modify";
    public static final String DROP_COLUMN_PREFIX = "drop";

    public static final String SHOW_TABLE_NAME = "Create Table";




    public static boolean isDDL(String sql) {
        sql = sql.trim().toLowerCase();
        if (sql.contains(CREATE_TABLE_PREFIX) || sql.contains(DROP_TABLE_PREFIX) || sql.contains(ALTER_TABLE_PREFIX)) {
            return true;
        }
        return false;
    }
}