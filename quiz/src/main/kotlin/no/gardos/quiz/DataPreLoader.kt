package no.gardos.quiz

import no.gardos.quiz.model.entity.Category
import no.gardos.quiz.model.entity.Question
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
		val lettersCategory = categoryRepository.save(Category(name = "Letters"))
		val numbersCategory = categoryRepository.save(Category(name = "Numbers"))
		val animalsCategory = categoryRepository.save(Category(name = "Animals"))
		categoryRepository.save(Category(name = "Words"))

		questionRepository.save(Question(
				questionText = "Which letter is B?",
				answers = listOf("A", "B", "C", "D"),
				correctAnswer = 1,
				category = lettersCategory
		))

		questionRepository.save(Question(
				questionText = "Which letter is not D?",
				answers = listOf("L", "D", "D", "D"),
				correctAnswer = 0,
				category = lettersCategory
		))

		questionRepository.save(Question(
				questionText = "What is 1+1?",
				answers = listOf("0", "1", "2", "3"),
				correctAnswer = 2,
				category = numbersCategory
		))

		questionRepository.save(Question(
				questionText = "What is 2-2?",
				answers = listOf("0", "1", "2", "3"),
				correctAnswer = 0,
				category = numbersCategory
		))

		questionRepository.save(Question(
				questionText = "Why is 1+1?",
				answers = listOf("What?", "Yes", "Because it would be embarrassing to be 2+1", "Because its not 3"),
				correctAnswer = 2,
				category = numbersCategory
		))

		questionRepository.save(Question(
				questionText = "What does the cow say?",
				answers = listOf("Moo", "Baah", "Woof", "Meow"),
				correctAnswer = 0,
				category = animalsCategory
		))

		questionRepository.save(Question(
				questionText = "What does the fox say?",
				answers = listOf("ÆØÅ", "Nothing", "Hatti-ho", "DingDing"),
				correctAnswer = 3,
				category = animalsCategory
		))

		questionRepository.save(Question(
				questionText = "Yes?",
				answers = listOf("No", "Yes", "No", "No"),
				correctAnswer = 1,
				category = null
		))
	}
}