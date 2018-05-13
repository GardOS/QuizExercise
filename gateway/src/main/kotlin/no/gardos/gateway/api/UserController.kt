package no.gardos.gateway.api

import io.swagger.annotations.Api
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(value = "/users", description = "API for players.")
@RequestMapping(
		path = ["/users"],
		produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
@Validated
class UserController