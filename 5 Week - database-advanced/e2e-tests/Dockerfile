FROM node:18-alpine

WORKDIR /app

# package.json 복사 및 의존성 설치
COPY package*.json ./
RUN npm install

# 테스트 파일 복사
COPY test/ ./test/

# 결과 디렉토리 생성
RUN mkdir -p /app/results

# 테스트 실행
CMD ["npm", "test"]









