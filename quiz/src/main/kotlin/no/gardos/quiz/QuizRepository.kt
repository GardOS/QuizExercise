package no.gardos.quiz

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface QuizRepository : CrudRepository<QuizEntity, Long>, QuizRepositoryCustom

@Transactional
interface QuizRepositoryCustom{
	fun findQuizByCategoryName(categoryName: String) : List<QuizEntity>
}