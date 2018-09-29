package com.booboot.vndbandroid.model.vndbandroid

import androidx.annotation.ColorRes
import com.booboot.vndbandroid.App
import com.booboot.vndbandroid.R

import java.text.DecimalFormat
import java.util.regex.Pattern

object Vote {
    const val DEFAULT = "Add a vote"
    private val VOTE_FORMAT = DecimalFormat("#.#")

    fun outOf10(vote: Int) = vote / 10f

    fun toString(vote: Int) =
        if (vote < 1) DEFAULT else toShortString(vote) + " (" + getName(outOf10(vote)) + ")"

    fun toShortString(vote: Int?, defaultValue: String? = App.context.getString(R.string.dash)): String? =
        if (vote != null) VOTE_FORMAT.format(outOf10(vote)) else defaultValue

    fun isValid(vote: String?): Boolean {
        if (vote == null) return false
        val pattern = Pattern.compile("[1-9](\\.[0-9])?")
        return pattern.matcher(vote).matches()
    }

    fun getName(vote: Float) = when {
        vote >= 10 -> "masterpiece"
        vote >= 9 -> "excellent"
        vote >= 8 -> "very good"
        vote >= 7 -> "good"
        vote >= 6 -> "decent"
        vote >= 5 -> "so-so"
        vote >= 4 -> "weak"
        vote >= 3 -> "bad"
        vote >= 2 -> "awful"
        vote >= 1 -> "worst ever"
        vote >= 0 -> "Other"
        else -> DEFAULT
    }

    @ColorRes
    fun getColor(vote: Float) = when {
        vote >= 8 -> R.color.green
        vote >= 7 -> R.color.lightGreen
        vote >= 6 -> R.color.yellow
        vote >= 4 -> R.color.lightOrange
        vote >= 3 -> R.color.orange
        else -> R.color.red
    }
}
