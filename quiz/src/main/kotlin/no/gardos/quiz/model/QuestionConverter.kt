package no.gardos.quiz.model

import no.gardos.schema.CategoryDto
import no.gardos.schema.QuestionDto

class QuestionConverter {
	companion object {
		fun transform(question: Question): QuestionDto {
			return QuestionDto(
					id = question.id,
					questionText = question.questionText,
					answers = question.answers,
					correctAnswer = question.correctAnswer,
					category = CategoryDto(name = question.category?.name, id = question.category?.id)
			)
		}

		fun transform(questions: Iterable<Question>): List<QuestionDto> {
			return questions.map { transform(it) }
		}
	}
}