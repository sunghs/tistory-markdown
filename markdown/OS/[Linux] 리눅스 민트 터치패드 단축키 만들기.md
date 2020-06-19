---
title: "[LINUX] 리눅스민트 터치패드 단축키 만들기"
tags: ""
---
## [LINUX] 리눅스민트 터치패드 단축키 만들기

노트북 제조사 드라이버 설치 시 터치패드 on/off 설정이 가능한데,
드라이버가 대부분 윈도우만 제공하는 경우가 많아 리눅스를 설치하는 경우 단축키 사용이 불가능 할 수 있다.

리눅스 민트의 경우 gnome 설정에서 터치패드 on/off 설정은 가능한데
**시스템설정 > 하드웨어탭 > 마우스와 터치패드 > 터치패드 사용 설정이 가능하다.**

![사진](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FcNNArg%2FbtqDbE7KFxr%2FLcLWQOihrarAiSLxkZKJLk%2Fimg.png)

끄고 킬순 있는데 제조사가 터치패드 리눅스 드라이버를 제공하지 않으면 단축키는 따로 만들어야 한다.

### 단축키 만들기

#### 터미널에서 xinput을 치면 입력장치 리스트를 가져올 수 있다.

```shell
xinput
```

![사진](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2Fn2Reb%2FbtqDaUJT8mN%2FqQPGKMIO7hMIiKpxpAvqAK%2Fimg.png)

Virtual core pointer 아래 리스트들이고, 
테스트 노트북의 터치패드명은 **ETPS/2 Elantech Touchpad (id = 15) 이다**
리스트에서 노트북 터치패드 마다 명칭이 다를 수 있다.

옆에 있는 id 값을 기억해야 한다.

#### xinput list-props $id

```shell
xinput list-props $id
```

![사진](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FbsXpJV%2FbtqDaWnsK5W%2F4cCGt3h75CST6WtVdgZ4mK%2Fimg.png)

아래 설정값에서 **Device Enabled : 1 인경우 현재 활성화 된 것이고, 0인 경우 비활성화 된 것이다.**

```shell
활성화
xinput enable $id
비활성화
xinput disable $id
```

하나씩 키고 끄면서 터치패드 id를 찾을 수 있다.

#### 실행 shell 만들기

```shell
#!/bin/bash

TOUCH_PAD_ID=`xinput list | grep Elantech | awk '{print $6}' | cut -d '=' -f2`

echo "SELECTED TOUCH PAD ID $TOUCH_PAD_ID"

ENABLE_STATUS=`xinput list-props $TOUCH_PAD_ID | grep "Device Enabled" | awk '{print $4}'`

echo "CURRENT ENABLE STATUS : $ENABLE_STATUS"

if [ "$ENABLE_STATUS" -eq "0" ];
then
        CMD=enable
else
        CMD=disable
fi

xinput $CMD $TOUCH_PAD_ID
notify-send "TOUCH PAD" $CMD
```

이 쉘은 현재 Device Enabled 상태를 보고 변경한다. (1이면 0으로, 0이면 1로)
notity-send 명령어는 알림명령어로 shell이 실행되면 우측 상단에 알림이 발생한다.

#### shell 파일 등록하기

적당한 위치에 옮겨놓는다.
여기서는 /usr/sbin 내에 쉘파일들이 모여있어 위 쉘 스크립트를 touchpad.sh 파일명으로 위치 시켜놓았다.
(관리자 권한 필요)

#### 시스템설정 > 키보드 > 단축키 > 사용자 지정 단축키

![사진](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FBwNV5%2FbtqDceHH2wr%2FVfPm5yN95nhn7fnlsgxZVk%2Fimg.png)

여기서 키보드 단축키를 추가하고, 실행파일을 선택한다. (/usr/sbin/touchpad.sh)

아래 단축키 설정값에서 설정할 수 있다.
이 노트북은 fn+F3이 윈도우 터치패드 on/off 설정키인데, 리눅스에서는 Ctrl+F3으로 설정했다.
편한 키로 설정해주자.

#### 설정한 단축키를 누르면 우측 상단에 상태가 표시된다.

![사진](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FbZFiKz%2FbtqDeJsYQhD%2FoDkyQAQVAB1K4OK80ODs21%2Fimg.png)
