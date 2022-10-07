package com.coderman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author zhangyukang
 * @Date 2020/12/15 13:14
 * @Version 1.0
 **/
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement  //开启事务管理
@MapperScan("com.coderman.*.mapper") //扫描mapper
public class ManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageApplication.class, args);
    }
}
