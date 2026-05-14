package com.practicum.playlistmaker.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.creator.Creator
import com.practicum.playlistmaker.ui.theme.AppColors
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    isDarkTheme: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val appShareMessage = stringResource(R.string.share_text)
    val contactEmail = stringResource(R.string.developer_email)
    val messageSubject = stringResource(R.string.email_subject)
    val messageBody = stringResource(R.string.email_body)
    val termsLink = stringResource(R.string.offer_link)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Белый контент
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .background(
                    color = AppColors.white,
                    shape = RoundedCornerShape(0.dp)
                )
        ) {
            // Контент меню
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Темная тема
                DarkThemeMenuItem(
                    isDarkTheme = isDarkTheme,
                    onToggle = { newValue ->
                        onThemeToggle(newValue)
                        coroutineScope.launch {
                            Creator.getThemePreferences().setDarkThemeEnabled(newValue)
                        }
                    }
                )

                // Поделиться приложением
                SettingsMenuItem(
                    title = stringResource(R.string.settings_share_app),
                    icon = Icons.Default.Share,
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, appShareMessage)
                        }
                        val chooser = Intent.createChooser(sendIntent, null)
                        context.startActivity(chooser)
                    }
                )

                // Написать в поддержку
                SettingsMenuItem(
                    title = stringResource(R.string.settings_write_developers),
                    icon = Icons.Default.Feedback,
                    onClick = {
                        val mailto = "mailto:${contactEmail}?subject=${Uri.encode(messageSubject)}&body=${Uri.encode(messageBody)}"
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = mailto.toUri()
                        }
                        val chooser = Intent.createChooser(emailIntent, null)
                        context.startActivity(chooser)
                    }
                )

                // Пользовательское соглашение
                SettingsMenuItem(
                    title = stringResource(R.string.settings_user_agreement),
                    icon = Icons.Default.ChevronRight,
                    onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, termsLink.toUri())
                        context.startActivity(browserIntent)
                    }
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // Синяя шапка поверх контента
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(AppColors.white),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onBackClick() },
                    tint = AppColors.black
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(R.string.settings_title),
                    color = AppColors.black,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp
                    )
                )
            }
        }
    }
}

@Composable
fun DarkThemeMenuItem(
    isDarkTheme: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.settings_dark_theme),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                color = AppColors.black,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = isDarkTheme,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedTrackColor = AppColors.primaryBlue,
                uncheckedTrackColor = AppColors.lightGray,
                checkedThumbColor = AppColors.white,
                uncheckedThumbColor = AppColors.white
            )
        )
    }
}

@Composable
fun SettingsMenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                color = AppColors.black,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = AppColors.gray,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    PlaylistMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = AppColors.white) {
            SettingsScreen(
                onBackClick = { }
            )
        }
    }
}
