package com.nidoham.bhubishwo.admin.presentation.screen.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nidoham.bhubishwo.admin.data.repository.ResourceRepository
import com.nidoham.bhubishwo.admin.presentation.component.dialog.ImageUrlPickerDialog
import com.nidoham.bhubishwo.admin.ui.theme.*

@Composable
fun ImageContentScreen() {
    // --- State ---
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var showImageDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }

    // Options
    var option1 by remember { mutableStateOf("") }
    var option2 by remember { mutableStateOf("") }
    var option3 by remember { mutableStateOf("") }
    var option4 by remember { mutableStateOf("") }

    // -1 means no option is selected as correct
    var correctOptionIndex by remember { mutableIntStateOf(-1) }
    var isLoading by remember { mutableStateOf(false) }

    // Dependencies
    // Ideally injected via Hilt/Koin, but instantiated here for the snippet context
    val repository = remember { ResourceRepository() }

    // Theme Logic
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    // Apply Premium Glass Theme Colors
    val bgColor = if (isDark) GlassDark.Background else GlassLight.Background
    val primaryColor = if (isDark) PrimaryDark else PrimaryLight

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // --- Scrollable Content ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 100.dp), // Padding for bottom button
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Big Clickable Image Card
            GlassImagePicker(
                imageUri = selectedImageUri,
                onClick = { showImageDialog = true }, // Opens the Dialog
                isDark = isDark
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Title Input
            GlassyInput(
                value = title,
                onValueChange = { title = it },
                label = "Question / Title",
                placeholder = "What is shown in the image?",
                isDark = isDark
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Options Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ANSWERS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) GlassDark.GlassBorder else GlassLight.GlassBorder,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Select correct option",
                    fontSize = 12.sp,
                    color = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Options List
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassyOptionRow(0, option1, { option1 = it }, correctOptionIndex == 0, { correctOptionIndex = 0 }, isDark, "Option A")
                GlassyOptionRow(1, option2, { option2 = it }, correctOptionIndex == 1, { correctOptionIndex = 1 }, isDark, "Option B")
                GlassyOptionRow(2, option3, { option3 = it }, correctOptionIndex == 2, { correctOptionIndex = 2 }, isDark, "Option C")
                GlassyOptionRow(3, option4, { option4 = it }, correctOptionIndex == 3, { correctOptionIndex = 3 }, isDark, "Option D")
            }
        }

        // --- Fixed Bottom Button ---
        val isValid = title.isNotBlank() &&
                option1.isNotBlank() && option2.isNotBlank() &&
                option3.isNotBlank() && option4.isNotBlank() &&
                correctOptionIndex != -1 && selectedImageUri != null

        // Gradient fade for the bottom area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            bgColor.copy(alpha = 0f),
                            bgColor.copy(alpha = 0.9f),
                            bgColor
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Button(
                onClick = {
                    if (isValid) {
                        isLoading = true
                        // TODO: Implement Push Logic using Repository
                        // repository.push(Resource(...))
                    }
                },
                enabled = isValid && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    disabledContainerColor = if (isDark) GlassDark.SurfaceVariant else GlassLight.SurfaceVariant,
                    disabledContentColor = if (isDark) Color.White.copy(0.3f) else Color.Black.copy(0.3f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 4.dp
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Publish",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Publish Content",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // --- Image URL Picker Dialog ---
        ImageUrlPickerDialog(
            visible = showImageDialog,
            onDismiss = { showImageDialog = false },
            onUrlConfirmed = { url ->
                selectedImageUri = url
                // Dialog closes automatically after confirmation animation
            },
            initialUrl = selectedImageUri ?: "",
            repository = repository
        )
    }
}

// ==========================================
// Sub-Composables (Kept consistent with UI)
// ==========================================

@Composable
private fun GlassImagePicker(
    imageUri: String?,
    onClick: () -> Unit,
    isDark: Boolean
) {
    val borderColor = if (isDark) GlassDark.GlassBorder else GlassLight.GlassBorder
    val surfaceColor = if (isDark) GlassDark.Surface else GlassLight.Surface
    val iconColor = if (isDark) PrimaryDark else PrimaryLight

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.6f)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected Content",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Overlay to indicate editability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Change Image",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap to change",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            } else {
                // Empty State
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Add Photo",
                        modifier = Modifier.size(56.dp),
                        tint = iconColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tap to add image URL",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) Color.White.copy(0.6f) else Color.Black.copy(0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassyInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isDark: Boolean
) {
    val containerColor = if (isDark) GlassDark.Elevation1 else GlassLight.Elevation1
    val borderColor = if (isDark) GlassDark.GlassBorderSubtle else GlassLight.GlassBorderSubtle
    val focusedBorder = if (isDark) PrimaryDark else PrimaryLight
    val textColor = if (isDark) Color.White else Color(0xFF0A1628)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) Color.White.copy(0.7f) else Color.Black.copy(0.6f),
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = if (isDark) Color.White.copy(0.3f) else Color.Black.copy(0.3f)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = containerColor,
                unfocusedContainerColor = if (isDark) GlassDark.Surface else GlassLight.Surface,
                focusedBorderColor = focusedBorder,
                unfocusedBorderColor = borderColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = focusedBorder
            )
        )
    }
}

@Composable
private fun GlassyOptionRow(
    index: Int,
    text: String,
    onTextChange: (String) -> Unit,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isDark: Boolean,
    placeholder: String
) {
    val containerColor = if (isDark) GlassDark.Surface else GlassLight.Surface
    val activeBorder = if (isDark) PrimaryDark else PrimaryLight
    val inactiveBorder = if (isDark) GlassDark.GlassBorderSubtle else GlassLight.GlassBorderSubtle
    val textColor = if (isDark) Color.White else Color(0xFF0A1628)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = activeBorder,
                unselectedColor = if (isDark) Color.White.copy(0.4f) else Color.Black.copy(0.4f)
            )
        )

        Spacer(modifier = Modifier.width(4.dp))

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = placeholder,
                    color = if (isDark) Color.White.copy(0.3f) else Color.Black.copy(0.3f)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (isDark) GlassDark.Elevation1 else GlassLight.Elevation1,
                unfocusedContainerColor = containerColor,
                focusedBorderColor = activeBorder,
                unfocusedBorderColor = if (text.isNotBlank()) activeBorder.copy(alpha = 0.5f) else inactiveBorder,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = activeBorder
            )
        )
    }
}