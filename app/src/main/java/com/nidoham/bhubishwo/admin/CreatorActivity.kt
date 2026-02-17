package com.nidoham.bhubishwo.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nidoham.bhubishwo.admin.presentation.screen.EXTRA_CREATOR_TYPE
import com.nidoham.bhubishwo.admin.presentation.screen.TYPE_CONTENT
import com.nidoham.bhubishwo.admin.presentation.screen.TYPE_RESOURCE
import com.nidoham.bhubishwo.admin.presentation.screen.creator.ContentCreatorContent
import com.nidoham.bhubishwo.admin.presentation.screen.creator.ResourceCreatorContent
import com.nidoham.bhubishwo.admin.ui.theme.AdminTheme
import com.nidoham.bhubishwo.admin.ui.theme.glassTopBarColor
import com.nidoham.bhubishwo.admin.ui.theme.glassTopBarScrolledColor
import com.nidoham.bhubishwo.admin.ui.theme.glassSurfaceColor

class CreatorActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val creatorType = intent.getStringExtra(EXTRA_CREATOR_TYPE) ?: TYPE_CONTENT

        setContent {
            AdminTheme {
                // Scroll behavior for glass elevation effect
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                    rememberTopAppBarState()
                )

                // Dynamic glass color that changes on scroll
                val topBarColor = if (scrollBehavior.state.collapsedFraction > 0.5f) {
                    glassTopBarScrolledColor()
                } else {
                    glassTopBarColor()
                }

                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        GlassTopBar(
                            creatorType = creatorType,
                            scrollBehavior = scrollBehavior,
                            containerColor = topBarColor,
                            onBackClick = { finish() }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { padding ->
                    // Glass surface container for content
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        color = glassSurfaceColor(elevation = 0),
                        tonalElevation = 0.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopStart // Changed from Center for forms
                        ) {
                            when (creatorType) {
                                TYPE_RESOURCE -> ResourceCreatorContent()
                                else -> ContentCreatorContent()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GlassTopBar(
    creatorType: String,
    scrollBehavior: TopAppBarScrollBehavior,
    containerColor: androidx.compose.ui.graphics.Color,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = when (creatorType) {
                    TYPE_RESOURCE -> "Upload Resource"
                    else -> "New Quiz"
                },
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = glassTopBarScrolledColor()
        ),
        scrollBehavior = scrollBehavior
    )
}