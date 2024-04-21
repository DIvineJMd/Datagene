package com.example.hackthon_datallm_ai.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModelChat : ViewModel() {
    private var _database =""
    val database:String
        get() {
         return  _database
        }
    fun setdatabase(databsae:String){
        _database=database
    }
    val conversation: StateFlow<List<ChatUiModel.Message>>
        get() = _conversation
    private val _conversation = MutableStateFlow(
        listOf(ChatUiModel.Message.initConv)
    )
    fun sendChat(msg: String) {

        val myChat = ChatUiModel.Message(msg, ChatUiModel.Author.me)
        viewModelScope.launch {
            _conversation.emit(_conversation.value + myChat)

//            delay(1000)
//            _conversation.emit(_conversation.value + getBotreply // make function to get bot reply
        }
    }
//    private fun getbotreply(): ChatUiModel.Message {
//     // make reply
//
//        return ChatUiModel.Message(
//            text = reply,
//            author = ChatUiModel.Author.bot
//        )
//    }
}