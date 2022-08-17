# ddlTransaction 

ddl 语句事务注解：通过注解拦截执行的DDL语句，将要执行的DDL语句生成反向语句（例如要执行新增字段，则生成删除字段SQL）存入日志表中，当事务失败，则执行任务表中生成的DDL语句，以达到事务回滚的效果。

## 1.pom.xml引入
```xml
 <dependency>
    <groupId>com.jjs.ddl</groupId>
    <artifactId>ddl-transaction-starter</artifactId>
    <version>1.1.0-RELEASE</version>
    <exclusions>
        <exclusion>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
        </exclusion>
    </exclusions>
 </dependency>
```


## 2.mapper及自定义注解扫描路径
```java
@MapperScan(basePackages = {"com.jjs.ddl.transaction.dao"})
@ComponentScan(basePackages = {"com.jjs.ddl.transaction"})
public class Application{
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

```yml
mybatis-plus:
  mapper-locations: classpath:com/jjs/ddl/transaction/dao/xml/*.xml

```

## 3.执行SQL
```
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

```

## 4.使用说明，方法上新增注解@DdlTransactional
```text
    @Override
    @DdlTransactional
    public void createTable(String tbName) throws Exception {
        ddlMapper.createNewTable(tbName);
        throw new Exception("自定义异常");
    }
```

### 项目结构
    
    |---ddl-transaction
       |---ddl-transaction-starter: DDLTransaction实现项目
       |---ddl-transaction-example: 示例项目 