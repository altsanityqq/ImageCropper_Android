package com.example.imagecropper_android.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.imagecropper_android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesListScreen(
    onAddClick: () -> Unit = {}
) {
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
        ImagesListScreenContent(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun ImagesListScreenContent(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // TODO
    }
}

@Preview
@Composable
private fun ImagesListPreview() {
    ImagesListScreenContent()
}