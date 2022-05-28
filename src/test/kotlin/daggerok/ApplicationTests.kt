package daggerok

import org.apache.logging.log4j.kotlin.logger
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.r2dbc.core.DatabaseClient
import reactor.kotlin.test.test

@SpringBootTest(webEnvironment = NONE)
class ApplicationTests(@Autowired val databaseClient: DatabaseClient) {

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
