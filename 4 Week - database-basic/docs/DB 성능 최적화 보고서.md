# 1. 개요

현재 MSA 구조로 분리된 e-commerce 서비스에서 조회 성능 저하가 예상되는 기능을 식별하고 최적화 방안을 제시합니다.

#### 분석 전제조건
- order-service는 현재 port를 통해 다른 서비스와 의존관계를 가지고 있으며, 향후 kafka 도입 시 분리 예정
- 서비스간 통신지연은 분석에서 제외
- DB 쿼리 성능에만 집중하여 분석
- 현재 테이블 구조는 대용량 처리를 고려하여 설계됨

<br>

# 2. 서비스별 성능 저하 예상 지점 분석

### 2.1. Product Service
#### 문제점 : 인기 상품 조회
```
SELECT p FROM ProductJpaEntity p ORDER BY p.stock ASC
```

#### 문제 원인
- ORDER BY로 인한 추가 정렬 작업
- 대용량 데이터에서 성능 저하 심화

### 2.2. Coupon Service
#### 문제점 : 쿠폰 발급 가능 여부 확인
```
// 구현 예상 쿼리
SELECT ce.* FROM coupon_event ce 
WHERE ce.expires_at > NOW() 
AND ce.remaining_quantity > 0
```

#### 문제 원인
- 전체 테이블 스캔으로 성능 저하
- 대용량 데이터에서 성능 저하 심화


# 3. 결론

Covering Index 적용하여 주요 조회 쿼리에서 모든 SELECT 컬럼을 인덱스에 포함하여 테이블 접근 최소화


