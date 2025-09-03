package com.himartclone.mybatis.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * MyBatis 설정 클래스
 * MyBatis와 Spring의 통합 설정을 담당
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.himartclone.mybatis.repository")
public class MyBatisConfig {
    
    /**
     * SqlSessionFactory Bean 생성
     * MyBatis의 핵심 컴포넌트로 SQL 세션을 생성하는 팩토리
     * 
     * @param dataSource 데이터베이스 연결을 위한 DataSource
     * @return SqlSessionFactory 인스턴스
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        
        // 데이터소스 설정
        sessionFactory.setDataSource(dataSource);
        
        // 타입 별칭 패키지 설정 (VO 클래스들의 패키지)
        sessionFactory.setTypeAliasesPackage("com.himartclone.mybatis.domain");
        
        // MyBatis XML 매퍼 파일 위치 설정
        sessionFactory.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/*.xml")
        );
        
        return sessionFactory.getObject();
    }
}
