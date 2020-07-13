package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.mapper")
@EnableTransactionManagement   //事务管理配置
public class SpringStarter {
	public static void main(String[] args) {
		SpringApplication.run(SpringStarter.class, args);
	}
}
