package com.example.chatlibrary



import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatActivity : AppCompatActivity() {

    private lateinit var webSocket: ChatWebSocket
    private lateinit var messageAdapter: ChatMessageAdapter
    private lateinit var messageList: MutableList<ChatMessage>
    private lateinit var recyclerView: RecyclerView

    private val messages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val editText = findViewById<EditText>(R.id.editTextMessage)
        val sendButton = findViewById<ImageButton>(R.id.buttonSend)

        messageList = mutableListOf()
        messageAdapter = ChatMessageAdapter(messageList)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = messageAdapter
        }

        webSocket = ChatWebSocket { message ->
            runOnUiThread {
                messageList.add(message)
                messageAdapter.notifyItemInserted(messageList.size - 1)
                recyclerView.scrollToPosition(messageList.size - 1)
            }
        }

        webSocket.connect()

        sendButton.setOnClickListener {
            val text = editText.text.toString().trim()
            if (text.isNotEmpty()) {
                val msg = ChatMessage(text, true)
                messageList.add(msg)
                messageAdapter.notifyItemInserted(messageList.size - 1)
                recyclerView.scrollToPosition(messageList.size - 1)

                webSocket.sendMessage(text)
                editText.text.clear()
            }
        }
    }
    private fun addMessage(message: ChatMessage) {
        messages.add(message)
        messageAdapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    override fun onDestroy() {
        webSocket.close()
        super.onDestroy()
    }
}
