package no.gardos.question.model.repository

import no.gardos.question.model.entity.Category
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface CategoryRepository : CrudRepository<Category, Long>, CategoryRepositoryCustom {
	fun findByName(name: String): Category?
	fun findByQuestionsIsNotNull(): List<Category>
}

@Transactional
interface CategoryRepositoryCustom

