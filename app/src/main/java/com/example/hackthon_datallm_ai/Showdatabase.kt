package com.example.hackthon_datallm_ai

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hackthon_datallm_ai.Database.DatabaseHelper
import com.example.hackthon_datallm_ai.geminidatamanager.ChatViewModel

class Showdatabase {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun Showdata(context: Context, chatViewModel: ChatViewModel, navController: NavController) {

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.popBackStack() }) {

                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Localized description",
                    )

                }
            },
            topBar = {

                CenterAlignedTopAppBar(
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Localized description",
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Localized description",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("DataGene 📊")
                    }
                )
            }
        ) {
            val cursor = chatViewModel.getAllData()

            // If cursor is not null and has data
            if (cursor != null ) {
//                cursor.moveToFirst()
                val attributes = chatViewModel._attributes.map { it.first }

                LazyColumn(Modifier.padding(it).fillMaxWidth()) {
                    item {
                        Surface(
                            modifier = Modifier.padding(8.dp).fillMaxWidth().horizontalScroll(rememberScrollState()),
                            shadowElevation = 4.dp,
                            tonalElevation = 5.dp,

                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    ,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Display attributes as table headers
                                attributes.forEach { attribute ->
                                    Text(
                                        text = attribute,
                                        modifier = Modifier
                                            .weight(2f)
                                            .padding(horizontal = 4.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                    items(
                        DatabaseHelper(context).getDataFromCursor(
                            cursor,
                            chatViewModel._attributes
                        )
                    ) { rowData ->
                        Surface(
                            modifier = Modifier.padding(8.dp),
                            shadowElevation = 4.dp,
                            tonalElevation = 5.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Display data rows
                                rowData.split(", ").forEach { entry ->
                                    val (key, value) = entry.split(": ")
                                    Text(
                                        text = value.trim(),
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text(text = "No data available")
            }
        }
    }
}