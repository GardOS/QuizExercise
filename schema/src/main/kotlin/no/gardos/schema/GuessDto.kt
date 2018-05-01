package no.gardos.schema

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO for guess. It represent a response to a question in a game")
data class GuessDto(
		@ApiModelProperty("Reference to the game which is played")
		var Game: Long? = null,
		@ApiModelProperty("Index of the answer to the question")
		var Answer: Int? = null
)