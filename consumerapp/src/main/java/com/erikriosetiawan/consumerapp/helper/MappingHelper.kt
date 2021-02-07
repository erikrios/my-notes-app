package com.erikriosetiawan.consumerapp.helper

import android.database.Cursor
import com.erikriosetiawan.consumerapp.db.DatabaseContract.NoteColumns.Companion.DATE
import com.erikriosetiawan.consumerapp.db.DatabaseContract.NoteColumns.Companion.DESCRIPTION
import com.erikriosetiawan.consumerapp.db.DatabaseContract.NoteColumns.Companion.TITLE
import com.erikriosetiawan.consumerapp.db.DatabaseContract.NoteColumns.Companion._ID
import com.erikriosetiawan.consumerapp.entity.Note

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<Note> {
        val noteList = ArrayList<Note>()

        notesCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(_ID))
                val title = getString(getColumnIndexOrThrow(TITLE))
                val description = getString(getColumnIndexOrThrow(DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(DATE))
                noteList.add(Note(id, title, description, date))
            }
        }
        return noteList
    }

    fun mapCursorToObject(notesCursor: Cursor?): Note {
        var note = Note()
        notesCursor?.apply {
            moveToFirst()
            val id = getInt(getColumnIndexOrThrow(_ID))
            val title = getString(getColumnIndexOrThrow(TITLE))
            val description = getString(getColumnIndexOrThrow(DESCRIPTION))
            val date = getString(getColumnIndexOrThrow(DATE))
            note = note.copy(id = id, title = title, description = description, date = date)
        }
        return note
    }
}