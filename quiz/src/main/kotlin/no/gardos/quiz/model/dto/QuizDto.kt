package no.gardos.quiz.model.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO for Quiz. It represent a Quiz entity")
data class QuizDto(
		@ApiModelProperty("Quiz id")
		var id: Long? = null
)