package no.gardos.quiz.model

import no.gardos.schema.CategoryDto
import no.gardos.schema.QuestionDto
import no.gardos.schema.QuizDto

class QuizConverter {
	companion object {
		fun transform(quiz: Quiz): QuizDto {
			return QuizDto(
					id = quiz.id,
					name = quiz.name,
					questions = quiz.questions?.map {
						QuestionDto(
								id = it.id,
								answers = it.answers,
								correctAnswer = it.correctAnswer,
								questionText = it.questionText,
								category = CategoryDto(name = it.category?.name, id = it.category?.id)
						)
					}
			)
		}

		fun transform(quizzes: Iterable<Quiz>): List<QuizDto> {
			return quizzes.map { transform(it) }
		}
	}
}