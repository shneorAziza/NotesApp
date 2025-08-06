package com.shneor.notesapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.shneor.notesapp.model.Note
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

@Composable
fun NotesMap(
    notes: List<Note>,
    onNoteClick: (String) -> Unit
) {
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    val defaultLocation = LatLng(32.0853, 34.7818)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = properties,
            cameraPositionState = cameraPositionState
        ) {
            notes.forEach { note ->
                Marker(
                    state = rememberMarkerState(
                        position = LatLng(note.latitude, note.longitude),
                        key = note.id ?: ""
                    ),
                    title = note.title,
                    snippet = note.content,
                    onClick = {
                        onNoteClick(note.id)
                        true
                    }
                )
            }
        }
    }
}