package com.erikriosetiawan.mynotesapp.db

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {

    const val AUTHORITY = "com.erikriosetiawan.mynotesapp"
    const val SCHEME = "content"

    class NoteColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "note"
            const val _ID = "_id"
            const val TITLE = "title"
            const val DESCRIPTION = "description"
            const val DATE = "date"

            // To create URI content://com.erikriosetiawan.mynotesapp/note
            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}