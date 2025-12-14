package com.example.myapplication.screen.Chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.DataMessanger.NODE_CHANNELS
import com.example.myapplication.model.Channel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(): ViewModel() {

    private val firebaseDatabase = Firebase.database
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels = _channels.asStateFlow()

    init {
        getChannels()
    }

    private fun getChannels() {

        firebaseDatabase.getReference(NODE_CHANNELS).get().addOnSuccessListener {
            val list = mutableListOf<Channel>()
            it.children.forEach { data ->
                val channel = Channel(id = data.key!!, name = data.value.toString())
                list.add(channel)
            }
            _channels.value = list
        }
    }

    fun addChannel(name: String){
        val key = firebaseDatabase.getReference(NODE_CHANNELS).push().key
        firebaseDatabase.getReference(NODE_CHANNELS).child(key!!).setValue(name).addOnSuccessListener {
            getChannels()
        }
    }

}