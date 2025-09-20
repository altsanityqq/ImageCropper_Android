package com.example.imagecropper_android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import com.example.imagecropper_android.R
import com.example.imagecropper_android.domain.photo.repository.PhotoRepository
import com.example.imagecropper_android.ui.model.PhotoUi
import com.example.imagecropper_android.ui.model.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ImagesListViewModel @Inject constructor(
    private val repo: PhotoRepository
) : ViewModel() {

    val uiState = repo.getAll()
        .map { list -> list.map { it.toUi() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun delete(id: String) = viewModelScope.launch {
        repo.delete(id)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesListScreen(
    onAddClick: () -> Unit = {},
    viewModel: ImagesListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = { Text(text = stringResource(R.string.images_list)) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(80.dp),
                onClick = onAddClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_add_24),
                    contentDescription = null
                )
            }
        }
    ) { paddingValues ->
        if (state.isEmpty()) {
            EmptyState(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state, key = { it.id }) { item ->
                    PhotoRow(
                        item = item,
                        onDelete = { viewModel.delete(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoRow(
    item: PhotoUi,
    onDelete: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = item.originalUri,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 2.dp, bottom = 2.dp)
            ) {
                Text(
                    text = "ID: ${item.id}",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = formatDate(item.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Square: (${item.squareCrop.topLeft.x.toInt()}, ${item.squareCrop.topLeft.y.toInt()}) → (${item.squareCrop.bottomRight.x.toInt()}, ${item.squareCrop.bottomRight.y.toInt()})",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "3:4:   (${item.rectCrop.topLeft.x.toInt()}, ${item.rectCrop.topLeft.y.toInt()}) → (${item.rectCrop.bottomRight.x.toInt()}, ${item.rectCrop.bottomRight.y.toInt()})",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Text(
            stringResource(R.string.empty_list_message),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

private fun formatDate(ts: Long): String {
    val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return df.format(Date(ts))
}