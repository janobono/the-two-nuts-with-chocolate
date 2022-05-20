# the-two-nuts-with-chocolate

After [the-three-nuts](https://github.com/janobono/the-three-nuts) I choose [Quarkus](https://quarkus.io/)
and [Spring Boot](https://spring.io/projects/spring-boot) as a winners. In this comparison I would like to make simple
application close to production needs (Restful/JPA/Messaging).


## tech stack

Database implementation - [Postgres](https://www.postgresql.org/)

Database versioning - [Flyway](https://flywaydb.org/)

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

Kafka topics are not created at first start so we have to create them:
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


### endpoints

- [quarkus-nut](http://127.0.0.1:8081/user/)
- [spring-boot-nut](http://127.0.0.1:8082/user/)
 

## measuring


### image size

```shell script
docker image ls
```

```
REPOSITORY                           TAG                 IMAGE ID            CREATED              SIZE
janobono/spring-boot-nut-chocolate   latest              2df1fab068fd        12 seconds ago       185MB
janobono/quarkus-nut-chocolate       latest              3fbbdffc3176        About a minute ago   179MB
```


### memory consumption

```shell script
docker stats
```

```
CONTAINER ID        NAME                                            CPU %               MEM USAGE / LIMIT     MEM %               NET I/O             BLOCK I/O           PIDS
22d681e84bd3        the-two-nuts-with-chocolate_quarkus-nut_1       1.92%               308.6MiB / 7.677GiB   3.93%               12.6kB / 8.42kB     0B / 0B             29
dfa6ec0b8eb4        the-two-nuts-with-chocolate_spring-boot-nut_1   1.95%               452.3MiB / 7.677GiB   5.75%               52.4kB / 45.7kB     0B / 0B             36

```


## results

1. quarkus
1. springboot
