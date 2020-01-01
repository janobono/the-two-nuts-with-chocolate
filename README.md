# the-two-nuts-with-chocolate

After [the-three-nuts](https://github.com/janobono/the-three-nuts) I choose [Quarkus](https://quarkus.io/)
and [Spring Boot](https://spring.io/projects/spring-boot) as a winners. In this comparison I would like to make simple
application close to production needs (Restful/JPA/Messaging).


## tech stack

Database implementation - [Postgres](https://www.postgresql.org/)

Database migration - [Flyway](https://flywaydb.org/)

Messaging platform - [Kafka](https://kafka.apache.org/)


## build

- jdk 1.8
- maven
- docker

```shell script
mvn clean install -Pdocker
```


## run

In root directory

```shell script
docker-compose up
```


### first start

Kafka topics are not created at firts start so we have to create them:
```shell script
docker exec -it the-two-nuts-with-chocolate_kafka_1 kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic quarkus-nut
docker exec -it the-two-nuts-with-chocolate_kafka_1 kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic spring-boot-nut
```

Then we have to start services:
```shell script
docker container start the-two-nuts-with-chocolate_quarkus-nut_1
docker container start the-two-nuts-with-chocolate_spring-boot-nut_1
```
or
```shell script
docker-compose down
docker-compose up 
```

