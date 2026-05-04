package com.androidforge.streakhabit.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidforge.streakhabit.core.R
import com.androidforge.streakhabit.core.ui.theme.StreakHabitTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings

sealed class BottomNavItem(val route: String, val labelResId: Int, val icon: ImageVector, val selectedIcon: ImageVector) {
    object Dashboard : BottomNavItem("dashboard", R.string.bottom_nav_dashboard, Icons.Outlined.Home, Icons.Filled.Home)
    object Settings : BottomNavItem("settings", R.string.bottom_nav_settings, Icons.Outlined.Settings, Icons.Filled.Settings)
    // Add more items as needed
}

@Composable
fun AppBottomBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(BottomNavItem.Dashboard, BottomNavItem.Settings)

    Column(modifier = modifier.fillMaxWidth()) {
        // Strong 2dp line at the top for separation
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant))

        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)), // 16dp top corners
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp // No elevation
        ) {
            navItems.forEach { item ->
                val selected = currentRoute == item.route
                AppBottomBarItem(
                    selected = selected,
                    onClick = { onItemClick(item.route) },
                    icon = { Icon(imageVector = if (selected) item.selectedIcon else item.icon, contentDescription = stringResource(item.labelResId)) },
                    label = { Text(text = stringResource(item.labelResId), style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
    }
}

@Composable
fun RowScope.AppBottomBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.secondary,
            selectedTextColor = MaterialTheme.colorScheme.secondary,
            indicatorColor = Color.Transparent, // We will draw our own indicator
            unselectedIconColor = MaterialTheme.colorScheme.onSurface,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
    // Custom indicator for selected item
    if (selected) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp) // 2dp thick underline
                .background(MaterialTheme.colorScheme.secondary)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomBarPreview() {
    StreakHabitTheme {
        AppBottomBar(
            currentRoute = "dashboard",
            onItemClick = {}
        )
    }
}