package no.gardos.question.model.converter

import no.gardos.question.model.entity.Question
import no.gardos.schema.QuestionDto

class QuestionConverter {
	companion object {
		fun transform(question: Question): QuestionDto {
			return QuestionDto(
					id = question.id,
					questionText = question.questionText,
					answers = question.answers,
					correctAnswer = question.correctAnswer,
					category = question.category?.id
			)
		}

		fun transform(questions: Iterable<Question>): List<QuestionDto> {
			return questions.map { transform(it) }
		}
	}
}