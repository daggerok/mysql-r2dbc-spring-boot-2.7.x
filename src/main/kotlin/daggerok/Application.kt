package daggerok

import java.time.Instant
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.relational.core.mapping.Table
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME

@EnableR2dbcAuditing
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Table("messages")
data class Message(
    val body: String,
    val addressTo: String = "all",
    val addressFrom: String = "anonymous",
    @Id val id: Long? = null,
    @LastModifiedDate
    @DateTimeFormat(iso = DATE_TIME)
    val sentAt: Instant? = null,
)

interface Messages : R2dbcRepository<Message, Long>
