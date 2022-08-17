package com.jjs.ddl.transaction.support;

import com.jjs.ddl.transaction.service.UndoLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @Description: ddl切面
 * @Version: 1.0
 * @Author: zhangrenhua
 * @Date: Created in 10:11 2022/1/13
 */
@Aspect
@Component
@Slf4j
public class DDLTransactionalAspect {

    @Resource
    private UndoLogService undoLogService;


    @Pointcut("@annotation(com.jjs.ddl.transaction.annotation.DdlTransactional)")
    public void pointcut() {
        System.out.println("test1111111111");;
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String txId = ContextHolder.getTxId();
        if(StringUtils.isNotBlank(txId)){
            return point.proceed();
        }
        Object result;
        try {
            txId = UUID.randomUUID().toString().replace("-", "");
            ContextHolder.putTxId(txId);
            result = point.proceed();
        }catch (Throwable e){
            undoLogService.execUndoLog();
            throw e;
        }
        return result;
    }

}
