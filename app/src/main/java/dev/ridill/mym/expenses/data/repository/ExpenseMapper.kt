package dev.ridill.mym.expenses.data.repository

import dev.ridill.mym.expenses.data.local.entity.ExpenseAndTagRelation
import dev.ridill.mym.expenses.data.local.entity.ExpenseEntity
import dev.ridill.mym.expenses.domain.model.Expense

fun ExpenseAndTagRelation.toExpense(): Expense = Expense(
    id = expenseEntity.id,
    note = expenseEntity.note,
    amount = expenseEntity.amount,
    date = expenseEntity.dateTime,
    tag = tagEntity?.toTag()
)

fun Expense.toEntity(): ExpenseEntity = ExpenseEntity(
    id = id,
    note = note,
    amount = amount,
    dateTime = date,
    tag = tag?.name
)