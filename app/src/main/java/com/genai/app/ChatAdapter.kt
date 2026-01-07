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
        
        holder.btnCopy?.setOnClickListener {
            val clipboard = holder.itemView.context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("AI Response", message.content)
            clipboard.setPrimaryClip(clip)
            android.widget.Toast.makeText(holder.itemView.context, "Copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
        }

        holder.btnShare?.setOnClickListener {
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_TEXT, message.content)
            }
            holder.itemView.context.startActivity(android.content.Intent.createChooser(intent, "Share via"))
        }

        holder.btnLike?.setOnClickListener {
            holder.btnLike.setColorFilter(android.graphics.Color.parseColor("#10a37f"))
            holder.btnDislike?.clearColorFilter()
        }

        holder.btnDislike?.setOnClickListener {
            holder.btnDislike.setColorFilter(android.graphics.Color.parseColor("#ef4444"))
            holder.btnLike?.clearColorFilter()
        }
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int) = if (messages[position].isUser) 1 else 0
}