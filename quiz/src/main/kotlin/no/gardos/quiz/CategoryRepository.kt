package no.gardos.quiz

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface CategoryRepository : CrudRepository<CategoryEntity, Long>, CategoryRepositoryCustom

@Transactional
interface CategoryRepositoryCustom {
    fun findByName(name: String) : CategoryEntity
}

