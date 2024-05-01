import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.hackthon_datallm_ai.Database.DatabaseHelper
import com.example.hackthon_datallm_ai.geminidatamanager.ChatViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class Showdatabase {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun Showdata(context: Context, chatViewModel: ChatViewModel, navController: NavController) {
        var search by remember { mutableStateOf("") }
        var suggest by remember { mutableStateOf(emptyList<String>()) }
        var isDropdownExpanded by remember {
            mutableStateOf(false)
        }
        val keyboardController = LocalSoftwareKeyboardController.current

        val focusManager = LocalFocusManager.current
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        tint = MaterialTheme.colorScheme.surface,
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
                                tint = MaterialTheme.colorScheme.surface,
                                contentDescription = "Localized description",
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                tint = MaterialTheme.colorScheme.surface,
                                contentDescription = "Localized description",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("DataGenie 📊")
                    }
                )
            }
        ) {
            val cursor = chatViewModel.getAllData()

            // If cursor is not null and has data
            if (cursor != null) {
                val attributes = chatViewModel._attributes.map { it.first }

                Column(Modifier.padding(it).pointerInput(Unit){
                   detectTapGestures(onTap = {
                       keyboardController?.hide()
                       focusManager.clearFocus(force = true)
                   })
                }) {
                    Column(Modifier.padding(8.dp)) {
                        OutlinedTextField(
                            value = search,
                            onValueChange = {
                                search = it
                                isDropdownExpanded = it.isNotEmpty()
                                chatViewModel.searchInDatabase(it) { suggestions ->
                                    suggest = suggestions
                                    println(suggest)
                                }
                            },
                            label = { Text("Search") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onSecondary,
                                cursorColor =MaterialTheme.colorScheme.onSecondary,
                                focusedLabelColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
//                                imeAction= ImeAction.Previous
                            ),
                            keyboardActions = KeyboardActions(onDone = {

                                keyboardController?.hide()
                                focusManager.clearFocus(force = true)
                            },
                             ),
                        )

                    }
                    LazyColumn(
                        Modifier.fillMaxWidth()
                    ) {
                        item {
                            Surface(
                                color=MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                shadowElevation = 4.dp,
                                tonalElevation = 5.dp,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Display attributes as table headers
                                    attributes.forEach { attribute ->
                                        Text(
                                            text = attribute,
                                            modifier = Modifier
                                                .weight(2f)
                                                .padding(horizontal = 4.dp),
                                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                        items(
                            if(!isDropdownExpanded){
                                DatabaseHelper(context).getDataFromCursor(
                                    cursor,
                                    chatViewModel._attributes
                                )
                            }else{
                                suggest
                            }
                        ) { rowData ->
                            Surface(
                                modifier = Modifier.padding(8.dp),
                                color=MaterialTheme.colorScheme.primary,
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

                                            color=MaterialTheme.colorScheme.onSecondary,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 4.dp)
                                        )
                                    }
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
