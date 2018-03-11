package no.gardos.quiz.model.repository

import no.gardos.quiz.model.entity.CategoryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Repository
interface CategoryRepository : CrudRepository<CategoryEntity, Long>, CategoryRepositoryCustom {
	fun findByName(name: String): CategoryEntity
}

@Transactional
interface CategoryRepositoryCustom {
	fun createCategory(name: String): Long
}

@Repository
@Transactional
class CategoryRepositoryImpl : CategoryRepositoryCustom {

	@PersistenceContext
	private lateinit var em: EntityManager

	override fun createCategory(name: String): Long {
		val entity = CategoryEntity(name)
		em.persist(entity)
		return entity.id!!
	}
}

