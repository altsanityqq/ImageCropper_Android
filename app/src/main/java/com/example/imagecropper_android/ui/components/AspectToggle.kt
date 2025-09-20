package com.example.imagecropper_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.imagecropper_android.ui.model.CropAspect
import com.example.imagecropper_android.ui.theme.ImageCropper_AndroidTheme

@Composable
fun AspectToggle(
    modifier: Modifier = Modifier,
    activeAspect: CropAspect,
    onAspectChange: (CropAspect) -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        SegmentedChip(
            text = "1:1",
            selected = activeAspect == CropAspect.Square
        ) { onAspectChange(CropAspect.Square) }

        SegmentedChip(
            text = "3:4",
            selected = activeAspect == CropAspect.Ratio3x4
        ) { onAspectChange(CropAspect.Ratio3x4) }
    }
}

@Composable
private fun SegmentedChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    else androidx.compose.ui.graphics.Color.Transparent
    val fg = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurface
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = fg),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
    ) {
        Text(text)
    }
}

@Preview
@Composable
private fun AspectTogglePreview() {
    var activeAspect by remember { mutableStateOf(CropAspect.Square) }
    ImageCropper_AndroidTheme {
        AspectToggle(
            activeAspect = activeAspect,
            onAspectChange = {
                activeAspect = it
            }
        )
    }
}