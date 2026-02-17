package com.nidoham.bhubishwo.admin.presentation.screen.creator

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.nidoham.bhubishwo.admin.ui.theme.GlassDark
import com.nidoham.bhubishwo.admin.ui.theme.GlassLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContentCreatorContent(
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    // Glass background with subtle gradient
    val glassBg = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                GlassDark.Background,
                GlassDark.BackgroundElevated
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                GlassLight.Background,
                GlassLight.BackgroundGrouped
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(glassBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

    }
}