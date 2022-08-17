package com.jjs.ddl.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * undo_log
 * @author 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UndoLog implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 事务id
     */
    private String txId;

    /**
     * SQL语句
     */
    private String sql;

    /**
     * 0 未执行  1 已执行 2 执行失败
     */
    private Integer status;

    /**
     * 执行时间
     */
    private Date runTime;

    /**
     * 是否删除
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 修改者
     */
    private Date updateTime;

    /**
     * 修改者
     */
    private String modifier;

    private static final long serialVersionUID = 1L;
}