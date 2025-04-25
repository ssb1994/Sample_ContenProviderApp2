package com.example.sample_contenproviderapp2.database

import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DictionaryProviderClient(
    private val context: Context
) {

    companion object {
        private const val AUTHORITY = "com.example.sample_contenproviderapp1"

        private const val WORDS_URI = "content://$AUTHORITY/words"
    }

    suspend fun queryWords(searchFor: String? = null): List<Word> {
        return withContext(Dispatchers.IO) {
            context
                .contentResolver
                .query(
                    WORDS_URI.toUri(),
                    null,
                    null,
                    if (searchFor != null) arrayOf(searchFor) else null,
                    null
                )
                ?.use { cursor ->
                    parseWords(cursor)
                } ?: emptyList<Word>()
        }
    }

    private fun parseWords(cursor: Cursor): List<Word> {
        val wordIndex = cursor.getColumnIndexOrThrow("word")
        val definitionIndex = cursor.getColumnIndexOrThrow("definition")

        val words = mutableListOf<Word>()

        while (cursor.moveToNext()) {
            val word = cursor.getString(wordIndex)
            val definition = cursor.getString(definitionIndex)
            words.add(
                Word(
                    word = word,
                    definition = definition
                )
            )
        }

        return words.toList()
    }

}