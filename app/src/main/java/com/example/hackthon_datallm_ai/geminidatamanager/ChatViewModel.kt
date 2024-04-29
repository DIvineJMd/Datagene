package com.example.hackthon_datallm_ai.geminidatamanager

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hackthon_datallm_ai.Database.DatabaseHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel (private val context: Context) : ViewModel() {
    private var _database = "" //table name

    lateinit var _attributes: List<Pair<String, String>>
    fun setattributes(attributes: List<Pair<String, String>>) {
        _attributes = attributes
    }

    fun setDatabase(database: String) {
        _database = database
    }

    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()
    val isLoading = mutableStateOf(true)
    val responseMessage = mutableStateOf("")
    private val _livechat = MutableLiveData<Resource<String>>()
    val chatdata: LiveData<Resource<String>> = _livechat

    fun onEvent(event: ChatUIEvent) {
        when (event) {
            is ChatUIEvent.SendPrompt -> {
                if (event.prompt.isNotEmpty()) {
                    addPrompt(event.prompt)
                }
                getResponse(event.prompt)
            }

            is ChatUIEvent.UpdatePrompt -> {
                _chatState.update {
                    it.copy(prompt = event.newPrompt)
                }
            }
        }
    }

    private fun addPrompt(prompt: String) {
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, Chat(prompt, true))
                },
                prompt = ""
            )
        }
    }

    private fun getResponse(prompt: String) {
        _livechat.value = Resource.Loading()
        viewModelScope.launch {
                var modifiedPrompt = "prompt is "+prompt + ", now make sure Give me a query in this specific format if prompt is literally have all necessary attributes corresponding to table other wise dont give any output corresponding to insert, alter, delete, or update query: " +
            "INSERT INTO <tablename> (key1, key2) VALUES (value1, value2)" +
            " or " +
            "UPDATE <tablename> SET key1 = value1, key2 = value2 WHERE condition" +
            " or " +
            "DELETE FROM <tablename> WHERE condition" +
            " or " +
            "ALTER TABLE <tablename> ADD COLUMN column_name datatype" + "here is the table attributes table name: ${_database} attributes are ${_attributes.toString()}"
            val chat = ChatData.getResponse(modifiedPrompt, isLoading, responseMessage)
            if(chat==null){
                _chatState.update {
                    it.copy(
                        chatList = it.chatList.toMutableList().apply {
                            add(0, chat)
                        }
                    )
                }
            }
            else{
                val dbHelper = DatabaseHelper(context)
                val contentValues = dbHelper.convertSqlToContentValues("INSERT INTO hospital (patient, room, discharge) VALUES (John Doe, 101, 0)")
                if (contentValues != null) {
                    dbHelper.insertData("hospital", contentValues)
                }
                _livechat.value = Resource.Success(responseMessage.value)
                _livechat.value = Resource.Stop()
                _chatState.update {
                    it.copy(
                        chatList = it.chatList.toMutableList().apply {
                            add(0, chat)
                        }
                    )
                }
            }
        }
    }

}
