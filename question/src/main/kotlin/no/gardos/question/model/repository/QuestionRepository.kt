package no.gardos.question.model.repository

import no.gardos.question.model.entity.QuestionEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface QuestionRepository : CrudRepository<QuestionEntity, Long>, QuestionRepositoryCustom {
	fun findQuestionByCategoryName(categoryName: String?): List<QuestionEntity>
	fun findQuestionByCategoryId(category: Long?): List<QuestionEntity>
}

@Transactional
interface QuestionRepositoryCustom
