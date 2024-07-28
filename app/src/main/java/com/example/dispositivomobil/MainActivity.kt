package com.example.dispositivomobil

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.dispositivomobil.ui.theme.DispositivoMobilTheme
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private val notesList = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DispositivoMobilTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NotesApp()
                }
            }
        }

        loadNotes()
    }

    @Composable
    fun NotesApp() {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var showEditDialog by remember { mutableStateOf(false) }
        var noteToEdit by remember { mutableStateOf<Note?>(null) }
        val notes by remember { mutableStateOf(notesList.toList()) }
        val context = LocalContext.current

        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                if (title.isNotEmpty() && description.isNotEmpty()) {
                    val note = Note(
                        title = title,
                        description = description
                    )
                    notesList.add(note)
                    saveNotes()
                    title = ""
                    description = ""
                    Toast.makeText(context, "Nota guardada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Save Note")
            }
            Spacer(modifier = Modifier.height(16.dp))
            NotesList(
                notes = notes,
                onEditNote = { index ->
                    noteToEdit = notes[index]
                    showEditDialog = true
                },
                onDeleteNote = { index ->
                    notesList.removeAt(index)
                    saveNotes()
                }
            )

            // Mostrar el cuadro de diálogo de edición si se ha seleccionado una nota para editar
            noteToEdit?.let { note ->
                if (showEditDialog) {
                    EditNoteDialog(
                        note = note,
                        onNoteUpdated = { updatedNote ->
                            val index = notes.indexOf(note)
                            notesList[index] = updatedNote
                            saveNotes()
                            showEditDialog = false
                        },
                        onDismiss = {
                            showEditDialog = false
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun NotesList(notes: List<Note>, onEditNote: (Int) -> Unit, onDeleteNote: (Int) -> Unit) {
        LazyColumn {
            items(notes) { note ->
                val index = notes.indexOf(note)
                NoteItem(
                    note = note,
                    onEdit = { onEditNote(index) },
                    onDelete = { onDeleteNote(index) }
                )
            }
        }
    }

    @Composable
    fun NoteItem(note: Note, onEdit: () -> Unit, onDelete: () -> Unit) {
        Card(modifier = Modifier.padding(8.dp), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = note.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = note.description)
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(onClick = onEdit) {
                        Text("Edit")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onDelete) {
                        Text("Delete")
                    }
                }
            }
        }
    }

    @Composable
    fun EditNoteDialog(
        note: Note,
        onNoteUpdated: (Note) -> Unit,
        onDismiss: () -> Unit
    ) {
        var newTitle by remember { mutableStateOf(note.title) }
        var newDescription by remember { mutableStateOf(note.description) }
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Edit Note") },
            text = {
                Column {
                    TextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Title") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = newDescription,
                        onValueChange = { newDescription = it },
                        label = { Text("Description") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newTitle.isNotEmpty() && newDescription.isNotEmpty()) {
                        onNoteUpdated(Note(newTitle, newDescription))
                        Toast.makeText(context, "Nota actualizada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }

    private fun saveNotes() {
        val sharedPreferences = getSharedPreferences("notes_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val jsonArray = JSONArray()
        for (note in notesList) {
            val jsonObject = JSONObject()
            jsonObject.put("title", note.title)
            jsonObject.put("description", note.description)
            jsonArray.put(jsonObject)
        }

        editor.putString("notes", jsonArray.toString())
        editor.apply()
    }

    private fun loadNotes() {
        val sharedPreferences = getSharedPreferences("notes_prefs", MODE_PRIVATE)
        val notesJson = sharedPreferences.getString("notes", "[]")

        val jsonArray = JSONArray(notesJson)
        notesList.clear()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val title = jsonObject.getString("title")
            val description = jsonObject.getString("description")
            notesList.add(Note(title, description))
        }
    }
}

data class Note(
    val title: String,
    val description: String
)
