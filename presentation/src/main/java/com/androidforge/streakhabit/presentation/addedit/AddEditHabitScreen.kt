package com.androidforge.streakhabit.presentation.addedit

import android.app.Activity
import android.app.TimePickerDialog
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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidforge.streakhabit.core.R
import com.androidforge.streakhabit.core.common.Constants
import com.androidforge.streakhabit.core.ui.components.AppButton
import com.androidforge.streakhabit.core.ui.components.AppTopBar
import com.androidforge.streakhabit.core.ui.components.ErrorScreen
import com.androidforge.streakhabit.core.ui.components.LoadingIndicator
import com.androidforge.streakhabit.core.ui.components.OfflineScreen
import com.androidforge.streakhabit.core.ui.components.ShimmerLoadingIndicator
import com.androidforge.streakhabit.core.ui.theme.StreakHabitTheme
import com.androidforge.streakhabit.core.utils.DateUtils.getDaysOfWeek
import com.androidforge.streakhabit.core.utils.DateUtils.formatTime
import com.androidforge.streakhabit.domain.model.FrequencyType
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitScreen(
    habitId: Long?,
    onHabitSaved: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AddEditHabitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as Activity

    LaunchedEffect(habitId) {
        if (habitId != null && habitId != Constants.DEFAULT_HABIT_ID) {
            viewModel.loadHabit(habitId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.adEvent.collect { event ->
            when (event) {
                is AddEditHabitViewModel.AdEvent.AdDismissed -> onHabitSaved()
                else -> { /* Handle other ad events if necessary */ }
            }
        }
    }

    Scaffold(topBar = {
        AppTopBar(
            title = stringResource(if (habitId == null || habitId == Constants.DEFAULT_HABIT_ID) R.string.add_habit_title else R.string.edit_habit_title),
            canNavigateBack = true,
            onNavigateBack = onNavigateBack
        )
    }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            AnimatedContent(
                targetState = uiState,
                label = "add_edit_habit_ui_state_animation",
                transitionSpec = {
                    // Custom slide transition for content changes
                    fadeIn(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) +
                            slideInVertically(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) { it / 2 } togetherWith
                            fadeOut(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) +
                            slideOutVertically(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) { -it / 2 }
                }
            ) { targetState ->
                when (targetState) {
                    is AddEditHabitUiState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ShimmerLoadingIndicator(width = 200.dp, height = 32.dp, modifier = Modifier.fillMaxWidth())
                            ShimmerLoadingIndicator(width = 250.dp, height = 100.dp, modifier = Modifier.fillMaxWidth())
                            ShimmerLoadingIndicator(width = 150.dp, height = 32.dp, modifier = Modifier.fillMaxWidth())
                            ShimmerLoadingIndicator(width = 180.dp, height = 32.dp, modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(32.dp))
                            ShimmerLoadingIndicator(width = 100.dp, height = 52.dp, modifier = Modifier.fillMaxWidth())
                        }
                    }
                    is AddEditHabitUiState.Success -> {
                        AddEditHabitContent(
                            uiState = targetState,
                            onNameChange = viewModel::onNameChange,
                            onDescriptionChange = viewModel::onDescriptionChange,
                            onFrequencyTypeChange = viewModel::onFrequencyTypeChange,
                            onToggleDay = viewModel::onToggleDay,
                            onToggleReminder = viewModel::onToggleReminder,
                            onReminderTimeChange = viewModel::onReminderTimeChange,
                            onSaveClick = { viewModel.saveHabit(activity) },
                            onDeleteClick = { viewModel.deleteHabit(activity) },
                            isEditMode = habitId != null && habitId != Constants.DEFAULT_HABIT_ID
                        )
                    }
                    is AddEditHabitUiState.Error -> {
                        ErrorScreen(
                            message = stringResource(R.string.error_loading_habit_title),
                            description = targetState.message ?: stringResource(R.string.error_loading_habit_description),
                            onRetryClick = { if (habitId != null && habitId != Constants.DEFAULT_HABIT_ID) viewModel.loadHabit(habitId) else viewModel.resetState() }
                        )
                    }
                    AddEditHabitUiState.Offline -> {
                        OfflineScreen(onRetryClick = { if (habitId != null && habitId != Constants.DEFAULT_HABIT_ID) viewModel.loadHabit(habitId) else viewModel.resetState() })
                    }
                    AddEditHabitUiState.Saved, AddEditHabitUiState.Deleted -> { /* Handled by LaunchedEffect */ }
                    AddEditHabitUiState.Empty -> { /* Not applicable for AddEdit screen */ }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitContent(
    uiState: AddEditHabitUiState.Success,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onFrequencyTypeChange: (FrequencyType) -> Unit,
    onToggleDay: (DayOfWeek) -> Unit,
    onToggleReminder: (Boolean) -> Unit,
    onReminderTimeChange: (LocalTime) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isEditMode: Boolean
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.habit_name_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            isError = uiState.nameError != null,
            supportingText = { if (uiState.nameError != null) Text(uiState.nameError) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primaryVariant,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                errorLabelColor = MaterialTheme.colorScheme.error,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                errorTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = MaterialTheme.shapes.medium // 8dp rounded
        )

        OutlinedTextField(
            value = uiState.description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.habit_description_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 96.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primaryVariant,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = MaterialTheme.shapes.medium // 8dp rounded
        )

        // Frequency Type
        Text(
            text = stringResource(R.string.frequency_label),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppButton(
                onClick = { onFrequencyTypeChange(FrequencyType.DAILY) },
                modifier = Modifier.weight(1f),
                isPrimary = uiState.frequencyType == FrequencyType.DAILY,
                text = stringResource(R.string.frequency_daily)
            )
            AppButton(
                onClick = { onFrequencyTypeChange(FrequencyType.SPECIFIC_DAYS(emptySet())) },
                modifier = Modifier.weight(1f),
                isPrimary = uiState.frequencyType is FrequencyType.SPECIFIC_DAYS,
                text = stringResource(R.string.frequency_specific_days)
            )
        }

        // Specific Days Selector if applicable
        AnimatedVisibility(
            visible = uiState.frequencyType is FrequencyType.SPECIFIC_DAYS,
            enter = fadeIn(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) + slideInVertically(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) { it / 2 },
            exit = fadeOut(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) + slideOutVertically(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) { -it / 2 }
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    getDaysOfWeek().forEach { (dayOfWeek, shortName) ->
                        val isSelected = (uiState.frequencyType as? FrequencyType.SPECIFIC_DAYS)?.days?.contains(dayOfWeek) == true
                        DayChip(
                            dayName = shortName,
                            isSelected = isSelected,
                            onClick = { onToggleDay(dayOfWeek) }
                        )
                    }
                }
                if (uiState.frequencyError != null) {
                    Text(
                        text = uiState.frequencyError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                    )
                }
            }
        }

        // Reminder Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium) // 8dp rounded
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onToggleReminder(!uiState.isReminderEnabled) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.enable_reminder),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Switch(
                checked = uiState.isReminderEnabled,
                onCheckedChange = onToggleReminder,
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

        // Reminder Time Picker if enabled
        AnimatedVisibility(
            visible = uiState.isReminderEnabled,
            enter = fadeIn(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) + slideInVertically(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) { it / 2 },
            exit = fadeOut(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) + slideOutVertically(animationSpec = tween(Constants.ANIMATION_DURATION_MILLIS)) { -it / 2 }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium) // 8dp rounded
                    .background(MaterialTheme.colorScheme.surface)
                    .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                    .clickable(onClick = {
                        val calendar = Calendar.getInstance().apply {
                            uiState.reminderTime?.let {
                                set(Calendar.HOUR_OF_DAY, it.hour)
                                set(Calendar.MINUTE, it.minute)
                            } ?: run {
                                set(Calendar.HOUR_OF_DAY, LocalTime.now().hour)
                                set(Calendar.MINUTE, LocalTime.now().minute)
                            }
                        }
                        TimePickerDialog(
                            context,
                            {\ _, hour: Int, minute: Int ->
                                onReminderTimeChange(LocalTime.of(hour, minute))
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false // 24-hour format
                        ).show()
                    }, interactionSource = remember { MutableInteractionSource() }, indication = null) // Custom ripple
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.reminder_time_label),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = uiState.reminderTime?.formatTime() ?: stringResource(R.string.set_time),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.cd_select_reminder_time),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        AppButton(
            onClick = onSaveClick,
            text = stringResource(R.string.btn_save_habit),
            enabled = uiState.isFormValid,
            modifier = Modifier.fillMaxWidth()
        )

        // Delete Button (only in edit mode)
        if (isEditMode) {
            Spacer(modifier = Modifier.height(8.dp))
            AppButton(
                onClick = onDeleteClick,
                text = stringResource(R.string.btn_delete_habit),
                isPrimary = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.cd_delete_habit), tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_delete_habit).uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun DayChip(
    dayName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        label = "dayChipBackgroundColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        label = "dayChipContentColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryVariant else MaterialTheme.colorScheme.surfaceVariant,
        label = "dayChipBorderColor"
    )

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small) // 6dp rounded
            .background(backgroundColor)
            .border(2.dp, borderColor, MaterialTheme.shapes.small)
            .clickable(onClick = onClick, interactionSource = remember { MutableInteractionSource() }, indication = null) // Custom ripple
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayName,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditHabitScreenPreview() {
    StreakHabitTheme {
        AddEditHabitScreen(
            habitId = Constants.DEFAULT_HABIT_ID,
            onHabitSaved = { /*TODO*/ },
            onNavigateBack = { /*TODO*/ },
            viewModel = hiltViewModel()
        )
    }
}