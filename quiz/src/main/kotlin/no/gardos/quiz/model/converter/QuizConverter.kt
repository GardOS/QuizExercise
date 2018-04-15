package no.gardos.quiz.model.converter

import no.gardos.quiz.model.entity.Quiz
import no.gardos.schema.QuizDto

class QuizConverter {
	companion object {
		fun transform(quiz: Quiz): QuizDto {
			return QuizDto(
					id = quiz.id
			)
		}

		fun transform(quizzes: Iterable<Quiz>): List<QuizDto> {
			return quizzes.map { transform(it) }
		}
	}
}