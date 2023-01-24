# mysql 환경 설정
version: "3.7"
services:
  db:
    image: mysql:8.0
    container_name: mysql
    restart: always
    ports:
      - '3306:3306'
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
    environment:
      TZ: 'Asia/Seoul'
      MYSQL_ROOT_PASSWORD: 1234
      MYSQL_DATABASE: test
    volumes:
      - ./mysql/mysql:/var/lib/mysql

$ vi docker-compose.yml
위의 내용 복붙

$ docker-compose up -d



