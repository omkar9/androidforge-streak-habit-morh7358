package com.androidforge.streakhabit.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidforge.streakhabit.core.R
import com.androidforge.streakhabit.core.ui.theme.StreakHabitTheme

@Composable
fun EmptyScreen(
    modifier: Modifier = Modifier,
    message: String,
    description: String? = null,
    illustrationResId: Int = R.drawable.empty_illustration_placeholder, // Placeholder drawable
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = illustrationResId),
            contentDescription = stringResource(R.string.cd_empty_illustration),
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        description?.let { desc ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            AppButton(
                onClick = onButtonClick,
                text = buttonText,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyScreenPreview() {
    StreakHabitTheme {
        EmptyScreen(
            message = "No habits yet!",
            description = "Start building powerful streaks by adding your first habit.",
            buttonText = "Add New Habit",
            onButtonClick = { /*TODO*/ }
        )
    }
}