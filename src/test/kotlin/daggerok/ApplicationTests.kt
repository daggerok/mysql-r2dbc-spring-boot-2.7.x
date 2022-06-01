package daggerok

import org.apache.logging.log4j.kotlin.logger
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.context.annotation.Import
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.delete
import org.springframework.data.r2dbc.core.select
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.ContextConfiguration
import reactor.kotlin.test.test
import reactor.test.StepVerifier

@SpringBootTest(webEnvironment = NONE)
@Import(R2dbcLiquibaseAutoConfiguration::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ApplicationTests @Autowired constructor(
    val messages: Messages,
    val databaseClient: DatabaseClient,
    val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {

    @Test
    fun `should test r2dbcRepository`() {
        StepVerifier
            .create(
                messages.deleteAll()
                    .doOnEach { log.info { "Repository deleteAll, on each: $it" } }
            )
            .verifyComplete()

        StepVerifier
            .create(
                messages.save(Message(body = "Hello all!"))
                    .doOnEach { log.info { "Repository save, in each: $it" } }
            )
            .expectNextCount(1)
            .verifyComplete()

        StepVerifier
            .create(
                messages.findAll()
                    .doOnEach { log.info { "Repository findAll, on each: $it" } }
            )
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test @Order(2)
    fun `should test r2dbcEntityTemplate`() {
        r2dbcEntityTemplate.delete<Message>()
            .all()
            .doOnEach { log.info { "Template delete all, on each: $it" } }
            .test()
                .expectNextCount(1)
                .verifyComplete()

        r2dbcEntityTemplate.insert(Message(body = "Hi there!"))
            .doOnEach { log.info { "Template insert, on each: $it" } }
            .test()
                .expectNextCount(1)
                .verifyComplete()

        r2dbcEntityTemplate.select<Message>()
            .all()
            .doOnEach { log.info { "Template select all, on each: $it" } }
            .test()
                .expectNextCount(1)
                .verifyComplete()
    }

    @Test
    @Order(1)
    fun `should test databaseClient`() {
        databaseClient.sql(" DELETE FROM messages ; ")
            .fetch().all()
            .doOnEach { log.info { "DatabaseClient delete, on each: $it" } }
            .test().verifyComplete()

        databaseClient.sql(" INSERT INTO messages (body) VALUES ('Hello, world!') ; ")
            .fetch().all()
            .doOnEach { log.info { "DatabaseClient insert, on each: $it" } }
            .test().verifyComplete()

        databaseClient.sql(" SELECT * FROM messages ; ")
            .fetch().all()
            .doOnEach { log.info { "DatabaseClient select, on each: $it" } }
            .test()
            .consumeNextWith {
                it.forEach { (column, value) ->
                    log.info { "$column: $value" }
                }
            }
            .verifyComplete()
    }

    @Test
    fun `should test context`() {
        databaseClient.sql { " SELECT 1 AS result ; " }
            .fetch().one()
            .test()
            .consumeNextWith {
                it.forEach { (column, value) ->
                    log.info { "$column: $value" }
                }
            }
            .verifyComplete()
    }

    companion object {
        val log = logger()
    }
}
