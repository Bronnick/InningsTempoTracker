package com.inntemp.inningstempotracker.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.inntemp.inningstempotracker.R
import com.inntemp.inningstempotracker.ui.home.MatchCard
import com.inntemp.inningstempotracker.ui.navigation.Screen
import com.inntemp.inningstempotracker.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun InningsLibraryScreen(navController: NavController) {
    val viewModel: InningsLibraryViewModel = koinViewModel()
    val matches by viewModel.matches.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState(initial = "")
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens
    val shapes = LocalAppTheme.shapes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = dimens.md)
    ) {
        Spacer(modifier = Modifier.height(dimens.lg))
        Text(text = stringResource(R.string.tab_library), style = typo.h2, color = colors.textPrimary)
        Spacer(modifier = Modifier.height(dimens.md))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.library_search_hint), color = colors.textSecondary) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = colors.iconInactive) },
            shape = RoundedCornerShape(shapes.lg),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primaryAccent,
                unfocusedBorderColor = colors.border,
                focusedContainerColor = colors.inputBackground,
                unfocusedContainerColor = colors.inputBackground
            )
        )

        Spacer(modifier = Modifier.height(dimens.md))

        if (matches.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (searchQuery.isBlank()) stringResource(R.string.library_empty_title) else stringResource(R.string.library_no_results),
                        style = typo.h3,
                        color = colors.textSecondary
                    )
                    if (searchQuery.isBlank()) {
                        Spacer(modifier = Modifier.height(dimens.sm))
                        Text(text = stringResource(R.string.library_empty_desc), style = typo.body, color = colors.textSecondary)
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(dimens.sm),
                contentPadding = PaddingValues(bottom = dimens.xl)
            ) {
                items(matches) { match ->
                    MatchCard(match = match, onClick = {
                        navController.navigate(Screen.InningsDetail.createRoute(match.id))
                    })
                }
            }
        }
    }
}
