package no.gardos.quiz.model.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import no.gardos.quiz.model.entity.CategoryEntity
import no.gardos.quiz.model.entity.QuestionEntity

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
		var category: CategoryEntity? = null
)

class QuestionConverter {
	companion object {
		private fun transform(question: QuestionEntity): QuestionDto {
			return QuestionDto(
					id = question.id,
					questionText = question.questionText,
					answers = question.answers,
					correctAnswer = question.correctAnswer,
					category = question.category
			)
		}

		fun transform(questions: Iterable<QuestionEntity>): List<QuestionDto> {
			return questions.map { transform(it) }
		}
	}
}