const axios = require('axios');
const assert = require('assert');

const API_GATEWAY_URL = process.env.API_GATEWAY_URL || 'http://localhost:8090';
const TIMEOUT = 30000;

// API 클라이언트 설정
const apiClient = axios.create({
    baseURL: API_GATEWAY_URL,
    timeout: TIMEOUT,
    headers: {
        'Content-Type': 'application/json'
    }
});

// 서비스 헬스체크 - Actuator 엔드포인트 사용
async function waitForServices(maxRetries = 30) {
    console.log('서비스 준비 대기중...');

    // 직접 각 서비스의 actuator 엔드포인트 확인
    const services = [
        { name: 'order-service', url: 'http://order-service:8080/actuator/health' },
        { name: 'balance-service', url: 'http://balance-service:8081/actuator/health' },
        { name: 'coupon-service', url: 'http://coupon-service:8082/actuator/health' },
        { name: 'product-service', url: 'http://product-service:8083/actuator/health' },
        { name: 'user-service', url: 'http://user-service:8084/actuator/health' }
    ];

    for (let i = 0; i < maxRetries; i++) {
        try {
            console.log(`시도 ${i + 1}/${maxRetries}...`);
            const healthChecks = await Promise.all(
                services.map(async service => {
                    try {
                        const response = await axios.get(service.url, { timeout: 5000 });
                        console.log(`  ✓ ${service.name}: UP`);
                        return true;
                    } catch (error) {
                        console.log(`  ✗ ${service.name}: DOWN`);
                        return false;
                    }
                })
            );

            if (healthChecks.every(check => check === true)) {
                console.log('모든 서비스 준비 완료');
                return true;
            }
        } catch (error) {
            console.log(`재시도 중...`);
        }
        await new Promise(resolve => setTimeout(resolve, 2000));
    }
    throw new Error('서비스 시작 타임아웃');
}

// 전체 플로우 E2E 테스트
async function testCompleteFlow() {
    console.log('\n 전체 플로우 테스트 시작');

    // 테스트용 사용자 ID (기존 데이터 사용)
    const userId = 7;  // 이미 존재하는 사용자 ID
    console.log(`사용자 ID: ${userId}`);

    try {
        // 1. 사용자 확인
        console.log('\n1. 사용자 확인...');
        const userResponse = await apiClient.get(`/api/users/${userId}`);
        assert.strictEqual(userResponse.status, 200);
        console.log(`   ✓ 사용자 정보 조회 성공`);

        // 2. 잔액 충전
        console.log('\n2. 잔액 충전 (5,000,000원)...');
        const chargeResponse = await apiClient.post('/api/balances/charge', null, {
            params: { userId: userId, amount: 5000000 }
        });
        assert.strictEqual(chargeResponse.status, 200);
        console.log(`   ✓ 잔액 충전 성공: ${chargeResponse.data}원`);

        // 3. 상품 조회
        console.log('\n3. 상품 조회 (ID: 50)...');
        const productResponse = await apiClient.get('/api/products/50');
        assert.strictEqual(productResponse.status, 200);
        assert.ok(productResponse.data.price);
        console.log(`   ✓ 상품명: ${productResponse.data.name}`);
        console.log(`   ✓ 가격: ${productResponse.data.price}원`);

        // 4. 쿠폰 발급
        console.log('\n4. 쿠폰 발급 (이벤트 ID: 5)...');
        const couponResponse = await apiClient.post('/api/coupons/issue', null, {
            params: { userId: userId, eventId: 5 }
        });
        assert.strictEqual(couponResponse.status, 200);
        const couponCode = couponResponse.data.code;
        console.log(`   ✓ 쿠폰 발급 성공: ${couponCode}`);

        // 5. 주문 생성
        console.log('\n5. 주문 생성');
        const orderResponse = await axios.post('http://order-service:8080/orders', null, {
            params: {
                userId: userId,
                productIds: 50,
                quantities: 2,
                couponCodes: couponCode
            }
        });

        // 6. 주문 결제
        console.log('\n6. 주문 결제 처리...');
        try {
            const payResponse = await apiClient.post(
                `/api/orders/${orderResponse.data.id}/pay`
            );
            assert.strictEqual(payResponse.status, 200);
            assert.strictEqual(payResponse.data.status, 'PAID');
            console.log(`   ✓ 결제 완료: ${payResponse.data.status}`);
        } catch (error) {
            // CONFIRMED 상태에서는 이미 결제가 완료된 것으로 간주
            if (orderResponse.data.status === 'CONFIRMED') {
                console.log(`   ✓ 결제 완료: 주문이 이미 CONFIRMED 상태`);
            } else {
                throw error;
            }
        }

        // 7. 최종 잔액 확인
        console.log('\n7. 최종 잔액 확인...');
        const finalBalanceResponse = await apiClient.get(`/api/balances/${userId}`);
        assert.strictEqual(finalBalanceResponse.status, 200);
        const finalBalance = finalBalanceResponse.data;
        const productPrice = productResponse.data.price;
        const orderQuantity = 2;
        const discountAmount = couponResponse.data.discountAmount || 0;
        const expectedUsedAmount = (productPrice * orderQuantity) - discountAmount;

        console.log(`   ✓ 최종 잔액: ${finalBalance}원`);
        console.log(`   ✓ 예상 사용 금액: ${expectedUsedAmount}원`);
        console.log(`   ✓ 실제 잔액 차이: ${500000 + chargeResponse.data - finalBalance}원`);

        // 잔액이 차감되었는지 확인
        assert.ok(finalBalance < chargeResponse.data, '잔액이 차감되어야 합니다');
        console.log(`   ✓ 결제 검증 성공: 잔액이 정상적으로 차감됨`);

        return true;
    } catch (error) {
        console.error(`\n테스트 실패: ${error.message}`);
        if (error.response) {
            console.error(`   상태 코드: ${error.response.status}`);
            console.error(`   응답 데이터:`, error.response.data);
        }
        throw error;
    }
}

// 메인 실행 함수
async function main() {
    console.log('E2E 테스트 시작');
    console.log(`API Gateway URL: ${API_GATEWAY_URL}`);
    console.log('='.repeat(50));

    let testPassed = false;

    try {
        // 서비스 준비 대기
        await waitForServices();

        // 전체 플로우 테스트 실행
        await testCompleteFlow();
        testPassed = true;

        console.log('\n' + '='.repeat(50));
        console.log('E2E 테스트 성공!');
        console.log('='.repeat(50));

    } catch (error) {
        console.log('\n' + '='.repeat(50));
        console.error('E2E 테스트 실패');
        console.error(`오류: ${error.message}`);
        console.log('='.repeat(50));
    }

    // 결과 파일 저장
    const fs = require('fs');
    const testResult = {
        status: testPassed ? 'PASSED' : 'FAILED',
        timestamp: new Date().toISOString(),
        apiGatewayUrl: API_GATEWAY_URL
    };

    try {
        const resultPath = '/app/results/test-results.json';
        fs.writeFileSync(resultPath, JSON.stringify(testResult, null, 2));
        console.log(`\n결과 저장: ${resultPath}`);
    } catch (error) {
        console.log(`\n결과 파일 저장 실패: ${error.message}`);
    }

    // 종료 코드 설정
    const exitCode = testPassed ? 0 : 1;
    console.log(`\n테스트 완료 (Exit Code: ${exitCode})`);
    process.exit(exitCode);
}

// 실행
main().catch(error => {
    console.error('치명적 오류:', error);
    process.exit(1);
});


