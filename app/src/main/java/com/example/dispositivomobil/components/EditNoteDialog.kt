package com.example.dispositivomobil.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.dispositivomobil.models.Note  // Importa la clase Note

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
                    onDismiss()
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
