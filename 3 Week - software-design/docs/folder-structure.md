# 목차
#### 1. 아키텍처
#### 2. 폴더 구조
#### 3. 참고 자료

<br>

---


# 1. 아키텍처
> 헥사고날 아키텍처(Hexagonal Architecture)를 기반으로 폴더 구조를 설계하였음

- **Domain/Business Logic**: 핵심 비즈니스 규칙 (육각형 중앙)
- **Ports**: 비즈니스가 정의한 인터페이스
- **Adapters**: 외부 시스템과의 실제 연결 구현

### 예시 구조
```
    pinocure
    ├── domain
    ├── application(port)
    └── adapter
```

<br>

# 2. 폴더 구조

### 1. 기본 구조
```
    com.hhplus.ecommerce
    ├── balance
    ├── coupon
    ├── order
    ├── product
    └── user
```
```
    com.hhplus.ecommerce
    ├── balance
    │   ├── adapter
    │   ├── application
    │   └── domain
    ├── coupon
    │   ├── adapter
    │   ├── application
    │   └── domain
    ├── order
    │   ├── adapter
    │   ├── application
    │   └── domain
    ├── product
    │   ├── adapter
    │   ├── application
    │   └── domain
    └── user
        ├── adapter
        ├── application
        └── domain
```

### 2. user
```
    user
    ├── application
    │   ├── port
    │   │   ├── in
    │   │   │   └── UserUseCase.java    // 사용자 관련 유즈케이스 인터페이스
    │   │   └── out
    │   │       ├── LoadUserPort.java   // 사용자 조회 포트
    │   │       ├── SaveUserPort.java   // 사용자 저장 포트
    │   │       └── UserRepository.java // 사용자 저장소 인터페이스
    │   └── service
    │       └── UserService.java        // 사용자 유즈케이스 조율 및 흐름 관리
    ├── adapter
    │   ├── in
    │   │   └── web
    │   │       └── UserController.java     // HTTP 요청 처리
    │   └── out
    │       └── persistence
    │           └── UserRepositoryAdapter.java // 데이터베이스 연결 구현
    └── domain
        └── User.java                   // 사용자 도메인 엔티티
```

### 3. Balance
```
    balance
    ├── application
    │   ├── port
    │   │   ├── in
    │   │   │   └── BalanceUseCase.java     // 잔액 관련 유즈케이스 인터페이스
    │   │   └── out
    │   │       ├── LoadBalancePort.java    // 잔액 조회 포트
    │   │       ├── SaveBalancePort.java    // 잔액 저장 포트
    │   │       └── BalanceRepository.java  // 잔액 저장소 인터페이스
    │   └── service
    │       └── BalanceService.java         // 잔액 유즈케이스 조율 및 흐름 관리
    ├── adapter
    │   ├── in
    │   │   └── web
    │   │       └── BalanceController.java  // 잔액 API 엔드포인트
    │   └── out
    │       └── persistence
    │           └── BalanceRepositoryAdapter.java // 잔액 데이터 영속화
    └── domain
        └── Balance.java                    // 잔액 도메인 엔티티
```

### 4. Product
```
    product
    ├── application
    │   ├── port
    │   │   ├── in
    │   │   │   └── ProductUseCase.java     // 상품 관련 유즈케이스 인터페이스
    │   │   └── out
    │   │       ├── LoadProductPort.java    // 상품 조회 포트
    │   │       ├── SaveProductPort.java    // 상품 저장 포트
    │   │       └── ProductRepository.java  // 상품 저장소 인터페이스
    │   └── service
    │       └── ProductService.java         // 상품 유즈케이스 조율 및 흐름 관리
    ├── adapter
    │   ├── in
    │   │   └── web
    │   │       └── ProductController.java  // 상품 API 엔드포인트
    │   └── out
    │       └── persistence
    │           └── ProductRepositoryAdapter.java // 상품 데이터 영속화
    └── domain
        └── Product.java                    // 상품 도메인 엔티티
```

### 5. Coupon
```
    coupon
    ├── application
    │   ├── port
    │   │   ├── in
    │   │   │   └── CouponUseCase.java      // 쿠폰 관련 유즈케이스 인터페이스
    │   │   └── out
    │   │       ├── LoadCouponPort.java     // 쿠폰 조회 포트
    │   │       ├── SaveCouponPort.java     // 쿠폰 저장 포트
    │   │       └── CouponRepository.java   // 쿠폰 저장소 인터페이스
    │   └── service
    │       └── CouponService.java          // 쿠폰 유즈케이스 조율 및 흐름 관리
    ├── adapter
    │   ├── in
    │   │   └── web
    │   │       └── CouponController.java   // 쿠폰 API 엔드포인트
    │   └── out
    │       └── persistence
    │           └── CouponRepositoryAdapter.java // 쿠폰 데이터 영속화
    └── domain
        ├── Coupon.java                     // 쿠폰 도메인 엔티티
        └── CouponEvent.java                // 쿠폰 이벤트 도메인 엔티티
```

### 6. Order
```
    order
    ├── application
    │   ├── port
    │   │   ├── in
    │   │   │   └── OrderUseCase.java       // 주문 관련 유즈케이스 인터페이스
    │   │   └── out
    │   │       ├── LoadOrderPort.java      // 주문 조회 포트
    │   │       ├── SaveOrderPort.java      // 주문 저장 포트
    │   │       └── OrderRepository.java    // 주문 저장소 인터페이스
    │   └── service
    │       └── OrderService.java           // 주문 유즈케이스 조율 및 흐름 관리
    ├── adapter
    │   ├── in
    │   │   └── web
    │   │       └── OrderController.java    // 주문 API 엔드포인트
    │   └── out
    │       └── persistence
    │           └── OrderRepositoryAdapter.java // 주문 데이터 영속화
    └── domain
        ├── Order.java                      // 주문 도메인 엔티티
        ├── OrderItem.java                  // 주문 항목 도메인 엔티티
        └── OrderCoupon.java                // 주문 쿠폰 도메인 엔티티
```

<br>

# 3. 참고 자료

1. 온라인 트리 구조 생성 : https://tree.nathanfriend.com/
2. 항해 플러스 백엔드 코스 9기 발제 자료 > 헥사고날 아키텍처 > 언제 사용하면 좋을까? > 폴더 구조