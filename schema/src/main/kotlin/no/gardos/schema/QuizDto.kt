package no.gardos.schema

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO for Quiz. It represent a Quiz entity")
data class QuizDto(
		@ApiModelProperty("Quiz id")
		var id: Long? = null,

		@ApiModelProperty("Name of the quiz")
		var name: String? = null,

		@ApiModelProperty("Questions in the quiz")
		var questions: List<QuestionDto>? = null
)