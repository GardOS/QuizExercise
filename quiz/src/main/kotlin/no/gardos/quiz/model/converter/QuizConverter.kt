package no.gardos.quiz.model.converter

import no.gardos.quiz.model.dto.QuizDto
import no.gardos.quiz.model.entity.Quiz

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