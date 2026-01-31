#!/bin/bash

# 액세스 토큰 가져오기
ACCESS_TOKEN=$(curl -s \
  "http://metadata.google.internal/computeMetadata/v1/instance/service-accounts/default/token" \
  -H "Metadata-Flavor: Google" | cut -d'"' -f4)

# 임시 config 디렉토리 생성 (writable 영역 사용)
mkdir -p /var/lib/docker-config
export DOCKER_CONFIG=/var/lib/docker-config

# Docker 로그인
echo $ACCESS_TOKEN | docker login -u oauth2accesstoken --password-stdin https://us-east1-docker.pkg.dev

FULL_IMAGE_NAME="{{IMAGE_NAME}}"  # <--- 여기가 파이프라인에 의해 바뀝니다!

docker stop my-app || true
docker rm my-app || true

# 컨테이너 실행
docker run -d \
  --name my-app \
  --restart unless-stopped \
  -p 8080:8080 \
  "${FULL_IMAGE_NAME}"