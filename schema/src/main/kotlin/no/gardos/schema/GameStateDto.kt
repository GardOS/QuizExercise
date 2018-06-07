package no.gardos.schema

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.io.Serializable

@ApiModel("DTO for GameState. It represent a GameState entity")
data class GameStateDto(
		@ApiModelProperty("Reference to the quiz which is played")
		var quiz: String? = null,
		@ApiModelProperty("Username of the player playing the game")
		var player: String? = null,
		@ApiModelProperty("Current score")
		var Score: Int = 0,
		@ApiModelProperty("Current round")
		var RoundNumber: Int = 0,
		@ApiModelProperty("Indication that all the questions has been attempted")
		var isFinished: Boolean = false,
		@ApiModelProperty("GameState id")
		var id: String? = null
) : Serializable