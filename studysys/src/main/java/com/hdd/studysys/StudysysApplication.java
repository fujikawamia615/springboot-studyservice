package com.hdd.studysys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.hdd.studysys.mapper")
@EnableCaching
public class StudysysApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudysysApplication.class, args);
	}

}
