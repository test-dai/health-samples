/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.healthplatformsample.presentation.ui.ListSessionsScreen

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthplatformsample.R
import com.example.healthplatformsample.data.HealthPlatformManager
import com.example.healthplatformsample.presentation.components.SessionRow
import java.util.UUID

@Composable
fun ListSessionsScreen(
    healthPlatformManager: HealthPlatformManager,
    onDetailsClick: (String) -> Unit,
    onError: (Context, Throwable?) -> Unit,
    viewModel: ListSessionsViewModel = viewModel(
        factory = ListSessionsViewModelFactory(
            healthPlatformManager = healthPlatformManager
        )
    )
) {
    val sessionsList by viewModel.sessionsList
    val state = viewModel.uiState
    val context = LocalContext.current
    // Remember the last error ID, such that it is possible to avoid re-launching the error
    // notification for the same error when the screen is recomposed, or configuration changes etc.
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }

    // The [MainModel.UiState] provides details of whether the last action was a success or resulted
    // in an error. Where an error occurred, for example in reading and writing to Health Platform,
    // the user is notified, and where the error is one that can be recovered from, an attempt to
    // do so is made.
    LaunchedEffect(state) {
        if (state is ListSessionsViewModel.UiState.Error && errorId.value != state.uuid) {
            onError(context, state.exception)
            errorId.value = state.uuid
        }
    }

    val modifier = Modifier.padding(4.dp)
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Button(
                modifier = modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
                    viewModel.insertSessionData()
                }
            ) {
                Text(stringResource(id = R.string.add_session))
            }
        }
        items(sessionsList) { session ->
            SessionRow(
                session.start,
                session.end,
                session.uid,
                session.name,
                modifier = modifier,
                onDeleteClick = { uid ->
                    viewModel.deleteSession(uid)
                },
                onDetailsClick = onDetailsClick
            )
        }
    }
}
