# E2E 테스트 Makefile

.PHONY: help test clean build up down logs

# 기본 명령어
help:
	@echo "사용 가능한 명령어:"
	@echo "  make test    - E2E 테스트 실행 (전체 프로세스)"
	@echo "  make build   - Docker 이미지 빌드"
	@echo "  make up      - 서비스 시작"
	@echo "  make down    - 서비스 중지 및 정리"
	@echo "  make logs    - 서비스 로그 확인"
	@echo "  make clean   - 모든 컨테이너, 볼륨, 이미지 정리"

# E2E 테스트 실행
test:
	@echo "E2E 테스트 시작..."
	@chmod +x run-e2e-test.sh
	@./run-e2e-test.sh

# Docker 이미지 빌드
build:
	@echo "Docker 이미지 빌드 중..."
	@docker-compose build --parallel

# 서비스 시작
up:
	@echo "서비스 시작 중..."
	@docker-compose up -d
	@echo "모든 서비스가 시작되었습니다."
	@echo "API Gateway: http://localhost:8090"

# 서비스 중지
down:
	@echo "서비스 중지 중..."
	@docker-compose down -v
	@echo "모든 서비스가 중지되었습니다."

# 로그 확인
logs:
	@docker-compose logs -f

# 완전 정리
clean:
	@echo "환경 정리 중..."
	@docker-compose down -v --rmi all --remove-orphans
	@docker system prune -af
	@echo "정리 완료"

# 개별 서비스 테스트
test-balance:
	@echo "Balance 서비스 테스트..."
	@curl -X GET http://localhost:8081/balances/1

test-coupon:
	@echo "Coupon 서비스 테스트..."
	@curl -X GET http://localhost:8082/coupons/1

test-order:
	@echo "Order 서비스 테스트..."
	@curl -X GET http://localhost:8080/orders/1

test-product:
	@echo "Product 서비스 테스트..."
	@curl -X GET http://localhost:8083/products

test-user:
	@echo "User 서비스 테스트..."
	@curl -X GET http://localhost:8084/users/1














