package com.jjs.ddl.example.service.impl;

import javax.annotation.Resource;

import com.jjs.ddl.example.mapper.DdlMapper;
import com.jjs.ddl.example.service.DdlTransactionExampleService;
import com.jjs.ddl.transaction.annotation.DdlTransactional;
import org.springframework.stereotype.Service;

/**
 * @author l2503
 * @date 2022-08-17
 */
@Service
public class DdlTransactionExampleServiceImpl implements DdlTransactionExampleService {

    @Resource
    private DdlMapper ddlMapper;

    @Override
    @DdlTransactional
    public void createTable(String tbName) throws Exception {
        ddlMapper.createNewTable(tbName);
        throw new Exception("自定义异常");
    }
}
