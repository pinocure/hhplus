# 프로젝트 개요
### 유형 : 이커머스 플랫폼
### 아키텍처 : 헥사고날 + MSA 아키텍처


<br>

# Concurrency 과제 (STEP 09)

**동시성 문제 분석 및 적합한 DB Lock 기반 동시성 제어 로직 구현 및 검증**

### 🎯 과제 목표

자신의 서비스 시나리오에서 발생할 수 있는 **동시성 이슈(Race Condition, Deadlock 등)**를 파악하고,

해결을 위한 RDBMS 기반의 **동시성 제어 방식(DB Lock, Optimistic/Pessimistic Lock)**을 학습합니다.

각 방식의 특징과 장단점을 비교하며, **자신의 비즈니스 로직에 가장 적합한 방식**을 선정하고 설계안을 문서화합니다.

### 🛠️ 핵심 기술 키워드

- DB 동시성 제어 (Transaction Isolation, Lock)
- S-Lock, X-Lock
- Optimistic Lock / Pessimistic Lock
- 동시성 테스트 시나리오 설계


<br>

# Finalize 과제 (STEP 10)

**통합 테스트 및 예외 상황 기반의 시나리오 테스트 구현**

### 🎯 과제 목표

각 기능을 REST API 로 제공할 수 있도록 누락된 기능, 테스트 등을 보완하고 애플리케이션이 정상적으로 기대하는 기능을 제공할 수 있도록 합니다.

기능이 정상적으로 동작하는 지 등을 검증할 수 있는 통합/E2E 테스트를 작성하고 애플리케이션이 배포가능한 수준을 확보할 수 있도록 합니다.

### 🛠️ 핵심 기술 키워드

- 통합 테스트: JUnit / Jest / Supertest / Spring Test
- Mocking: Mockito / MockK / 테스트용 Stub

