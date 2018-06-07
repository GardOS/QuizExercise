package no.gardos.schema

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO for Score. It represent a Score entity")
data class ScoreDto(
		@ApiModelProperty("Reference to Quiz played")
		var quiz: String? = null,

		@ApiModelProperty("Username of player that played")
		var player: String? = null,

		@ApiModelProperty("Score")
		var score: Int = 0,

		@ApiModelProperty("Id of score")
		var id: Long? = null
)