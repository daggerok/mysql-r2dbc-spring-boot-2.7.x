# MySQL R2DBC Spring Boot 2.7.x [![CI](https://github.com/daggerok/mysql-r2dbc-spring-boot-2.7.x/actions/workflows/ci.yml/badge.svg)](https://github.com/daggerok/mysql-r2dbc-spring-boot-2.7.x/actions/workflows/ci.yml)
This repository demonstrates Spring Boot R2DBC MySQL support

## Table of content
* [Steps](#steps)
* [Getting started](#getting-started)
* [Release](#release)
* [Reference Documentation](#reference-documentation)

## Steps
### [Step 1](https://github.com/daggerok/mysql-r2dbc-spring-boot-2.7.x/tree/v0-simple-database-client-select-with-mysql-in-docker)
Simple databaseClient use: `SELECT 1` query with local mysql in docker.
Check [v0-simple-database-client-select-with-mysql-in-docker](https://github.com/daggerok/mysql-r2dbc-spring-boot-2.7.x/tree/v0-simple-database-client-select-with-mysql-in-docker)
tag for details
### [Step 2](https://github.com/daggerok/mysql-r2dbc-spring-boot-2.7.x/tree/v1-database-migrations-using-SqlInitializationProperties)
Database migration using simple schema.sql and data.sql init scripts.
Check [v1-database-migrations-using-SqlInitializationProperties](https://github.com/daggerok/mysql-r2dbc-spring-boot-2.7.x/tree/v1-database-migrations-using-SqlInitializationProperties)
tag for details
### [Step 3](https://github.com/daggerok/mysql-r2dbc-spring-boot-2.7.x/tree/v2-database-migrations-using-custom-r2dbc-liquibase-spring-boot-starter)
More advanced database migration using custom R2DBC Liquibase migration tool Spring Boot starter.
Check [v2-database-migrations-using-custom-r2dbc-liquibase-spring-boot-starter](https://github.com/daggerok/mysql-r2dbc-spring-boot-2.7.x/tree/v2-database-migrations-using-custom-r2dbc-liquibase-spring-boot-starter)
tag for details

## Getting started

Run and test latest's code:

```bash
if [[ "" != `docker ps -aq` ]] ; then docker rm -f -v `docker ps -aq` ; fi

docker run -d --rm --name mysql --platform=linux/x86_64 \
  --health-cmd='mysqladmin ping -h 127.0.0.1 -u $MYSQL_USER --password=$MYSQL_PASSWORD || exit 1' \
  --health-start-period=1s --health-retries=1111 --health-interval=1s --health-timeout=5s \
  -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=database \
  -e MYSQL_USER=user -e MYSQL_PASSWORD=password \
  -p 3306:3306 \
  mysql:8.0.24

while [[ $(docker ps -n 1 -q -f health=healthy -f status=running | wc -l) -lt 1 ]] ; do sleep 3 ; echo -n '.' ; done ; sleep 15; echo 'MySQL is ready.'

./mvnw

docker stop mysql
```

## Release

Create and push git tag to park some piece of completed work, for example:

```bash
git tag v0-simple-database-client-select-with-mysql-in-docker
git pso --tags
```

<!--

## Reference Documentation
* Spring boot glob resources:
  ```kotlin
  ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
      .getResources("classpath:*/db/migrations/*.ddl.sql")
      .sort { r1, r2 -> "${r1.url}".compareTo("${r2.url}") }
      .forEach { prontln(it) } // will print:
        // /path/to/db/migrations/V202205281525_drop_table_if_exists_messages.ddl.sql
        // /path/to/db/migrations/V202205281526_create_table_if_not_exists_messages.ddl.sql
        // /path/to/db/migrations/V202205281527_create_index_messages_sent_at_idx.ddl.sql
  ```
* Spring boot glob resources, see 1: org.springframework.core.io.support.PathMatchingResourcePatternResolver.getResources
* Spring boot glob resources, see 2: org.springframework.core.io.support.PathMatchingResourcePatternResolver.findPathMatchingResources
* Spring boot glob resources, see 3: org.springframework.core.io.support.ResourcePatternUtils.getResourcePatternResolver
* Spring boot glob resources, see 4: org.springframework.core.io.support.ResourcePatternResolver.getResources
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.7.0/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.7.0/maven-plugin/reference/html/#build-image)
* [Coroutines section of the Spring Framework Documentation](https://docs.spring.io/spring/docs/5.3.20/spring-framework-reference/languages.html#coroutines)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.7.0/reference/htmlsingle/#configuration-metadata-annotation-processor)
* [Spring Data R2DBC](https://docs.spring.io/spring-boot/docs/2.7.0/reference/htmlsingle/#boot-features-r2dbc)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/2.7.0/reference/htmlsingle/#web.reactive)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Acessing data with R2DBC](https://spring.io/guides/gs/accessing-data-r2dbc/)
* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [R2DBC Homepage](https://r2dbc.io)
* Make sure to include a [R2DBC Driver](https://r2dbc.io/drivers/) to connect to your database.

-->
