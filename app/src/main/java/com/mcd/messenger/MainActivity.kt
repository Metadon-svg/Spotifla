package com.mcd.messenger

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    data class Msg(val text: String, val isMe: Boolean)
    private val messages = ArrayList<Msg>()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val et = findViewById<EditText>(R.id.etMessage)
        val btn = findViewById<Button>(R.id.btnSend)
        val rv = findViewById<RecyclerView>(R.id.recyclerView)

        adapter = ChatAdapter(messages)
        rv.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        rv.adapter = adapter

        // Welcome message
        addMsg("Secure Channel Established.", false)
        addMsg("Identify yourself.", false)

        btn.setOnClickListener {
            val txt = et.text.toString()
            if(txt.isNotEmpty()){
                addMsg(txt, true)
                et.text.clear()
                
                // Bot Logic
                Handler(Looper.getMainLooper()).postDelayed({
                    val reply = when {
                        txt.contains("hi", true) -> "Greetings, Operator."
                        txt.contains("code", true) -> "Accessing Repository..."
                        txt.contains("admin", true) -> "Administrator is offline."
                        else -> "Command logged. Waiting for approval."
                    }
                    addMsg(reply, false)
                    rv.smoothScrollToPosition(messages.size - 1)
                }, 800)
            }
        }
    }

    private fun addMsg(text: String, isMe: Boolean){
        messages.add(Msg(text, isMe))
        adapter.notifyItemInserted(messages.size - 1)
    }

    class ChatAdapter(private val list: List<Msg>) : RecyclerView.Adapter<ChatAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tv: TextView = v.findViewById(R.id.tvText)
        }
        override fun getItemViewType(position: Int): Int {
            return if (list[position].isMe) 1 else 0
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val layout = if (viewType == 1) R.layout.item_msg_me else R.layout.item_msg_other
            val v = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return VH(v)
        }
        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.tv.text = list[position].text
        }
        override fun getItemCount() = list.size
    }
}
