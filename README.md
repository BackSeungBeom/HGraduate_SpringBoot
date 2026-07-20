# HGraduate

졸업요건 진단 서비스 백엔드 (Spring Boot + JPA + MySQL)

## 로컬 개발 환경 설정

`compose.yaml`은 실제 DB 비밀번호를 담고 있어 git에 커밋하지 않습니다 (`.gitignore` 처리됨).
로컬에서 실행하려면:

1. `compose.yaml.example`을 복사해서 `compose.yaml`을 만듭니다.
2. `MYSQL_PASSWORD`, `MYSQL_ROOT_PASSWORD` 값을 실제 비밀번호로 채웁니다.
3. `gradlew.bat bootRun` 등으로 실행하면 Docker Compose 지원을 통해 MySQL이 자동으로 기동됩니다.

```
cp compose.yaml.example compose.yaml
# compose.yaml 안의 YOUR_PASSWORD_HERE / YOUR_ROOT_PASSWORD_HERE 를 실제 값으로 수정
```
