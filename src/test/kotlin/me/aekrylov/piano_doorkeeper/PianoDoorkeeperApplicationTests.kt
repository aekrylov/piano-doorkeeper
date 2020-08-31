package me.aekrylov.piano_doorkeeper

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@SpringBootTest
@ContextConfiguration(initializers = [PianoDoorkeeperApplicationTests.PropertyInitializer::class])
@ExtendWith(RestDocumentationExtension::class)
@Testcontainers
class PianoDoorkeeperApplicationTests {

    @Autowired
    private lateinit var context: ApplicationContext

    private lateinit var webTestClient: WebTestClient

    private val id = ID.next()
    private val user = User(id)
    private val room = id

    companion object {
        @Container
        var redis: RedisContainer = RedisContainer()
                .withExposedPorts(6379)
    }

    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        webTestClient = WebTestClient.bindToApplicationContext(context)
                .configureClient().baseUrl("http://localhost:8080")
                .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
                .build()
    }

    @Test
    fun contextLoads() {
    }

    @Test
    fun `should allow user to enter`() {
        exchange(user, room, true)
                .expectStatus().isOk
                .expectBody()
                .consumeWith(document("enter_success"))
    }

    @Test
    fun `should allow user to leave after entering`() {
        exchange(user, room, true)
                .expectStatus().isOk

        exchange(user, room, false)
                .expectStatus().isOk
                .expectBody()
                .consumeWith(document("leave_success"))
    }

    @Test
    fun `should do 403 when leaving the room without entering it`() {
        exchange(user, room, false)
                .expectStatus().isForbidden
                .expectBody()
                .consumeWith(document("leave_not_in_room"))
    }

    @Test
    fun `should check that user has access to the room`() {
        exchange(user, room+1, true)
                .expectStatus().isForbidden
                .expectBody()
                .consumeWith(document("access_denied"))
    }

    @Test
    fun `shouldn't allow being in multiple rooms simultaneously`() {
        exchange(user, room, true)
                .expectStatus().isOk

        exchange(user, room*2, true)
                .expectStatus().isForbidden
                .expectBody()
                .consumeWith(document("enter_different_room"))
    }

    private fun exchange(user: User, roomId: Int, entrance: Boolean) = webTestClient.get()
            .uri { builder ->
                builder.path("/check")
                        .queryParam("keyId", user.id)
                        .queryParam("roomId", roomId)
                        .queryParam("entrance", entrance)
                        .build()
            }
            .exchange()

    class PropertyInitializer: ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.redis.host=${redis.host}",
                    "spring.redis.port=${redis.firstMappedPort}"
            )
        }

    }
}
