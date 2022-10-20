# Mini URL Shortener

Сервис сокращения ссылок

Локальный запуск
```shell
./mvnw spring-boot:run
```
Или
```shell
./mvnw clean install
java -jar target/mini-url-shortener*.jar
```

Swagger - http://localhost:8080/swagger-ui/index.html

Имеет 2 версии
- V1 - генерация сокращенной версии через UUID и BASE62
- V2 - генерация рандомной строки и проверка на соответствии в коллекции