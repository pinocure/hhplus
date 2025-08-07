#!/bin/bash

echo "🚀 E2E 테스트 환경 시작"
echo "=================================="

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. 기존 컨테이너 정리
echo -e "${YELLOW}1. 기존 컨테이너 정리 중...${NC}"
docker-compose down -v
docker-compose rm -f

# 2. 각 서비스 빌드
echo -e "${YELLOW}2. 서비스 이미지 빌드 중...${NC}"
docker-compose build --parallel

# 3. 데이터베이스 먼저 시작 (초기 데이터 로드)
echo -e "${YELLOW}3. 데이터베이스 시작 및 초기 데이터 로드 중...${NC}"
docker-compose up -d mysql-balance mysql-coupon mysql-order mysql-product mysql-user

# 데이터베이스 준비 대기
echo "   데이터베이스 준비 대기 (30초)..."
sleep 30

# 4. 애플리케이션 서비스 시작
echo -e "${YELLOW}4. 애플리케이션 서비스 시작 중...${NC}"
docker-compose up -d balance-service coupon-service product-service user-service
sleep 10
docker-compose up -d order-service
sleep 10
docker-compose up -d api-gateway

# 서비스 준비 대기
echo "   서비스 준비 대기 (20초)..."
sleep 20

# 5. E2E 테스트 실행
echo -e "${YELLOW}5. E2E 테스트 실행 중...${NC}"
echo "=================================="

# 테스트 컨테이너 실행 및 결과 수집
docker-compose run --rm e2e-test

# 종료 코드 저장
TEST_EXIT_CODE=$?

# 6. 테스트 결과 출력
echo "=================================="
if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}✅ E2E 테스트 성공!${NC}"
else
    echo -e "${RED}❌ E2E 테스트 실패!${NC}"
fi

# 7. 결과 파일 확인
if [ -f "./e2e-tests/results/test-results.json" ]; then
    echo -e "${YELLOW}📊 테스트 결과 상세:${NC}"
    cat ./e2e-tests/results/test-results.json
fi

# 8. 환경 정리
echo -e "${YELLOW}8. 테스트 환경 정리 중...${NC}"
docker-compose down -v

# 9. 최종 결과
echo "=================================="
if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}🎉 모든 테스트가 성공적으로 완료되었습니다!${NC}"
else
    echo -e "${RED}💥 테스트 실패! 로그를 확인하세요.${NC}"
fi

# 종료 코드 반환
exit $TEST_EXIT_CODE







