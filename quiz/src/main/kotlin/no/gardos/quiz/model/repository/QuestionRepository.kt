package no.gardos.quiz.model.repository

import no.gardos.quiz.model.entity.QuestionEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface QuestionRepository : CrudRepository<QuestionEntity, Long>, QuestionRepositoryCustom

@Transactional
interface QuestionRepositoryCustom {
	fun findQuizByCategoryName(categoryName: String): List<QuestionEntity>
}