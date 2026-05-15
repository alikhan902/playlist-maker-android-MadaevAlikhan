package com.practicum.playlistmaker.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.screens.playlists.PlaylistViewModel
import com.practicum.playlistmaker.ui.theme.AppColors
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPlaylistScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PlaylistViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = PlaylistViewModel.getViewModelFactory()
    )

    var playlistTitle by remember { mutableStateOf("") }
    var playlistDetails by remember { mutableStateOf("") }
    val selectedCoverImageUri by viewModel.coverImageUri.collectAsState()

    val context = LocalContext.current

    // ВЫБОР КАРТИНКИ
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            try {
                context.contentResolver.takePersistableUriPermission(it, flags)
            } catch (_: Exception) { }

            viewModel.setCoverImageUri(it.toString())
        }
    }

    // Разрешения для старых Android
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            imagePickerLauncher.launch(arrayOf("image/*"))
        }
    }


    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .background(color = AppColors.white)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(205.dp)
                            .clickable {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    imagePickerLauncher.launch(arrayOf("image/*"))
                                } else {
                                    when {
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                                            imagePickerLauncher.launch(arrayOf("image/*"))
                                        }

                                        else -> {
                                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                        }
                                    }
                                }
                            }
                    ) {
                        if (selectedCoverImageUri != null) {
                            AsyncImage(
                                model = selectedCoverImageUri!!.toUri(),
                                contentDescription = stringResource(R.string.playlist_cover),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = stringResource(R.string.add_cover),
                                tint = AppColors.gray,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {

                    OutlinedTextField(
                        value = playlistTitle,
                        onValueChange = { playlistTitle = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        label = {
                            Row {
                                Text(
                                    stringResource(R.string.playlist_name_placeholder),
                                    color = AppColors.gray
                                )
                                Text(
                                    "*",
                                    color = androidx.compose.ui.graphics.Color.Red,
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.primaryBlue,
                            unfocusedBorderColor = AppColors.gray,
                            focusedTextColor = AppColors.black,
                            unfocusedTextColor = AppColors.black,
                            focusedContainerColor = AppColors.white,
                            unfocusedContainerColor = AppColors.white
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = playlistDetails,
                        onValueChange = { playlistDetails = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        label = {
                            Text(
                                stringResource(R.string.playlist_description_placeholder),
                                color = AppColors.gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.primaryBlue,
                            unfocusedBorderColor = AppColors.gray,
                            focusedTextColor = AppColors.black,
                            unfocusedTextColor = AppColors.black,
                            focusedContainerColor = AppColors.white,
                            unfocusedContainerColor = AppColors.white
                        )
                    )

                    Button(
                        onClick = {
                            if (playlistTitle.isNotEmpty()) {
                                viewModel.createNewPlaylist(
                                    playlistTitle,
                                    playlistDetails,
                                    selectedCoverImageUri
                                )
                                onSaveClick()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = playlistTitle.isNotEmpty(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.primaryBlue,
                            contentColor = AppColors.white
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.save_playlist),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }


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
                    contentDescription = stringResource(R.string.back_button),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onBackClick() },
                    tint = AppColors.black
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(R.string.new_playlist_title),
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

@Preview(showBackground = true)
@Composable
fun NewPlaylistPreview() {
    PlaylistMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = AppColors.white) {
            NewPlaylistScreen(
                onBackClick = { },
                onSaveClick = { }
            )
        }
    }
}

