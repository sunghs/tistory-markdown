## \[MAVEN\] Received fatal alert : protocol\_version

#### ... from/to central (https\\://repo.maven.apache.org/maven2)\\: Received fatal alert\\: protocol\_version

pom.xml에 설정한 dependency에서 jar 다운로드가 안되고 .pom 파일에 위와 같은 에러가 떨어지는 경우
Repository가 https (TLS v1.2) 프로토콜로 연결되어야 하는데 JDK 낮은 버전에서는 TLS v1.0 ~ v1.1 이 기본값이라 그렇다.  
(~JDK7버전까지)

**1\. TLS v1.2로 바꾸는 방법**
-   JDK버전을 JDK8 이상으로 올리면 된다.

**2\. repository를 http URL로 바꾸는 방법 (repository가 SSL만 지원하면 나중에 없어질듯 하다.)**
-   JDK를 올리지 못하는 상황에서 2번을 사용해야 하는데 아래는 2번에 대한 설명이다.

#### Users\\사용자\\.m2 에 settings.xml 파일 편집 (기본적으로는 없음)
setting.xml은 dependency 저장소 위치 변경 등 작업이 있을때만 만들어주면 됨.  
기본 xmlns 태그 이후 사이에 아래 태그를 넣어주면 된다.

```xml
<profiles>
    <profile>
        <id>myprofile</id>
        <repositories>
            <repository>
                <releases>
                    <enabled>true</enabled>
                </releases>
                <id>central</id>
                <url>http://repo.maven.apache.org/maven2</url>
            </repository>
        </repositories>
        <pluginRepositories>
            <pluginRepository>
                <releases>
                    <enabled>true</enabled>
                </releases>
                <id>central</id>
                <url>http://repo.maven.apache.org/maven2</url>
            </pluginRepository>
        </pluginRepositories>
    </profile>
</profiles>
<activeProfiles>
    <activeProfile>myprofile</activeProfile>
</activeProfiles>
```

#### 전체태그로 본다면 (settings.xml)

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <profiles>
    <profile>
      <id>myprofile</id>
      <repositories>
        <repository>
          <releases>
            <enabled>true</enabled>
          </releases>
          <id>central</id>
          <url>http://repo.maven.apache.org/maven2</url>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <releases>
            <enabled>true</enabled>
          </releases>
          <id>central</id>
          <url>http://repo.maven.apache.org/maven2</url>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>myprofile</activeProfile>
  </activeProfiles>
</settings>
```

URL은 저장소에 따라 달라질 수 있다.  
위와 같이 세팅하면 effectivePOM 내용을 위 내용으로 오버라이딩 하게 된다.

**effectivePOM과 다른내용은 https -> http**  
이후 Update Project 하면 정상적으로 받아옴. 임시방편일 뿐이고 JDK8버전 이후로 업데이트 해야한다.  
(JDK8부터 TLSv1.2 프로토콜이 기본)