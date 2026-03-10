package com.inntemp.inningstempotracker.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val storageFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun today(): String = storageFormat.format(Date())

    fun formatForDisplay(dateStr: String): String = runCatching {
        val date = storageFormat.parse(dateStr) ?: return dateStr
        displayFormat.format(date)
    }.getOrDefault(dateStr)

    fun parseForStorage(displayDate: String): String = runCatching {
        val date = displayFormat.parse(displayDate) ?: return displayDate
        storageFormat.format(date)
    }.getOrDefault(displayDate)
}
