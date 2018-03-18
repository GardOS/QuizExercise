package no.gardos.quiz.model.repository

import no.gardos.quiz.model.entity.CategoryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface CategoryRepository : CrudRepository<CategoryEntity, Long>, CategoryRepositoryCustom {
	fun findByName(name: String): CategoryEntity?
}

@Transactional
interface CategoryRepositoryCustom

