# 개발 기록

## 1주차: 환경 구성과 Docker

### 한 일
- WSL2 Ubuntu, Docker, Git 환경 구성
- Spring Boot 앱을 로컬에서 실행하고, Docker 이미지로 만들어 컨테이너로 실행
- Docker Compose로 앱과 PostgreSQL(pgvector)을 함께 실행
- pgvector로 벡터 저장과 유사도 검색을 실험해서 벡터 저장소로 결정

### 막힌 점과 해결
- 컨테이너에서 앱이 Connection reset으로 실패: 컨테이너 안의 127.0.0.1은 컨테이너 자신이라서 생긴 문제. 환경변수 SERVER_ADDRESS=0.0.0.0으로 해결
- 홈 폴더에서 간이 웹서버를 실행: 폴더 전체가 공개될 수 있어서 바로 끄고 로그를 확인. 앞으로는 빈 폴더에서만 실행
- .gitignore를 먼저 만들고 .env를 만들어야 한다는 것을 실험으로 확인

### AI가 틀리거나 애매했던 점, 내가 확인한 방법
- 앞 단계에서 실패했는데 뒤 명령을 이어쳐서 에러가 남. 그 뒤로는 단계마다 결과를 확인하고 넘어감.

### 결정
- 벡터 저장소는 pgvector로 결정 (Chroma는 필요 없었음)
- 비밀번호는 .env에 두고 .env.example만 올림



## 2주차: 문서 CRUD API와 테스트

### 한 일
- 문서 정보 장부(id, 파일 이름, 종류, 상태, 올린 시각)를 정하고 Document 엔티티와 Repository 작성
- 문서 CRUD API 5개 구현, curl로 하나씩 확인
- 입력값 검사와 에러 응답 형식 통일(ErrorResponse, GlobalExceptionHandler)
- DocumentControllerTest 7개 작성: 가짜 Repository로 DB 없이 Controller만 점검

### 막힌 점과 해결
- 전체 테스트에서 BackendApplicationTests만 실패: 에러 로그의 Caused by 마지막 줄을 보고 원인을 좁힘. 처음엔 DB 연결 실패라고 예상했지만 실제 원인은 DB_URL 환경변수가 비어 있어서였음. 환경변수를 넣고 나니 다음에는 Connection refused가 났고, 이건 DB 컨테이너가 꺼져 있어서였음. DB를 띄우고 환경변수를 넣으니 8개 모두 통과
- 새 터미널에서는 Docker가 WSL에서 안 보였음: Docker Desktop을 켜고 WSL 연동을 확인해서 해결

### 직접 확인한 것
- 컨트롤러 테스트의 기대 상태 코드를 일부러 틀리게 고쳐서 Failures가 어떻게 보이는지 확인하고 원래대로 되돌림
- Failures는 결과가 기대와 다른 경우, Errors는 테스트 자체가 실행되지 못한 경우

### 정리
- 컨트롤러 테스트 통과는 "Controller는 괜찮다"는 뜻이지 DB 저장까지 확인한 것은 아님
- 전체 테스트는 DB 컨테이너와 환경변수가 있어야 통과함
