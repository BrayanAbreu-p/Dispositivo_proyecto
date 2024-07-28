package com.example.dispositivomobil

import android.app.Activity
import android.os.Bundle
import android.widget.*
import android.widget.Toast

class MainActivity : Activity() {

    private val notesList = mutableListOf<Note>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        val editTextDescription = findViewById<EditText>(R.id.editTextDescription)
        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val listViewNotes = findViewById<ListView>(R.id.listViewNotes)

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            mutableListOf<String>()
        )
        listViewNotes.adapter = adapter

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                val note = Note(
                    title = title,
                    description = description
                )
                notesList.add(note)
                updateListView()
                editTextTitle.text.clear()
                editTextDescription.text.clear()
                Toast.makeText(this, "Nota guardada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Nota no guardada.Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateListView() {
        val noteStrings = notesList.map { "${it.title}: ${it.description}" }
        adapter.clear()
        adapter.addAll(noteStrings)
        adapter.notifyDataSetChanged()
    }
}

data class Note(
    val title: String,
    val description: String
)
