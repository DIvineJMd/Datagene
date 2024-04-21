package com.example.hackthon_datallm_ai

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.hackthon_datallm_ai.Database.DatabaseHelper

class UIdatainput( val context: Context,val navController: NavController) {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
    @Composable
    fun Databaseinput() {
        Scaffold(

            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Localized description",
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Localized description",
                            )
                        }
                    },
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("Add Document")
                    }
                )
            }
        ) {
            var documentId by remember { mutableStateOf(TextFieldValue()) }
            var fieldName by remember { mutableStateOf(TextFieldValue()) }
            var fields by remember { mutableStateOf(mutableListOf<Pair<String, String>>()) }
            var isDialogOpen by remember { mutableStateOf(false) }
            val Datatype =
                arrayOf(
                    "primaryKey",
                    "string",
                    "number",
                    "boolean",
                    "map",
                    "array",
                    "null",
                    "timestamp",
                )
            var expanded by remember { mutableStateOf(false) }
            var selectedText by remember { mutableStateOf(Datatype[0]) }
            var isPrimaryKeyAdded by remember {
                mutableStateOf(false)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 80.dp, horizontal = 15.dp)
                    .clip(shape = RoundedCornerShape(50.dp))
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(15.dp)
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 80.dp, horizontal = 15.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Input for Document ID
                        OutlinedTextField(
                            value = documentId,
                            onValueChange = { documentId = it },
                            label = { Text("Document Name") }
                        )

                        Row(horizontalArrangement = Arrangement.SpaceBetween) { // Input for Field Name
                            OutlinedTextField(
                                modifier = Modifier
                                    .requiredWidth(150.dp)
                                    .padding(3.dp),
                                value = fieldName,
                                onValueChange = { fieldName = it },
                                label = { Text("Field Name") }
                            )

                            Box(modifier = Modifier.padding(5.dp)) {
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = {
                                        expanded = !expanded
                                    }
                                ) {
                                    OutlinedTextField(
                                        modifier = Modifier
                                            .requiredWidth(170.dp)
                                            .padding(5.dp)
                                            .menuAnchor(),
                                        value = selectedText,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expanded
                                            )
                                        },

                                        )

                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier.requiredHeight(300.dp)
                                    ) {
                                        Datatype.forEach { item ->
                                            DropdownMenuItem(
                                                text = { Text(text = item) },
                                                onClick = {

                                                    selectedText = item
                                                    expanded = false

                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Button to add field
                        Button(
                            onClick = {
                                if (fieldName.text.isNotEmpty() && documentId.text.isNotEmpty()) {

                                    if (!selectedText.equals("primaryKey") || !isPrimaryKeyAdded) {
                                        if(selectedText.equals("primaryKey")){
                                            showToast("Added Primary key")
                                            isPrimaryKeyAdded=true
                                        }
                                        fields.add(fieldName.text to selectedText)
                                        fieldName = TextFieldValue()
                                    } else {
                                        showToast("Primary key already added")
                                    }
                                }
                            },
                            enabled = fieldName.text.isNotEmpty() && documentId.text.isNotEmpty()
                        ) {
                            Text("Add Field")
                        }
                        // Display added fields
                        Column {
                            fields.forEach { (name, type) ->
                                Text(
                                    "$name: $type",
                                    style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }

                        // Button to submit data
                        Button(
                            onClick = { isDialogOpen = true },
                            enabled = fields.isNotEmpty()
                        ) {
                            Text("Submit")
                        }

                        Column(modifier = Modifier) { // Dialog for confirmation
                            if (isDialogOpen) {
                                Dialog(
                                    onDismissRequest = { isDialogOpen = false }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .clip(
                                                shape = RoundedCornerShape(25.dp)
                                            )
                                            .background(MaterialTheme.colorScheme.background),
                                        horizontalAlignment = Alignment.CenterHorizontally

                                    ) {
                                        Text(
                                            "Are you sure you want to submit?",
                                            Modifier.padding(15.dp)
                                        )
                                        Row(
                                            modifier = Modifier.padding(top = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)

                                        ) {
                                            OutlinedButton(onClick = { isDialogOpen = false }) {
                                                Text("Cancel")
                                            }
                                            Button(
                                                onClick = {
                                                    DatabaseHelper(context).createTable(documentId.text, fields)
                                                    isDialogOpen = false
                                                    navController.popBackStack()
                                                }
                                            ) {
                                                Text("Confirm")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }
    private fun showToast( message: String) {
        // Use application context to show toast
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
    }



