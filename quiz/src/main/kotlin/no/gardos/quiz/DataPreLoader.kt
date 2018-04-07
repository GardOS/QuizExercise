package no.gardos.quiz

import no.gardos.quiz.model.entity.CategoryEntity
import no.gardos.quiz.model.entity.QuestionEntity
import no.gardos.quiz.model.repository.CategoryRepository
import no.gardos.quiz.model.repository.QuestionRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DataPreLoader(var questionRepository: QuestionRepository,
                    var categoryRepository: CategoryRepository)
	: CommandLineRunner {

	//For practical reasons some data is created when the application starts, since its a drag to create it yourself
	override fun run(vararg p0: String?) {
		val lettersCategory = categoryRepository.save(CategoryEntity(name = "Letters"))
		val numbersCategory = categoryRepository.save(CategoryEntity(name = "Numbers"))
		val animalsCategory = categoryRepository.save(CategoryEntity(name = "Animals"))

		questionRepository.save(QuestionEntity(
				questionText = "Which letter is B?",
				answers = listOf("A", "B", "C", "D"),
				correctAnswer = 1,
				category = lettersCategory
		))

		questionRepository.save(QuestionEntity(
				questionText = "Which letter is not D?",
				answers = listOf("L", "D", "D", "D"),
				correctAnswer = 0,
				category = lettersCategory
		))

		questionRepository.save(QuestionEntity(
				questionText = "What is 1+1?",
				answers = listOf("0", "1", "2", "3"),
				correctAnswer = 2,
				category = numbersCategory
		))

		questionRepository.save(QuestionEntity(
				questionText = "What is 2-2?",
				answers = listOf("0", "1", "2", "3"),
				correctAnswer = 0,
				category = numbersCategory
		))

		questionRepository.save(QuestionEntity(
				questionText = "Why is 1+1?",
				answers = listOf("What?", "Yes", "Because it would be embarrassing to be 2+1", "Because its not 3"),
				correctAnswer = 2,
				category = numbersCategory
		))

		questionRepository.save(QuestionEntity(
				questionText = "What does the cow say?",
				answers = listOf("Moo", "Baah", "Woof", "Meow"),
				correctAnswer = 0,
				category = animalsCategory
		))

		questionRepository.save(QuestionEntity(
				questionText = "What does the fox say?",
				answers = listOf("ÆØÅ", "Nothing", "Hatti-ho", "DingDing"),
				correctAnswer = 3,
				category = animalsCategory
		))
	}
}