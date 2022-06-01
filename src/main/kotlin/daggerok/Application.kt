package daggerok

import java.time.Instant
import javax.sql.DataSource
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.Database
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.apache.logging.log4j.kotlin.logger
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.runApplication
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
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

@Configuration
@EnableConfigurationProperties(R2dbcLiquibaseProperties::class)
class R2dbcLiquibasePropertiesConfig

@ConstructorBinding
@ConfigurationProperties("spring.liquibase")
data class R2dbcLiquibaseProperties(
    @Suppress("UNUSED") val enabled: Boolean = true,
    val changeLog: String = "",
    // val driverClassName: String = "",
)

@Configuration
@ConditionalOnProperty(
    prefix = "spring.liquibase",
    name = ["enabled"], havingValue = "true", matchIfMissing = true,
)
@AutoConfigureAfter(R2dbcAutoConfiguration::class)
@ComponentScan(basePackageClasses = [R2dbcProperties::class])
class R2dbcLiquibaseAutoConfiguration(private val props: R2dbcLiquibaseProperties, private val context: AnnotationConfigApplicationContext) {

    @Bean
    @ConditionalOnMissingBean
    fun r2dbcLiquibaseResourceAccessor(resourceLoader: ResourceLoader): ClassLoaderResourceAccessor =
        ClassLoaderResourceAccessor(resourceLoader.classLoader)
            .also { log.debug { "r2dbcLiquibaseResourceAccessor bean refers to: $it" } }

    @Bean
    @ConditionalOnMissingBean
    fun r2dbcLiquibaseDataSource(r2dbcProperties: R2dbcProperties): DataSource =
        DataSourceBuilder.create()
            .url(
                r2dbcProperties.url.replaceFirst("r2dbc:pool:", "jdbc:")
                    .replaceFirst("r2dbc:", "jdbc:")
            )
            // .driverClassName(props.driverClassName)
            .username(r2dbcProperties.username)
            .password(r2dbcProperties.password)
            .build()
            .also { log.debug { "r2dbcLiquibaseDataSource bean refers to: $it" } }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "close")
    fun r2dbcLiquibaseJdbcConnection(r2dbcLiquibaseDataSource: DataSource): JdbcConnection =
        JdbcConnection(r2dbcLiquibaseDataSource.connection)
            .also { log.debug { "r2dbcLiquibaseJdbcConnection bean refers to: $it" } }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "close")
    fun r2dbcLiquibaseDatabase(r2dbcLiquibaseJdbcConnection: JdbcConnection): Database =
        DatabaseFactory.getInstance().findCorrectDatabaseImplementation(r2dbcLiquibaseJdbcConnection)
            .also { log.debug { "r2dbcLiquibaseDatabase bean refers to: $it" } }

    @Bean
    @ConditionalOnMissingBean
    fun r2dbcLiquibaseDatabaseInitialization(r2dbcLiquibaseResourceAccessor: ClassLoaderResourceAccessor, r2dbcLiquibaseDatabase: Database) =
        ApplicationRunner {
            Liquibase(props.changeLog, r2dbcLiquibaseResourceAccessor, r2dbcLiquibaseDatabase)
                .also { log.debug { "r2dbcLiquibaseDatabaseInitialization bean refers to: $it" } }
                .use {
                    it.update(Contexts(), LabelExpression(), true)
                    log.debug { "R2DBC liquibase database initialized by ${it.changeLogFile}" }
                }
        }

    private companion object {
        val log = logger()
    }
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
