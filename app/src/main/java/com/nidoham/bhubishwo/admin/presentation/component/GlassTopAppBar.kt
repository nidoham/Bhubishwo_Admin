package com.nidoham.bhubishwo.admin.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nidoham.bhubishwo.admin.ui.theme.TopBarColor
import com.nidoham.bhubishwo.admin.ui.theme.glassTopBarColor
import com.nidoham.bhubishwo.admin.ui.theme.glassTopBarScrolledColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassTopAppBar(
    title: String,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    // Compute composable colors before Brush lambda (non-composable context)
    val topColor    = glassTopBarColor()
    val scrollColor = glassTopBarScrolledColor()

    // Gradient fades slightly at the bottom edge — gives depth without harsh line
    val gradientBottom = topColor.copy(alpha = topColor.alpha * 0.85f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(topColor, gradientBottom)
                )
            )
    ) {
        TopAppBar(
            title = {
                Text(
                    text       = title,
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
            },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector        = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint               = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector        = Icons.Default.Search,
                        contentDescription = "Search",
                        tint               = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { }) {
                    BadgedBox(
                        badge = {
                            Badge(containerColor = MaterialTheme.colorScheme.error) {
                                Text("3", fontSize = 10.sp)
                            }
                        }
                    ) {
                        Icon(
                            imageVector        = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint               = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor             = TopBarColor,
                scrolledContainerColor     = scrollColor,
                titleContentColor          = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor     = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}