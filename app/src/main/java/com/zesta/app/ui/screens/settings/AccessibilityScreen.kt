package com.zesta.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zesta.app.R

// Estado local — en producción conectar a un ViewModel + DataStore
private data class AccessibilityState(
    val largeText: Boolean = false,
    val highContrast: Boolean = false,
    val reduceMotion: Boolean = false,
    val audioDescriptions: Boolean = false
)

/** Ajustes de accesibilidad visuales y de audio. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilityScreen(onBack: () -> Unit) {

    var state by remember { mutableStateOf(AccessibilityState()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.accessibility_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { SectionHeader(stringResource(R.string.accessibility_sec_text)) }
            item {
                SettingToggle(
                    title = stringResource(R.string.accessibility_large_text_title),
                    description = stringResource(R.string.accessibility_large_text_desc),
                    checked = state.largeText,
                    onToggle = { state = state.copy(largeText = it) }
                )
            }
            item { SectionHeader(stringResource(R.string.accessibility_sec_visual)) }
            item {
                SettingToggle(
                    title = stringResource(R.string.accessibility_high_contrast_title),
                    description = stringResource(R.string.accessibility_high_contrast_desc),
                    checked = state.highContrast,
                    onToggle = { state = state.copy(highContrast = it) }
                )
            }
            item {
                SettingToggle(
                    title = stringResource(R.string.accessibility_reduce_motion_title),
                    description = stringResource(R.string.accessibility_reduce_motion_desc),
                    checked = state.reduceMotion,
                    onToggle = { state = state.copy(reduceMotion = it) }
                )
            }
            item { SectionHeader(stringResource(R.string.accessibility_sec_audio)) }
            item {
                SettingToggle(
                    title = stringResource(R.string.accessibility_audio_desc_title),
                    description = stringResource(R.string.accessibility_audio_desc_desc),
                    checked = state.audioDescriptions,
                    onToggle = { state = state.copy(audioDescriptions = it) }
                )
            }
            item {
                Spacer(Modifier.height(4.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.accessibility_system_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(text = title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun SettingToggle(title: String, description: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}