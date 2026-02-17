package com.nidoham.bhubishwo.admin.presentation.screen.creator

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.nidoham.bhubishwo.admin.presentation.screen.content.ImageContentScreen
import com.nidoham.bhubishwo.admin.presentation.screen.content.TextContentScreeen

@Composable
fun ContentCreatorContent(
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    var selectedTab by remember { mutableStateOf(TabType.TEXT) }

    // Use standard Material Theme background
    val bgColor = MaterialTheme.colorScheme.background

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // 1. Fixed Tab Row at the top
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = bgColor, // Now uses Material Theme Background
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                if (selectedTab.ordinal < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            TabType.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Tab(
                    selected = isSelected,
                    onClick = { selectedTab = tab },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2. Content Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(bgColor)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                    (slideInHorizontally { width -> width * direction } + fadeIn()) togetherWith
                            (slideOutHorizontally { width -> width * -direction } + fadeOut())
                },
                modifier = Modifier.fillMaxSize()
            ) { tab ->
                when (tab) {
                    TabType.TEXT -> TextContentScreeen()
                    TabType.IMAGE -> ImageContentScreen()
                }
            }
        }
    }
}

enum class TabType(val label: String, val icon: ImageVector) {
    TEXT("Text", Icons.Outlined.TextFields),
    IMAGE("Image", Icons.Outlined.Image)
}