package com.nidoham.bhubishwo.admin.presentation.screen

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState  // ✅ Fix: was collectIsHoveredAsState — hover never fires on Android touch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard   // ✅ Fix: was Analytics (duplicate with ANALYTICS tab)
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nidoham.bhubishwo.admin.ui.theme.glassBackgroundColor
import com.nidoham.bhubishwo.admin.ui.theme.glassNavBarColor
import com.nidoham.bhubishwo.admin.ui.theme.glassSurfaceColor

enum class AdminTab {
    DASHBOARD, QUIZZES, RESOURCES, ANALYTICS, SETTINGS
}

@Composable
fun MainScreen() {
    var selectedTab    by remember { mutableStateOf(AdminTab.DASHBOARD) }
    var showCreateMenu by remember { mutableStateOf(false) }
    val view = LocalView.current

    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            topBar = {
                SharedTopAppBar(
                    title = when (selectedTab) {
                        AdminTab.DASHBOARD -> "Dashboard"
                        AdminTab.QUIZZES   -> "Quizzes"
                        AdminTab.RESOURCES -> "Resources"
                        AdminTab.ANALYTICS -> "Analytics"
                        AdminTab.SETTINGS  -> "Settings"
                    },
                    onMenuClick   = { },
                    onSearchClick = { }
                )
            },
            bottomBar = {
                SharedNavigationBar(
                    selectedTab   = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = selectedTab == AdminTab.DASHBOARD,
                    enter   = scaleIn(spring(Spring.DampingRatioMediumBouncy)),
                    exit    = scaleOut(tween(150))
                ) {
                    FloatingActionButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            showCreateMenu = !showCreateMenu
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor   = MaterialTheme.colorScheme.onPrimary,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = if (showCreateMenu) 0.dp else 6.dp
                        ),
                        modifier = Modifier.size(56.dp)
                    ) {
                        AnimatedContent(
                            targetState = showCreateMenu,
                            transitionSpec = {
                                scaleIn(spring(Spring.DampingRatioMediumBouncy)) togetherWith
                                        scaleOut(tween(100))
                            }
                        ) { isExpanded ->
                            Icon(
                                imageVector        = if (isExpanded) Icons.Default.Close else Icons.Default.Add,
                                contentDescription = if (isExpanded) "Close" else "Create",
                                modifier           = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                                glassSurfaceColor(elevation = 0).copy(alpha = 0.90f)
                            )
                        )
                    )
            ) {
                when (selectedTab) {
                    AdminTab.DASHBOARD -> DashboardScreen()
                    AdminTab.QUIZZES   -> QuizScreen()
                    AdminTab.RESOURCES -> ResourceScreen()
                    AdminTab.ANALYTICS -> AnalyticsScreen()
                    AdminTab.SETTINGS  -> SettingsScreen()
                }
            }
        }

        AnimatedVisibility(
            visible = showCreateMenu,
            enter   = fadeIn(tween(200)),
            exit    = fadeOut(tween(150))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { showCreateMenu = false }   // tap outside to dismiss
            )
        }

        AnimatedVisibility(
            visible = showCreateMenu,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 88.dp, end = 16.dp),     // sit just above FAB
            enter = slideInVertically(tween(220)) { it } + fadeIn(tween(220)),
            exit  = slideOutVertically(tween(160)) { it } + fadeOut(tween(160))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassMenuItem(
                    icon        = Icons.Default.Quiz,
                    label       = "New Quiz",
                    description = "Create image and name quiz",
                    onClick     = {
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        showCreateMenu = false
                    }
                )
                GlassMenuItem(
                    icon        = Icons.Default.Image,
                    label       = "Upload Image",
                    description = "Add quiz images",
                    onClick     = {
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        showCreateMenu = false
                    }
                )
            }
        }
    }
}

// ══════════════════════════════════════════
// TOP APP BAR WRAPPER
// ══════════════════════════════════════════

@Composable
fun SharedTopAppBar(
    title: String,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    com.nidoham.bhubishwo.admin.presentation.component.GlassTopAppBar(
        title         = title,
        onMenuClick   = onMenuClick,
        onSearchClick = onSearchClick
    )
}

// ══════════════════════════════════════════
// NAVIGATION BAR
// ══════════════════════════════════════════

@Composable
fun SharedNavigationBar(
    selectedTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit
) {
    NavigationBar(
        containerColor = glassNavBarColor(),
        tonalElevation = 0.dp
    ) {
        AdminTab.entries.forEach { tab ->
            val selected = tab == selectedTab

            val icon: ImageVector = when (tab) {
                AdminTab.DASHBOARD -> Icons.Default.Dashboard   // now distinct
                AdminTab.QUIZZES   -> Icons.Default.Quiz
                AdminTab.RESOURCES -> Icons.Default.Folder
                AdminTab.ANALYTICS -> Icons.Default.Analytics
                AdminTab.SETTINGS  -> Icons.Default.Settings
            }

            NavigationBarItem(
                icon = {
                    BadgedBox(
                        badge = {
                            // Only show badge on Dashboard when there are items
                            if (tab == AdminTab.DASHBOARD) {
                                Badge { Text("0") }
                            }
                        }
                    ) {
                        Icon(icon, contentDescription = tab.name)
                    }
                },
                label   = { Text(tab.name.lowercase().replaceFirstChar { it.uppercase() }) },
                selected = selected,
                onClick  = { onTabSelected(tab) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MaterialTheme.colorScheme.primary,
                    selectedTextColor   = MaterialTheme.colorScheme.primary,
                    indicatorColor      = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.50f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun GlassMenuItem(
    icon       : ImageVector,
    label      : String,
    description: String,
    onClick    : () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue  = if (isPressed) 0.97f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label        = "cardScale"
    )

    Card(
        onClick           = onClick,
        interactionSource = interactionSource,
        modifier          = Modifier
            .width(280.dp)
            .scale(scale),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = glassSurfaceColor(elevation = 2)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier           = Modifier.padding(16.dp),
            verticalAlignment  = Alignment.CenterVertically
        ) {
            Surface(
                shape    = CircleShape,
                color    = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector        = icon,
                        contentDescription = null,
                        modifier           = Modifier.size(24.dp),
                        tint               = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text       = label,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text  = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
