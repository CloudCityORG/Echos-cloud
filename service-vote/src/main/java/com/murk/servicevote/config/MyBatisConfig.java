package com.murk.servicevote.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.murk.servicevote.Mapper")
public class MyBatisConfig {
}
