package no.gardos.question.model.repository

import no.gardos.question.model.entity.Question
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface QuestionRepository : CrudRepository<Question, Long>, QuestionRepositoryCustom {
	fun findQuestionByCategoryName(categoryName: String?): List<Question>
	fun findQuestionByCategoryId(category: Long?): List<Question>
}

@Transactional
interface QuestionRepositoryCustom
