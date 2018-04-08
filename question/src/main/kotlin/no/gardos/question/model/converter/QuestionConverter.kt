package no.gardos.question.model.converter

import no.gardos.question.model.dto.QuestionDto
import no.gardos.question.model.entity.QuestionEntity

class QuestionConverter {
	companion object {
		fun transform(question: QuestionEntity): QuestionDto {
			return QuestionDto(
					id = question.id,
					questionText = question.questionText,
					answers = question.answers,
					correctAnswer = question.correctAnswer,
					category = question.category?.id
			)
		}

		fun transform(questions: Iterable<QuestionEntity>): List<QuestionDto> {
			return questions.map { transform(it) }
		}
	}
}