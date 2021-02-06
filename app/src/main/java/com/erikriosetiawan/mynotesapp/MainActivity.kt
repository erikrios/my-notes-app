package com.erikriosetiawan.mynotesapp

import android.content.Intent
import android.os.Bundle
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
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NoteAdapter
    private lateinit var binding: ActivityMainBinding

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

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.rvNotes, message, Snackbar.LENGTH_SHORT).show()
    }
}