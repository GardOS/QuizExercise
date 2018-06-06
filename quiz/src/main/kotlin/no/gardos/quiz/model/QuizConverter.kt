package no.gardos.quiz.model

import no.gardos.schema.CategoryDto
import no.gardos.schema.QuestionDto
import no.gardos.schema.QuizDto

class QuizConverter {
	companion object {
		fun transform(quiz: Quiz): QuizDto {
			return QuizDto(
					id = quiz.id.toString(),
					name = quiz.name,
					questions = quiz.questions?.map {
						QuestionDto(
								id = it.id.toString(),
								answers = it.answers,
								correctAnswer = it.correctAnswer,
								questionText = it.questionText,
								category = CategoryDto(name = it.category?.name, id = it.category?.id.toString())
						)
					}
			)
		}

		fun transform(quizzes: Iterable<Quiz>): List<QuizDto> {
			return quizzes.map { transform(it) }
		}
	}
}