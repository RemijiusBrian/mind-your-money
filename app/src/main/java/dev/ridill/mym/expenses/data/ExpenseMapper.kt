package dev.ridill.mym.expenses.data.repository

import dev.ridill.mym.expenses.data.local.relation.ExpenseAndTagRelation
import dev.ridill.mym.expenses.data.local.entity.ExpenseEntity
import dev.ridill.mym.expenses.domain.model.Expense
import dev.ridill.mym.expenses.domain.model.ExpenseListItem

fun ExpenseAndTagRelation.toExpenseListItem(): ExpenseListItem = ExpenseListItem(
    id = expenseEntity.id,
    note = expenseEntity.note,
    amount = expenseEntity.amount,
    date = expenseEntity.dateTime,
    tag = tagEntity?.toTag()
)

fun Expense.toEntity(): ExpenseEntity = ExpenseEntity(
    id = id,
    note = note,
    amount = amount.toDouble(),
    dateTime = dateTime,
    tag = tagName
)

fun ExpenseEntity.toExpense(): Expense = Expense(
    id = id,
    note = note,
    amount = amount.toString(),
    dateTime = dateTime,
    tagName = tag
)