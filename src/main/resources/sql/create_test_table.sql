-- MyBatis 테스트용 테이블 생성 스크립트
-- H2 데이터베이스용 (MySQL 호환 모드)

-- 기존 테이블이 있다면 삭제
DROP TABLE IF EXISTS Test_Tb;

-- Test_Tb 테이블 생성
CREATE TABLE Test_Tb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '기본키 - 자동증가',
    test_name VARCHAR(100) NOT NULL COMMENT '테스트 이름',
    test_description VARCHAR(500) COMMENT '테스트 설명',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시'
);

-- 인덱스 생성 (성능 향상을 위해)
CREATE INDEX idx_test_tb_test_name ON Test_Tb(test_name);
CREATE INDEX idx_test_tb_created_at ON Test_Tb(created_at);

-- 샘플 데이터 삽입 (테스트용)
INSERT INTO Test_Tb (test_name, test_description) VALUES 
('테스트1', '첫 번째 테스트 데이터입니다.'),
('테스트2', '두 번째 테스트 데이터입니다.'),
('MyBatis 테스트', 'MyBatis 연동 테스트용 데이터입니다.');

-- 생성된 테이블 확인
SELECT * FROM Test_Tb ORDER BY created_at;
