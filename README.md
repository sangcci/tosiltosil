# 🐰 토실토실 (Tosiltosil) - Backend

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8.13-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-010101?style=for-the-badge&logo=socket.io&logoColor=white)

**목표 달성을 위한 스톱워치 기반 시간 관리 애플리케이션**

</div>

---

## 📋 서비스 소개

<div align="center">
<table>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/a7441511-0e18-4bfd-a497-8319ab78ffd2" width="300" alt="캘린더 및 목표 설정"/></td>
    <td><img src="https://github.com/user-attachments/assets/18c959a5-810b-4673-8791-0f1e841a77ac" width="300" alt="카테고리 관리"/></td>
    <td><img src="https://github.com/user-attachments/assets/01edb816-534d-4fab-8236-43c7aa7d161b" width="300" alt="스톱워치 및 진행률"/></td>
    <td><img src="https://github.com/user-attachments/assets/715604e6-cf4f-4c86-9a12-b679f17df3f8" width="300" alt="목표 상세 및 통계"/></td>
  </tr>
</table>
</div>

> **자신의 목표를 설정하고 친구와 경쟁하며 목표를 성취하는 서비스**

항상 자신이 해야 하는 과제나 프로젝트가 있는 대학생이나 자신이 맡고 있는 일을 해야 하는 직장인, 또는 목표가 있는 사용자가 자신이 생각하는 데드라인에 맞춰 목표를 설정하지만 막상 벼락치기를 하거나 혹은 귀찮아서 목표를 이루지 못하는 경우가 많습니다.

이러한 상황에서 **자극이 될 수 있는 친구와 같이 목표를 설정하여 공유**를 하면 재미와 성취감을 배로 느낄 수 있을거라고 판단하여 이 서비스를 제공하고자 합니다.

### 🎯 서비스 타깃

- 투두리스트나 일정을 꾸미고 친구와 같이 공유를 즐기는 **대학생**
- 회사 내부 직원들과 계속 일정을 공유하는 것에 지루함을 느끼는 **직장인**
- 특히 **2030 여성**을 주요 타깃으로 설정

---

## ✨ Features

### 🔐 Authentication & Security
- **JWT 토큰 기반 인증**: Access Token + Refresh Token 구조
- **이메일 인증**: 비동기 이메일 발송 및 인증번호 검증
- **Rate Limiting**: 이메일 발송 및 인증 시도 횟수 제한
- **쿠키 기반 토큰 관리**: HttpOnly, Secure, SameSite 설정

### 📁 Category Management
- 사용자별 최대 10개 카테고리 생성
- Fractional Indexing 기반 순서 관리
- 월별 카테고리 색상 조회

### 🎯 Goal Management
- 다중 날짜 목표 일괄 생성
- 카테고리별 목표 그룹화
- 실시간 진행률 계산
- 목표 상태 관리 (시작 전 → 진행 중 → 완료/실패)

### ⏱️ Stopwatch System (개발 중)
- WebSocket(STOMP) 기반 실시간 동기화
- 이벤트 드리븐 아키텍처
- 일일 총 진행 시간 Redis 캐싱

### 📊 Progress Tracking
- Redis 기반 일일 진행 시간 관리
- 목표 달성률 자동 계산
- 카테고리/목표 삭제 시 자동 시간 차감

### 👥 Friend System (개발 중)
- 친구 추가 및 관계 관리
- 친구 진행 상황 공유
- 경쟁을 통한 동기부여

---

## 🛠 Tech Stack

### Backend Framework
| Technology | Purpose |
|------------|---------|
| **Java 17** | Primary Language |
| **Spring Boot 3.x** | Application Framework |
| **Spring Security** | Authentication & Authorization |
| **Spring Data JPA** | ORM & Data Access |
| **QueryDSL** | Type-safe Query |
| **Spring WebSocket** | Real-time Communication |

### Database & Cache
| Technology | Purpose |
|------------|---------|
| **MySQL** | Primary Database |
| **Redis** | Token Storage, Session, Caching |

### Infrastructure & DevOps
| Technology | Purpose |
|------------|---------|
| **Docker** | Containerization |
| **GitHub Actions** | CI/CD Pipeline |
| **Gradle** | Build Tool |
| **Spring REST Docs** | API Documentation |

### Testing
| Technology | Purpose |
|------------|---------|
| **JUnit 5** | Unit Testing |
| **Mockito** | Mocking Framework |
| **Spring Test** | Integration Testing |

---

## 🏗 Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client Layer                            │
│                    (Web / Mobile App)                           │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      API Gateway Layer                          │
│              (Spring Security + JWT Filter)                     │
└─────────────────────────────┬───────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐   ┌─────────────────┐   ┌─────────────────┐
│   REST API    │   │    WebSocket    │   │   Async Jobs    │
│  Controllers  │   │    (STOMP)      │   │  (Email Send)   │
└───────┬───────┘   └────────┬────────┘   └────────┬────────┘
        │                    │                     │
        └────────────────────┼─────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Service Layer                              │
│     (Business Logic + Domain Services + Event Handlers)         │
└─────────────────────────────┬───────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐   ┌─────────────────┐   ┌─────────────────┐
│  JPA/QueryDSL │   │     Redis       │   │  Event System   │
│  Repository   │   │   Repository    │   │   (Spring)      │
└───────┬───────┘   └────────┬────────┘   └─────────────────┘
        │                    │
        ▼                    ▼
┌───────────────┐   ┌─────────────────┐
│    MySQL      │   │     Redis       │
│   Database    │   │     Cache       │
└───────────────┘   └─────────────────┘
```

### Module Structure

```
src/main/java/tosiltosil/backend/
├── common/                          # 공통 모듈
│   ├── auth/                        # JWT 인증/인가
│   │   ├── filter/                  # Security Filter
│   │   ├── resolver/                # Argument Resolver
│   │   └── util/                    # JWT Utility
│   ├── domain/                      # 공통 도메인
│   │   ├── exception/               # Custom Exceptions
│   │   ├── holder/                  # Time Holder (테스트 용이성)
│   │   ├── order/                   # Fractional Ordering
│   │   └── validator/               # Custom Validators
│   ├── infrastructure/              # 인프라 설정
│   ├── logging/                     # 구조화된 로깅
│   ├── messaging/                   # 이벤트 시스템
│   └── web/                         # Web 설정 및 핸들러
│
└── module/                          # 비즈니스 모듈
    ├── auth/                        # 인증 모듈
    ├── member/                      # 회원 모듈
    ├── category/                    # 카테고리 모듈
    ├── goal/                        # 목표 모듈
    ├── stopwatch/                   # 스톱워치 모듈
    ├── progress/                    # 진행률 모듈
    ├── friend/                      # 친구 모듈 (개발 중)
    ├── terms/                       # 약관 모듈
    └── email/                       # 이메일 모듈
```

---

## 🗄️ Database Schema
<iframe width="560" height="315" src='https://dbdiagram.io/e/678f0c006b7fa355c380fbbf/692f2ad6d6676488ba4c3876'> </iframe>
<div align="left">

[![DB Diagram](https://img.shields.io/badge/View%20Full%20Diagram-dbdiagram.io-blue?style=for-the-badge)](https://dbdiagram.io/d/678f0c006b7fa355c380fbbf)

</div>

---

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

```
┌─────────────────────────────────────────────────────────────────┐
│                        Developer                                │
│                     (Push / PR to main)                         │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     GitHub Actions                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐       │
│  │   Checkout   │───▶│  Setup JDK   │───▶│ Cache Gradle │       │
│  │     Code     │    │    17        │    │ Dependencies │       │
│  └──────────────┘    └──────────────┘    └──────────────┘       │
│                                                  │              │
│                                                  ▼              │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐       │
│  │    Build     │◀───│     Test     │◀───│    Lint      │       │
│  │   Project    │    │  (JUnit 5)   │    │   (Check)    │       │
│  └──────────────┘    └──────────────┘    └──────────────┘       │
│         │                                                       │
│         ▼                                                       │
│  ┌──────────────┐    ┌──────────────┐                           │
│  │ Build Docker │───▶│  Push Image  │                           │
│  │    Image     │    │ to docker hub│                           │
│  └──────────────┘    └──────────────┘                           │
│                              │                                  │
└──────────────────────────────┼──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                      docker hub                                 │
│               (Container Registry)                              │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Oracle Cloud(for tests)                    │
│                   (Deploy & Run)                                │
└─────────────────────────────────────────────────────────────────┘
```

### Pipeline Stages

| Stage | Description | Tools |
|-------|-------------|-------|
| **Checkout** | 소스 코드 체크아웃 | GitHub Actions |
| **Setup** | JDK 17 환경 설정 | actions/setup-java |
| **Cache** | Gradle 의존성 캐싱 | actions/cache |
| **Lint** | 코드 스타일 검사 | Checkstyle |
| **Test** | 단위/통합 테스트 실행 | JUnit 5, Mockito |
| **Build** | JAR 파일 빌드 | Gradle |
| **Docker Build** | 컨테이너 이미지 빌드 | Docker |
| **Push** | 이미지 레지스트리 업로드 | Docker Hub |
| **Deploy** | 프로덕션 배포 | Oracle Cloud |

---

## 🧪 Testing Strategy

### Test Pyramid

```
                    ┌───────────┐
                   ╱             ╲
                  ╱   E2E Tests   ╲
                 ╱   (계획 중)      ╲
                ├───────────────────┤
               ╱                     ╲
              ╱   Integration Tests   ╲
             ╱   (Repository, Redis)   ╲
            ├───────────────────────────┤
           ╱                             ╲
          ╱        Unit Tests             ╲
         ╱   (Service, Domain, Validator)  ╲
        └───────────────────────────────────┘
```

### Test Coverage

| Layer | Test Type | Description |
|-------|-----------|-------------|
| **Service** | Unit Test | 비즈니스 로직 검증 |
| **Domain** | Unit Test | 엔티티 및 도메인 객체 동작 검증 |
| **Validator** | Unit Test | 커스텀 검증기 동작 검증 |
| **Repository** | Integration Test | 데이터 접근 계층 검증 |
| **Security** | Integration Test | 인증/인가 필터 체인 검증 |
| **Redis** | Integration Test | 캐시 및 세션 저장소 검증 |
| **Controller** | REST Docs Test | API 문서화 및 엔드포인트 검증 |

