# Eighteen_BE

![개발 서버 CI / CD 현황](https://github.com/LayTheGroundWork/Eighteen_BE/actions/workflows/dockerize-on-dev.yml/badge.svg)

## docker-compose 실행 방법

1. `/gradlew build -x test` 명령어를 실행하여 빌드합니다.( 설정파일 복사 관련 )

2. `docker-compose.yml` 파일이 있는 디렉토리로 이동합니다.

3. 다음 명령어를 실행합니다. (env 환경에 맞게 설정필)
    ```shell
    docker-compose --env-file .env.local up -d
    ```

4. 서비스를 종료하려면 다음 명령어를 실행합니다.
    ```shell
   docker-compose -p eighteen_be down
    ```

## Eighteen: 소셜 플랫폼 소개

Eighteen은 청소년들에게 소속감을 제공하고, 주목받고 싶은 욕구를 충족시키는 소셜 플랫폼입니다. 사용자들은 자신의 개성과 성취를 공유함으로써 소셜 미디어 상에서 인기를 높일 수 있습니다.

## 주요 특징

- **개성과 성취 공유**: 사용자들은 자신만의 독특한 경험과 성취를 공유할 수 있습니다.
- **상호 인정**: 긍정적인 상호작용을 통해 서로를 인정하고 격려합니다.
- **커뮤니티 유대감 강화**: 공통의 관심사를 바탕으로 한 커뮤니티 내에서 유대감을 형성합니다.
- **자신감 향상**: 상호작용을 통해 자신감을 키우고, 사회적 기술을 발달시킬 수 있습니다.

Eighteen은 청소년들이 사회적 상호작용을 통해 성장하고 발전할 수 있는 환경을 제공합니다.

## 기술 스택

### 프로그래밍 언어 및 버전

- Java 17

### 프레임워크

- Spring Boot 3.2.3

### 빌드 도구

- Gradle 7.x

### ORM

- Spring Data JPA
- QueryDSL 5.0

### 데이터베이스

- H2 1.4.x
- MySQL 8.0.x

### 개발 환경

- IDE: IntelliJ IDEA