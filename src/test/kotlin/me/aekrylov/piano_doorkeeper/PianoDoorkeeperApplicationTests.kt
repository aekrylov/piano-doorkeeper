package me.aekrylov.piano_doorkeeper

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.reactive.server.WebTestClient


@SpringBootTest
@ExtendWith(RestDocumentationExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PianoDoorkeeperApplicationTests {

    @Autowired
    private lateinit var context: ApplicationContext

    private lateinit var webTestClient: WebTestClient

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
        exchange(1, 1, true)
                .expectStatus().isOk
                .expectBody()
                .consumeWith(document("enter_success"))
    }

    @Test
    fun `should allow user to leave after entering`() {
        exchange(1, 1, true)
                .expectStatus().isOk

        exchange(1, 1, false)
                .expectStatus().isOk
                .expectBody()
                .consumeWith(document("leave_success"))
    }

    @Test
    fun `should do 403 when leaving the room without entering it`() {
        exchange(1, 1, false)
                .expectStatus().isForbidden
                .expectBody()
                .consumeWith(document("leave_not_in_room"))
    }

    @Test
    fun `should check that user has access to the room`() {
        exchange(3, 2, true)
                .expectStatus().isForbidden
                .expectBody()
                .consumeWith(document("access_denied"))
    }

    @Test
    fun `shouldn't allow being in multiple rooms simultaneously`() {
        exchange(10, 1, true)
                .expectStatus().isOk

        exchange(10, 2, true)
                .expectStatus().isForbidden
                .expectBody()
                .consumeWith(document("enter_different_room"))
    }

    private fun exchange(keyId: Int, roomId: Int, entrance: Boolean) = webTestClient.get()
            .uri { builder ->
                builder.path("/check")
                        .queryParam("keyId", keyId)
                        .queryParam("roomId", roomId)
                        .queryParam("entrance", entrance)
                        .build()
            }
            .exchange()

}
