package com.erikriosetiawan.mynotesapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.erikriosetiawan.mynotesapp.NoteAddUpdateActivity.Companion.EXTRA_NOTE
import com.erikriosetiawan.mynotesapp.NoteAddUpdateActivity.Companion.EXTRA_POSITION
import com.erikriosetiawan.mynotesapp.NoteAddUpdateActivity.Companion.REQUEST_ADD
import com.erikriosetiawan.mynotesapp.NoteAddUpdateActivity.Companion.REQUEST_UPDATE
import com.erikriosetiawan.mynotesapp.NoteAddUpdateActivity.Companion.RESULT_ADD
import com.erikriosetiawan.mynotesapp.NoteAddUpdateActivity.Companion.RESULT_DELETE
import com.erikriosetiawan.mynotesapp.NoteAddUpdateActivity.Companion.RESULT_UPDATE
import com.erikriosetiawan.mynotesapp.adapter.NoteAdapter
import com.erikriosetiawan.mynotesapp.databinding.ActivityMainBinding
import com.erikriosetiawan.mynotesapp.entity.Note
import com.erikriosetiawan.mynotesapp.helper.MappingHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NoteAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var noteHelper: NoteHelper

    companion object {
        private const val EXTRA_STATE = "extra_state"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Notes"

        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.setHasFixedSize(true)
        adapter = NoteAdapter(this)
        binding.rvNotes.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, NoteAddUpdateActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD)
        }

        noteHelper = NoteHelper.getInstance(applicationContext)
        noteHelper.open()

        if (savedInstanceState == null) {
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null) {
                adapter.listNotes = list
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listNotes)
    }

    override fun onDestroy() {
        super.onDestroy()
        noteHelper.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            when (requestCode) {
                REQUEST_ADD -> if (resultCode == RESULT_ADD) {
                    val note = data.getParcelableExtra<Note>(EXTRA_NOTE) as Note

                    adapter.addItem(note)
                    binding.rvNotes.smoothScrollToPosition(adapter.itemCount - 1)

                    showSnackbarMessage("Satu item berhasil ditambahkan")
                }
                REQUEST_UPDATE ->
                    when (resultCode) {
                        RESULT_UPDATE -> {
                            val note = data.getParcelableExtra<Note>(EXTRA_NOTE) as Note
                            val position = data.getIntExtra(EXTRA_POSITION, 0)

                            adapter.updateItem(position, note)
                            binding.rvNotes.smoothScrollToPosition(position)

                            showSnackbarMessage("Satu item berhasil diubah")
                        }
                        RESULT_DELETE -> {
                            val position = data.getIntExtra(EXTRA_POSITION, 0)
                            adapter.removeItem(position)
                            showSnackbarMessage("Satu item berhasil dihapus")
                        }
                    }
            }
        }
    }

    private fun loadNotesAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = noteHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }
            binding.progressBar.visibility = View.INVISIBLE
            val notes = deferredNotes.await()
            if (notes.size > 0) {
                adapter.listNotes = notes
                Log.i("TES123", adapter.listNotes.toString())
            } else {
                adapter.listNotes = ArrayList()
                showSnackbarMessage("Tidak ada data saat ini")
            }
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.rvNotes, message, Snackbar.LENGTH_SHORT).show()
    }
}