package dev.ridill.mym.expenses.data.repository

import dev.ridill.mym.expenses.data.local.TagsDao
import dev.ridill.mym.expenses.data.local.entity.TagEntity
import dev.ridill.mym.expenses.domain.model.Tag
import dev.ridill.mym.expenses.domain.repository.TagsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TagsRepositoryImpl(
    private val dao: TagsDao
) : TagsRepository {
    override fun getAllTags(): Flow<List<Tag>> = dao.getAllTags().map { entities ->
        entities.map(TagEntity::toTag)
    }
}