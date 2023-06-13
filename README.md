# GAuth SDK Kotlin

## Dependency

### Gradle
repositories
```gradle
// build.gradle
maven { url 'https://jitpack.io' }

// or

// build.gradle.kts
maven { url = uri("https://jitpack.io") }
```

dependencies
```gradle
// build.gradle
implementation 'com.github.GSM-MSG:GAuth-SDK-Java:v2.0.0'

// or

// build.gradle.kt
implementation("com.github.GSM-MSG:GAuth-SDK-Java:v2.0.0")
```

### Maven
repositories
```maven
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

dependencies
```maven
<dependency>
	  <groupId>com.github.GSM-MSG</groupId>
	  <artifactId>GAuth-SDK-Java</artifactId>
	  <version>v2.0.0</version>
</dependency>
```

<br>

## Configuration

### Bean 등록
```java
@Bean
public GAuth gauth() {
	return new GAuthImpl();
}
```

### DI
```java
@Component
public class Component{
	private GAuth gAuth;

	public Component(GAuth gAuth){
		this.gAuth = gAuth;
	}
}
```

<br>

## 사용법

### 코드발급
해당 메서드를 통해 발급 가능
```java
gAuth.generateCode(email, password);
```

```java
public class GAuthCode {
    private String code;
}
```

코드 발급후 해당 객체를 리턴한다.

### 토큰 발급

```java
gAuth.generateToken(email, password, clientId, clientSecret, redirectUri);

gAuth.generateToken(code, clientId, clientSecret, redirectUri);
```
이메일, 패스워드, 클라이언트 아이디, 클라이언트 시크릿, 리다이렉트 uri를 사용해서 토큰을 발급할 수도 있고,
  
코드, 클라이언트 아이디, 클라이언트 시크릿, 리다이렉트 uri를 사용해서 발급할 수도 있다.

```java
public class GAuthToken {
    private String accessToken;
    private String refreshToken;
}
```
토큰 발급 후 해당 객체를 리턴한다.

### 토큰 재발급

refreshToken을 통해서 토큰을 발급할 수 있다.

```java
gAuth.refresh(refreshToken);
```

```java
public class GAuthToken {
    private String accessToken;
    private String refreshToken;
}
```
토큰 발급 후 해당 객체를 리턴한다.

### 유저 정보

```java
gAuth.getUserInfo(accessToken);
```

accessToken을 사용해 유저 정보를 가져올 수 있다.

```java
public class GAuthUserInfo {
    private String email;
    private String name;
    private Integer grade;
    private Integer classNum;
    private Integer num;
    private String gender; // MALE | FEMALE
    private String profileUrl;
    private String role; // ROLE_STUDENT | ROLE_TEACHER
}
```

유저 정보를 해당 객체에 담아서 리턴한다.
  
> grade, classNum, num, number, gender, profileUrl은 nullable로  
> GAuth SDK Java는 Type!를 반환하지만(문제는 없음) GAuth SDK Kotlin은 Type?를 반환한다.

### Exception
응답코드가 200이 아니면 예외코드를 담은 GAuthException을 throw한다.