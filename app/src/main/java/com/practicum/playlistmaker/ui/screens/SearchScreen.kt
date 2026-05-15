package com.practicum.playlistmaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.screens.search.SearchState
import com.practicum.playlistmaker.ui.screens.search.SearchViewModel
import com.practicum.playlistmaker.ui.screens.search.components.TrackListItem
import com.practicum.playlistmaker.ui.theme.AppColors
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SearchViewModel = viewModel(factory = SearchViewModel.getViewModelFactory())
    val screenState by viewModel.searchScreenState.collectAsState()
    val searchText by viewModel.searchTextState.collectAsState()
    val history by viewModel.historyState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .background(AppColors.white)
        ) {

            Column(modifier = Modifier.fillMaxSize().padding(vertical = 16.dp)) {
                SearchTextFieldWithSuggestions(
                    value = searchText,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    onSearchClick = {
                        val q = searchText.trim()
                        if (q.isNotEmpty()) coroutineScope.launch { viewModel.executeSearchFromUI(q) }
                    },
                    onClearClick = { viewModel.resetSearch() },
                    suggestions = history,
                    onSuggestionClick = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (screenState) {
                    is SearchState.Initial -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.search_initial_text),
                            fontSize = 16.sp,
                            color = AppColors.gray
                        )
                    }
                    is SearchState.Searching -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                    is SearchState.Success -> {
                        val tracks = (screenState as SearchState.Success).foundList
                        if (tracks.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        painter = painterResource(R.drawable.no_tracks),
                                        contentDescription = null,
                                        modifier = Modifier.size(120.dp),
                                        tint = null
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(R.string.search_no_results),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AppColors.black
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
                            ) {
                                items(tracks) { track ->
                                    TrackListItem(
                                        track = track,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .combinedClickable(
                                                onClick = { onTrackClick(track) }
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    is SearchState.Fail -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(R.drawable.no_connection),
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = null
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.search_network_error),
                                fontSize = 16.sp,
                                color = AppColors.black,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.search_network_error_2),
                                fontSize = 16.sp,
                                color = AppColors.black,
                                modifier = Modifier.fillMaxWidth(),
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                coroutineScope.launch { viewModel.executeSearchFromUI(searchText) }
                            }) {
                                Text(text = stringResource(R.string.refresh_button))
                            }
                        }
                    }
                }
            }
        }

        // Верхняя панель
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(AppColors.white),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                    modifier = Modifier.size(32.dp).clickable { onBackClick() },
                    tint = AppColors.black
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.search_screen_title),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextFieldWithSuggestions(
    value: String,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onClearClick: () -> Unit,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = isFocused && it.isEmpty() && suggestions.isNotEmpty()
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    expanded = isFocused && value.isEmpty() && suggestions.isNotEmpty()
                },
            placeholder = { Text(stringResource(R.string.search_hint), color = AppColors.gray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_icon),
                    tint = AppColors.gray,
                    modifier = Modifier.clickable { onSearchClick() }
                )
            },
            trailingIcon = {
                if (value.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.clear_search),
                        modifier = Modifier.clickable {
                            onClearClick()
                            expanded = isFocused && suggestions.isNotEmpty()
                        },
                        tint = AppColors.gray
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.gray,
                unfocusedBorderColor = AppColors.lightGray,
                focusedContainerColor = AppColors.searchBackground,
                unfocusedContainerColor = AppColors.searchBackground,
                cursorColor = AppColors.black,
                focusedTextColor = AppColors.black,
                unfocusedTextColor = AppColors.black
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp, topStart = 16.dp, topEnd = 16.dp))
                    .background(AppColors.searchBackground)
            ) {
                Column {
                    suggestions.forEach { suggestion ->
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = AppColors.lightGray
                                )
                            },
                            text = { Text(suggestion, color = AppColors.black) },
                            onClick = {
                                onSuggestionClick(suggestion)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    PlaylistMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = AppColors.white) {
            SearchScreen(
                onBackClick = { },
                onTrackClick = { }
            )
        }
    }
}

