package com.rittmann.passwordnotify.ui.listpasswords

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.basic.ManagerPassword

class RecyclerViewPasswords(
    context: Context,
    private val list: List<ManagerPassword>,
    private val callback: (Any) -> Unit
) :
    RecyclerView.Adapter<RecyclerViewPasswords.ViewHolderPasswords>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPasswords =
        ViewHolderPasswords(
            inflater.inflate(R.layout.adapter_passwords, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolderPasswords, position: Int) {
        val item = list[holder.adapterPosition]
        with(holder) {
            txtName.text = item.name
            adapterItem.setOnClickListener {
                callback(item)
            }
        }
    }

    override fun getItemCount(): Int = list.size
    class ViewHolderPasswords(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val adapterItem: View = itemView.findViewById(R.id.adapterItem)
    }
}