# Django 이미지 블로그 (Mobile/Web Service Project) 
by kiokahn@khu.ac.kr

텍스트 블로그 + 이미지 모델(./blog/models.py) 만 추가된 테스트용 코드입니다.

## 가상환경

Django 프로젝트를 위한 가상환경 생성
```bash
mkdir web-blog
cd mysite
python3 -m venv ./venv
source ./venv/bin/activate # for linux or macos
./venv/bin/activate.ps1 # for windows PowerShell
./venv/bin/activate.bat # for windows CMD
```

MS Windows PowerShell을 사용 할 경우, 실행 정책 보호를 해제 하기위해 PowerShell을 관리자모드로 실행하여 아래를 실행합니다.
```bash
Set-ExecutionPolicy Unrestricted
```

실행 후 프로프트 확인 필요
```
(venv)$
```

## 라이브러리 설치
```bash
pip install -r requirements.txt
```

## DB 마이그레이션
```bash
python manage.py migrate
```

## 관리자 계정 생성
```bash
python manage.py createsuperuser
```

## 서버 실행
```bash
python manage.py runserver
```

## 브라우저 
관리자 접속 후 권한 획득 이후 "+" 버튼이 우 상단에 보여짐

관리자[http://127.0.0.1:8000/admin/]

사용자[http://127.0.0.1:8000/]

Api Root[http://127.0.0.1:8000/api_root/]


## telnet 테스트

```bash
telnet 127.0.0.1 8000
```

아래 "Host"의 경우 ".mysite/settings.py"파일 내 "ALLOWED_HOSTS"에 등록되지 않은 호스트는 http 400 error 응답 함 
```
GET /media/images/pig.png HTTP/1.1
Host: 127.0.0.1
User-Agent: Mozilla/5.0


```