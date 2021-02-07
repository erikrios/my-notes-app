package com.erikriosetiawan.mynotesapp

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.erikriosetiawan.mynotesapp.databinding.ActivityNoteAddUpdateBinding
import com.erikriosetiawan.mynotesapp.db.DatabaseContract
import com.erikriosetiawan.mynotesapp.db.DatabaseContract.NoteColumns.Companion.CONTENT_URI
import com.erikriosetiawan.mynotesapp.db.DatabaseContract.NoteColumns.Companion.DATE
import com.erikriosetiawan.mynotesapp.entity.Note
import com.erikriosetiawan.mynotesapp.helper.MappingHelper
import java.text.SimpleDateFormat
import java.util.*

class NoteAddUpdateActivity : AppCompatActivity(), View.OnClickListener {

    private var isEdit = false
    private var note: Note? = null
    private var position: Int = 0
    private lateinit var binding: ActivityNoteAddUpdateBinding
    private lateinit var uriWithId: Uri

    companion object {
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"
        const val REQUEST_ADD = 100
        const val REQUEST_UPDATE = 200
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        note = intent.getParcelableExtra(EXTRA_NOTE)
        if (note != null) {
            position = intent.getIntExtra(EXTRA_POSITION, 0)
            isEdit = true
        } else {
            note = Note()
        }

        val actionBarTitle: String
        val btnTitle: String

        if (isEdit) {
            // This Uru yang will be user to put the data from provider
            // content://com.erikriosetiawan.mynotesapp/note/id

            uriWithId = Uri.parse("$CONTENT_URI/${note?.id}")

            val cursor = contentResolver.query(uriWithId, null, null, null, null)
            cursor?.let {
                note = MappingHelper.mapCursorToObject(cursor)
                it.close()
            }

            actionBarTitle = " Ubah"
            btnTitle = "Update"

            note?.let {
                binding.etTitle.setText(it.title)
                binding.etDescription.setText(it.description)
            }
        } else {
            actionBarTitle = "Tambah"
            btnTitle = "Simpan"
        }

        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.btnSubmit.text = btnTitle

        binding.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        if (v?.id == binding.btnSubmit.id) {
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()

            if (title.isEmpty()) {
                binding.etTitle.error = "Field can not be blank"
                return
            }

            note?.title = title
            note?.description = description

            val intent = Intent().apply {
                putExtra(EXTRA_NOTE, note)
                putExtra(EXTRA_POSITION, position)
            }

            val values = ContentValues().apply {
                put(DatabaseContract.NoteColumns.TITLE, title)
                put(DatabaseContract.NoteColumns.DESCRIPTION, description)
            }

            if (isEdit) {
                // User uriWithId from this activity intent
                // content://com.erikriosetiawan.mynotesapp/note/id
                contentResolver.update(uriWithId, values, null, null)
                Toast.makeText(this, "Satu item berhasil diedit", Toast.LENGTH_SHORT).show()
                finish()

            } else {
                values.put(DATE, getCurrentDate())
                // Use content uri to insert
                // content://com.erikriosetiawan.mynotesapp/note/
                contentResolver.insert(CONTENT_URI, values)
                Toast.makeText(this, "Satu item berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isEdit) {
            menuInflater.inflate(R.menu.menu_form, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val date = Date()

        return dateFormat.format(date)
    }

    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose) {
            dialogTitle = "Batal"
            dialogMessage = "Apakah Anda ingin membatalkan perubahan pada form?"
        } else {
            dialogMessage = "Apakah Anda yakin ingin menghapus item ini?"
            dialogTitle = "Hapus Note"
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                if (isDialogClose) {
                    finish()
                } else {
                    // Use uriWithId to delete
                    // content://com.erikriosetiawan.mynotesapp/note/id
                    contentResolver.delete(uriWithId, null, null)
                    Toast.makeText(this, "Satu item berhasil dihapus", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}