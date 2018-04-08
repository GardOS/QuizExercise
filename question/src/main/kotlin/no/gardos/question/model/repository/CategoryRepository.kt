package no.gardos.question.model.repository

import no.gardos.question.model.entity.CategoryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface CategoryRepository : CrudRepository<CategoryEntity, Long>, CategoryRepositoryCustom {
	fun findByName(name: String): CategoryEntity?
	fun findByQuestionsIsNotNull(): List<CategoryEntity>
}

@Transactional
interface CategoryRepositoryCustom

