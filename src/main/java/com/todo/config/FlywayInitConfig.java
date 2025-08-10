package com.todo.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Flyway를 사용하여 애플리케이션 실행시마다 데이터베이스를 초기화하기 위한 설정.
 *
 * <li>create 처럼 동작하도록 구현함</li>
 * <li>만약 데이터베이스 초기화를 원하지 않는다면 설정을 비활성화 해야함</li>
 * */
@Configuration
public class FlywayInitConfig {

    @Bean
    @Order(1)   // 제일 먼저 실행되도록 설정
    public ApplicationRunner init(Flyway flyway) {
        return args -> {
            // 모든 테이블 드롭
            flyway.clean();

            // 다시 마이그레이션
            flyway.migrate();
        };
    }
}