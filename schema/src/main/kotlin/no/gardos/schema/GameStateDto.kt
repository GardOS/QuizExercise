package no.gardos.schema

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO for GameState. It represent a GameState entity")
data class GameStateDto(
		@ApiModelProperty("Reference to the quiz which is played")
		var Quiz: Long? = null,
		@ApiModelProperty("Reference to the first player playing the game")
		var PlayerOne: Long? = null,
		@ApiModelProperty("Reference to the second player playing the game")
		var PlayerTwo: Long? = null,
		@ApiModelProperty("GameState id")
		var id: Long? = null
)