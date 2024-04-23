package com.example.hackthon_datallm_ai

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hackthon_datallm_ai.Database.DatabaseHelper
import com.example.hackthon_datallm_ai.Model.ViewModelChat

class MainScreen {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainUi(context: Context, navController: NavController, dataModelChat: ViewModelChat) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate("input") }) {

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
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Localized description",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("DataGene")
                    }
                )
            }
        ) {
            Column(
                Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                val database = DatabaseHelper(context).getTableNames()
                if (database.isNotEmpty()) {
                    LazyColumn {
                        items(database) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.secondary
                                ),
                                elevation = CardDefaults.elevatedCardElevation(
                                    defaultElevation = 10.dp,
                                    pressedElevation = 5.dp
                                ),
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth()
                            )
                            {
                                Row {
                                    Image(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(40.dp)
                                            .align(Alignment.CenterVertically),
                                        painter = painterResource(id = R.drawable.db),
                                        contentDescription = ""
                                    )
                                    Button(
                                        onClick = {
                                            dataModelChat.setdatabase(it)
                                            navController.navigate("chat")
                                        },
                                        modifier = Modifier
                                            .padding(start = 5.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 25.sp,
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .align(Alignment.CenterVertically) // Add padding for better spacing
                                        )

                                    }
                                }

                            }
                        }

                    }
                } else {
                    Text(
                        text = "Please Add the database ",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .offset(y = 300.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .offset(y = 300.dp)
                            .size(200.dp)
                    )
                }
                Button(onClick = {
                    val dbHelper = DatabaseHelper(context)
                    val contentValues = dbHelper.convertSqlToContentValues("INSERT INTO hospital (patient, room, discharge) VALUES ('John Doe', 101, 0)")
//                    if (contentValues != null) {
//                        dbHelper.insertData("hospital", contentValues)
//                    }

                }) {
                    Text(text = "daal do")
                }
            }
        }
    }

}