package com.jjs.ddl.transaction.support;


import com.jjs.ddl.transaction.util.SqlHelper;
import com.jjs.ddl.transaction.service.UndoLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: ddl切面
 * @Version: 1.0
 * @Author: zhangrenhua
 * @Date: Created in 10:11 2022/1/13
 */
@Aspect
@Component
@Slf4j
public class DDLAspect {

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Resource
    private UndoLogService undoLogService;


    @Pointcut("execution(* com..mapper.*Mapper.*(..)) && !execution(* com..dao.UndoLogMapper.*(..)) && !execution(* com.baomidou.mybatisplus.core.mapper.BaseMapper.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        List<String> ddlSqlList = null;
        try {
            if(StringUtils.isNotBlank(ContextHolder.getTxId())){
                MethodSignature signature = (MethodSignature) point.getSignature();
                String path = signature.getMethod().getDeclaringClass().getName();
                String methodName = point.getSignature().getName();
                //参数
                Object[] args = point.getArgs();
                String[] parameterNames = signature.getParameterNames();
                Map<String,Object> paramMap = new HashMap<>();
                if(parameterNames != null && parameterNames.length > 0 && args.length > 0){
                    for (int i = 0; i < parameterNames.length; i++) {
                        paramMap.put(parameterNames[i],args[i]);
                    }
                }
                String sql = SqlHelper.getNamespaceSql(sqlSessionFactory, path + "." + methodName, paramMap);
                if(StringUtils.isNotBlank(sql) && SqlHelper.isDDL(sql)){
                    ddlSqlList = undoLogService.generateRollbackSql(sql);
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }finally {
            result = point.proceed();
        }
        if(CollectionUtils.isNotEmpty(ddlSqlList)){
            try {
                undoLogService.saveUndoLog(ddlSqlList);
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }
        return result;
    }



}
