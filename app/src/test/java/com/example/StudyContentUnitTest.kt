package com.example

import com.example.data.StudyContentRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StudyContentUnitTest {

    private val repo = StudyContentRepository()

    @Test
    fun `curriculum covers all 7 official LAQP01 units with comprehensive notes`() {
        val units = repo.getUnits()
        val notes = repo.getNotes()
        assertEquals(7, units.size)
        assertTrue(notes.size >= 21)
        units.forEach { unit ->
            val unitNotes = notes.filter { it.unitId == unit.id }
            assertTrue("Unit ${unit.number} should have at least 3 notes", unitNotes.size >= 3)
        }
    }

    @Test
    fun `full mock provides 75 valid questions with 4 options each`() {
        val mockQuestions = repo.getFullMock75Questions()
        assertEquals(75, mockQuestions.size)
        mockQuestions.forEach { q ->
            assertEquals("Question ${q.id} must have 4 options", 4, q.options.size)
            assertTrue("Question ${q.id} correctIndex out of bounds", q.correctIndex in 0..3)
            assertTrue("Question ${q.id} prompt blank", q.prompt.isNotBlank())
            assertTrue("Question ${q.id} explanation blank", q.explanation.isNotBlank())
        }
    }

    @Test
    fun `pyq bank covers all 7 units with valid answer keys`() {
        val pyqs = repo.getPyqQuestions()
        assertEquals(25, pyqs.size)
        val coveredUnits = pyqs.map { it.unitId }.toSet()
        assertEquals(7, coveredUnits.size)
        pyqs.forEach { q ->
            assertEquals(4, q.options.size)
            assertTrue(q.correctIndex in 0..3)
        }
    }
}
