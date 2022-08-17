package com.jjs.ddl.example.service;

import com.jjs.ddl.transaction.annotation.DdlTransactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author l2503
 * @date 2021-11-30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DdlTransactionExampleServiceTest {

    @Autowired
    private DdlTransactionExampleService ddlTransactionExampleService;

    @Test
    @DdlTransactional
    public void saveEmptyTableTest() throws Exception {
        ddlTransactionExampleService.createTable("test1");
    }

}
