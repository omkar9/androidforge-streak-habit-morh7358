package com.androidforge.streakhabit.presentation.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidforge.streakhabit.core.R
import com.androidforge.streakhabit.core.common.Constants
import com.androidforge.streakhabit.core.ui.components.AdBanner
import com.androidforge.streakhabit.core.ui.components.AppBottomBar
import com.androidforge.streakhabit.core.ui.components.AppTopBar
import com.androidforge.streakhabit.core.ui.components.ErrorScreen
import com.androidforge.streakhabit.core.ui.components.OfflineScreen
import com.androidforge.streakhabit.core.ui.components.ShimmerLoadingIndicator
import com.androidforge.streakhabit.core.ui.theme.StreakHabitTheme
import com.androidforge.streakhabit.domain.model.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    navigateToDashboard: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.settings_title),
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            AppBottomBar(
                currentRoute = "settings",
                onItemClick = {\ route ->
                    when (route) {
                        "dashboard" -> navigateToDashboard()
                        "settings" -> { /* Already here */ }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            AnimatedContent(
                targetState = uiState,
                label = "settings_ui_state_animation",
                transitionSpec = {
                    fadeIn(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) +
                            slideInVertically(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) { it / 2 } togetherWith
                            fadeOut(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) +
                            slideOutVertically(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) { -it / 2 }
                }
            ) { targetState ->
                when (targetState) {
                    is SettingsUiState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ShimmerLoadingIndicator(width = 200.dp, height = 28.dp)
                            ShimmerLoadingIndicator(width = 250.dp, height = 60.dp, shape = MaterialTheme.shapes.medium)
                            Spacer(Modifier.height(16.dp))
                            ShimmerLoadingIndicator(width = 180.dp, height = 28.dp)
                            ShimmerLoadingIndicator(width = 280.dp, height = 120.dp, shape = MaterialTheme.shapes.medium)
                        }
                    }
                    is SettingsUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                SettingCategoryTitle(stringResource(R.string.notifications_category))
                                SettingItemToggle(
                                    label = stringResource(R.string.enable_reminders),
                                    checked = targetState.notificationsEnabled,
                                    onCheckedChange = { isChecked ->
                                        viewModel.setNotificationEnabled(isChecked)
                                        // Optionally navigate to app settings if user needs to grant permission manually
                                        if (isChecked && !viewModel.areNotificationsGloballyEnabled(context)) {
                                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                                data = Uri.fromParts("package", context.packageName, null)
                                            }
                                            context.startActivity(intent)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            item {
                                SettingCategoryTitle(stringResource(R.string.appearance_category))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.surface)
                                        .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                                        .padding(vertical = 8.dp)
                                ) {
                                    AppTheme.values().forEach { theme ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { viewModel.setAppTheme(theme) }
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = when (theme) {
                                                    AppTheme.SYSTEM -> stringResource(R.string.theme_system_default)
                                                    AppTheme.LIGHT -> stringResource(R.string.theme_light)
                                                    AppTheme.DARK -> stringResource(R.string.theme_dark)
                                                },
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            RadioButton(
                                                selected = targetState.appTheme == theme,
                                                onClick = { viewModel.setAppTheme(theme) },
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = MaterialTheme.colorScheme.primary,
                                                    unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                )
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            item {
                                AdBanner(modifier = Modifier.fillMaxWidth())
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    is SettingsUiState.Error -> {
                        ErrorScreen(
                            message = stringResource(R.string.error_loading_settings_title),
                            description = targetState.message ?: stringResource(R.string.error_loading_settings_description),
                            onRetryClick = { viewModel.loadSettings() }
                        )
                    }
                    SettingsUiState.Empty -> { /* Not applicable for settings */ }
                    SettingsUiState.Offline -> {
                        OfflineScreen(onRetryClick = { viewModel.loadSettings() })
                    }
                }
            }
        }
    }
}

@Composable
fun SettingCategoryTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingItemToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium) // 8dp rounded
            .background(MaterialTheme.colorScheme.surface)
            .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                checkedBorderColor = MaterialTheme.colorScheme.primaryVariant,
                uncheckedBorderColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    StreakHabitTheme {
        SettingsScreen(
            onNavigateBack = { /*TODO*/ },
            navigateToDashboard = { /*TODO*/ },
            viewModel = hiltViewModel()
        )
    }
}