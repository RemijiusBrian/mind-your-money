package dev.ridill.mym.expenses.domain.repository

import dev.ridill.mym.expenses.data.local.entity.TagEntity
import dev.ridill.mym.expenses.data.local.relation.TagWithExpenditureRelation
import dev.ridill.mym.expenses.domain.model.Tag
import dev.ridill.mym.expenses.domain.model.TagInput
import kotlinx.coroutines.flow.Flow

interface TagsRepository {

    fun getAllTags(): Flow<List<Tag>>

    suspend fun insert(tag: TagInput)

    suspend fun delete(tag: String)

    fun getTagWithExpenditure(monthAndYear: String): Flow<List<TagWithExpenditureRelation>>
}