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
