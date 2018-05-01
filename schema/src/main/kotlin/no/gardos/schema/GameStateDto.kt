package no.gardos.schema

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO for GameState. It represent a GameState entity")
data class GameStateDto(
		@ApiModelProperty("Reference to the quiz which is played")
		var quiz: Long? = null,
		@ApiModelProperty("Reference to the player playing the game")
		var player: Long? = null,
		@ApiModelProperty("Indication that all the questions has been attempted")
		var isFinished: Boolean = false,
		@ApiModelProperty("GameState id")
		var id: Long? = null
)