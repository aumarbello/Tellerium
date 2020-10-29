package com.aumarbello.telleriumassessment.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aumarbello.telleriumassessment.R
import com.aumarbello.telleriumassessment.db.UserEntity
import com.aumarbello.telleriumassessment.utils.loadImage
import java.util.*

class UsersAdapter (private val callback: CallBack) : ListAdapter<UserEntity, UsersAdapter.UsersHolder>(DIFF) {
    interface CallBack {
        fun openDetails(view: View, userId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_user, parent, false
        )

        return UsersHolder(view)
    }

    override fun onBindViewHolder(holder: UsersHolder, position: Int) {
        holder.bindItem(getItem(position))
    }

    inner class UsersHolder(view: View): RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.findViewById(R.id.image)
        private val fullName: TextView = view.findViewById(R.id.full_name)
        private val address: TextView = view.findViewById(R.id.address)
        private val gender: TextView = view.findViewById(R.id.gender)
        private val phoneNumber: TextView = view.findViewById(R.id.phone_number)

        fun bindItem(user: UserEntity) {
            itemView.transitionName = user.id

            image.loadImage(user.imageUrl)
            fullName.text = itemView.resources.getString(
                R.string.format_full_name,
                user.firstName.toLowerCase(Locale.getDefault()).capitalize(),
                user.lastName.toLowerCase(Locale.getDefault()).capitalize()
            )
            address.text = formatAddress(user)
            gender.text = user.gender
            phoneNumber.text = user.phoneNumber

            itemView.setOnClickListener {
                callback.openDetails(it, user.id)
            }
        }

        private fun formatAddress(user: UserEntity): String {
            return "${user.address.capitalize()}, ${user.city.capitalize()}"
        }

        private fun String.capitalize(): String {
            if (isNotEmpty()) {
                val firstChar = this[0]
                if (firstChar.isLowerCase()) {
                    return buildString {
                        val titleChar = firstChar.toTitleCase()
                        if (titleChar != firstChar.toUpperCase()) {
                            append(titleChar)
                        } else {
                            append(this@capitalize.substring(0, 1).toUpperCase(Locale.getDefault()))
                        }
                        append(this@capitalize.substring(1))
                    }
                }
            }
            return this
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<UserEntity>() {
            override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}