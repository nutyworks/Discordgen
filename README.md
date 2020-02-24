# 디스코드젠
귀찮아서 파일 하나에 때려 박은 디스코드봇 

## 사용법
`디미젠 [번호]`를 입력하면 [코드젠](http://codegen.dimigo.hs.kr)에 있는 오류를 알려줍니다.

`디미젠 난수처리`를 입력하면 중복 답안을 처리할 수 있는 방법을 알려줍니다.

## 설치/실행
이 단계는 [Maven](https://maven.apache.org/index.html)이 설치되어 있다고 가정합니다.

1. 이 리포지토리를 로컬 저장소로 복제합니다.
```
git clone https://github.com/nutyworks/Discordgen
```
2. 다음 명령을 실행합니다.
```
cd Discordgen
mvn install
```
3. `target/` 폴더에서 `Discordgen-0.1.0.jar-with-dependencies.jar`을 실행합니다.
```
java -jar Discordgen.jar <your bot token>
```
