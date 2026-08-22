package com.unix.app.data.academic

/**
 * UNI X academic calendar: one Session = one academic year = two Semesters.
 * This mirrors standard private-sector school structure rather than a
 * US-style quarter/trimester system.
 */
data class AcademicSession(
    val id: Int,
    val label: String, // e.g. "2026/2027"
    val semesters: List<Semester>,
    val isCurrent: Boolean,
)

data class Semester(
    val id: Int,
    val label: String, // "Semester 1" / "Semester 2"
    val startLabel: String,
    val endLabel: String,
    val isCurrent: Boolean,
)

data class TuitionInvoice(
    val sessionLabel: String,
    val semesterLabel: String,
    val programme: String,
    val baseAmountUsd: Double,
    val amountPaid: Double,
    val status: InvoiceStatus,
    val dueLabel: String,
)

enum class InvoiceStatus { UNPAID, PARTIALLY_PAID, PAID, OVERDUE }
