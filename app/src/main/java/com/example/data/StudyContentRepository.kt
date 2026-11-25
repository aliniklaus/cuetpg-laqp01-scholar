package com.example.data

import com.example.model.QuizQuestion
import com.example.model.StudyNote
import com.example.model.SyllabusUnit

class StudyContentRepository {

    fun getUnits(): List<SyllabusUnit> = CurriculumNotesBank.units

    fun getNotes(): List<StudyNote> = CurriculumNotesBank.notes

    fun getPyqQuestions(): List<QuizQuestion> = PyqQuestionBank.questions

    fun getMockBankQuestions(): List<QuizQuestion> =
        MockQuestionBankPart1.questions + MockQuestionBankPart2.questions

    fun getFullMock75Questions(): List<QuizQuestion> {
        val bank = getMockBankQuestions()
        return if (bank.size >= 75) bank.take(75) else (bank + getPyqQuestions()).take(75)
    }

    fun getUnitPracticeQuestions(unitId: String, count: Int = 10): List<QuizQuestion> {
        val combined = (getMockBankQuestions() + getPyqQuestions()).filter { it.unitId == unitId }
        return combined.shuffled().take(count.coerceAtMost(combined.size))
    }

    fun getMixedQuickDrill(count: Int = 10): List<QuizQuestion> {
        return (getMockBankQuestions() + getPyqQuestions()).shuffled().take(count)
    }
}
