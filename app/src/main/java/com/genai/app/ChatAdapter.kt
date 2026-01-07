package com.genai.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.genai.app.data.Message
import io.noties.markwon.Markwon

class ChatAdapter(private val messages: List<Message>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val ivAvatar: ImageView? = view.findViewById(R.id.ivAvatar)
        val tvStatus: TextView? = view.findViewById(R.id.tvStatus)
        val llActions: LinearLayout? = view.findViewById(R.id.llActions)
        
        // Buttons
        val btnCopy: ImageView? = view.findViewById(R.id.btnCopy)
        val btnShare: ImageView? = view.findViewById(R.id.btnShare)
        val btnLike: ImageView? = view.findViewById(R.id.btnLike)
        val btnDislike: ImageView? = view.findViewById(R.id.btnDislike)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == 1) R.layout.item_message_user else R.layout.item_message_ai
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        val markwon = Markwon.create(holder.itemView.context)
        
        if (message.isThinking) {
            holder.tvMessage.visibility = View.GONE
            holder.tvStatus?.visibility = View.VISIBLE
            holder.tvStatus?.text = message.statusText ?: "Thinking..."
            holder.llActions?.visibility = View.GONE
        } else {
            holder.tvMessage.visibility = View.VISIBLE
            holder.tvStatus?.visibility = View.GONE
            markwon.setMarkdown(holder.tvMessage, message.content)
            holder.llActions?.visibility = if (!message.isUser) View.VISIBLE else View.GONE
        }
        
        holder.btnCopy?.setOnClickListener { /* Copy logic */ }
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int) = if (messages[position].isUser) 1 else 0
}