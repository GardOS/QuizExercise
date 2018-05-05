package no.gardos.player.api

import io.swagger.annotations.Api
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(value = "/players", description = "API for players.")
@RequestMapping(
		path = ["/players"],
		produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
@Validated
class PlayerController