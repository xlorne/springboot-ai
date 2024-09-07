# SpringBoot-ai

## package
```shell
mvn clean package -Dmaven.test.skip=true
```
## Run
```shell
java -Dspring.ai.openai.api-key={your api key}  -jar target/springboot-ai-0.0.1.jar
```

## Test
```shell
curl --location 'http://localhost:8080/chat/hi' \
--header 'Content-Type: application/json' \
--data '{
    "message":"你的兴趣是什么",
    "chatId":"1"
}'
```
```shell
