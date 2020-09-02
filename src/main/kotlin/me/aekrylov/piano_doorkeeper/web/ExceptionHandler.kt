package me.aekrylov.piano_doorkeeper.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.handler.WebFluxResponseStatusExceptionHandler
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@RestControllerAdvice
class ExceptionHandler : WebFluxResponseStatusExceptionHandler() {

    @ExceptionHandler(Exception::class)
    fun unknownException(e: Exception): Mono<ResponseEntity<RestResponse>> {
        val determinedStatus = determineStatus(e) ?: HttpStatus.INTERNAL_SERVER_ERROR
        if (determinedStatus.is4xxClientError) {
            if (e is ResponseStatusException) {
                return Mono.just(
                        ResponseEntity.status(determinedStatus).body<RestResponse>(RestResponse("invalid_request", e.message))
                )
            }
        }

        return Mono.error(e)

    }
}