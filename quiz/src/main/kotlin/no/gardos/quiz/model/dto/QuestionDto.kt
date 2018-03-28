package no.gardos.quiz.model.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO for Question. It represent a Question entity")
data class QuestionDto(

		@ApiModelProperty("Question id")
		var id: Long? = null,

		@ApiModelProperty("The question text for the question")
		var questionText: String? = null,

		@ApiModelProperty("The possible answers for the question")
		var answers: List<String>? = null,

		@ApiModelProperty("The correct answer for the question")
		var correctAnswer: Int? = null,

		@ApiModelProperty("The category of the question")
		var category: Long? = null
)