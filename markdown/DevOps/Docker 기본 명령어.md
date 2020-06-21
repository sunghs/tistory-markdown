# Docker

![](./../../static/DevOps/docker-image.png)


**이 글은 추가사항이 있으면 계속 업데이트 됩니다.**  
last updated 2020. 04. 10

## Docker 명령어 기본 개념
- docker는 항상 root로 실행 되어야 한다. (유저 권한이라면 sudo docker ..)
- docker 기본 명령어는 docker "cmd" 형태로 구성된다. (docker run, docker attach)

## Docker 배포판 찾기
Docker Hub에서 찾는다.  
docker search "keyword"
```sh
docker search amazon
```
![](docker-search-amazon.png)


## 이미지 리스트
```sh
docker images
```

## 이미지 다운로드
```sh
docker pull "image"
```
위 search 명령어로 찾은 배포판:버전을 합쳐 쓴다.  
EX) 배포판 : amazonlinux, 버전(태그) : latest => **amazonlinux:latest**


## Docker 이미지로 컨테이너 생성 및 실행
배포판이 없을 경우 자동으로 다운로드하고, 컨테이너를 생성한다.
```sh
docker run -it "amazonlinux:latest"
```
it 파라미터를 부여하고 구동해야 이후 bash 명령어를 사용 할 수 있다.
>The -it instructs Docker to allocate a pseudo-TTY connected to the container’s stdin; creating an interactive bash shell in the container.


### 이름 지정
이름이 없는 경우 알아서 생성한다.  
--name "name"
```sh
docker run -it --name amazon "amazonlinux:latest"
```

### 백그라운드 실행
run 옵션은 기본이 현재 세션에서 실행한다.  
그래서 -d 파라미터를 줘야 백그라운드로 실행한다.
```sh
docker run -it -d --name amazon "amazonlinux:latest"
```

## Docker 전체 컨테이너 리스트 출력
```sh
docker ps -a
```
![](docker-ps-a-grep-amazon.png)

### 구동 중인 컨테이너만 출력 
```sh
docker ps
```

## 특정 프로그램 실행하며 컨테이너 실행 (bash)
가장 마지막 인자값으로 실행 명령어를 붙여준다.  
run 명령어기 때문에 이미지를 실행하면 컨테이너가 생겨나는 구조.  
컨테이너명을 붙이면 기존 있는 컨테이너를 실행한다.
```sh
docker run -it "image or container" /bin/bash
```


## Docker 컨테이너 삭제
```sh
docker rm "컨테이너 명 또는 id"
```

## Docker 이미지 삭제
배포판을 삭제하기 전에 컨테이너를 먼저 삭제해야 한다.
```sh
docker rmi "배포판이름 또는 id"
```


## 컨테이너 실행
이미지를 한번이라도 run으로 실행해서 컨테이너가 있는 경우 사용 가능하다.
```sh
docker start "컨테이너 명 또는 id"
```

## 컨테이너 정지
```sh
docker stop "컨테이너 명 또는 id"
```

## 컨테이너에 붙기 (접속)
```sh
docker attach "컨테이너 명 또는 id"
```
컨테이너 내에서 백그라운드로 동작하지 않는 프로그램은 bash shell이 동작하지만 원래 목적은 이미 실행중인 프로세스(=컨테이너)에 붙는 명령어다.  
그래서 종료명령어를 날리게 되면 컨테이너가 종료된다.

그래서 bash를 사용할 목적이라면 새 세션으로 붙는 exec 명령어로 붙어야 한다.
```sh
docker exec -it "컨테이너 명 또는 id" /bin/bash
```
 

## 컨테이너 빠져나오기
```sh
Ctrl + P, Ctrl +Q
```
Exit 입력 시 컨테이너가 종료된다.

## 현재 컨테이너 이미지로 만들기 (commit)
```sh
docker commit "컨테이너 명" "이미지 이름"
```
현재 컨테이너의 상태를 SNAPSHOT 같이 이미지로 만든다.

![](docker-commit-jenkins.png)
현재 구동중인 jenkins 이미지 commit
![](docker-commit-after-images.png)
이미지로 만들어진 jenkins 이미지


## 파일 Host -> Container 로 옮기기
```sh
docker cp "host파일 경로" "container파일 경로"

```


## 컨테이너 ip보기
```sh
docker inspect -f "{{ .NetworkSettings.IPAddress }}" "컨테이너 명"
```
컨테이너가 내려갔다가 올라오는 경우 IP가 바뀔 수 있다.  
그래서 젠킨스를 이용해서 배포하는 경우 IP가 달라지면 매번 확인해야 하므로 링크를 걸 수 있다.

### 서버(amazon) - 젠킨스(jk) 묶기
#### 젠킨스 IP를 묶은 amazon 컨테이너를 구동
```sh
docker run -d --name amazon --link jk amazon:shs
```
#### 링크로 묶으면 IP가 아닌 컨테이너 명으로 대체 가능하다.
```sh
docker exec -t amazon ping jk
```

