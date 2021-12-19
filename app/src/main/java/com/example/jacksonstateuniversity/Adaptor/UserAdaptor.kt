package com.example.jacksonstateuniversity.Adaptor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jacksonstateuniversity.Activity.ChatActivity
import com.example.jacksonstateuniversity.R
import com.example.jacksonstateuniversity.Student.Student

class UserAdaptor(val context: Context, val userList:ArrayList<Student>):
    RecyclerView.Adapter<UserAdaptor.UserViewHolder>(){

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.activity_user, parent, false)
        return UserViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val currentUser = userList[position]
        holder.textName.text= currentUser.firstname +" "+ currentUser.lastname

        holder.itemView.setOnClickListener{
            val intent = Intent(context, ChatActivity::class.java)

            intent.putExtra("name",currentUser.firstname +" "+ currentUser.lastname)
            intent.putExtra("uid", currentUser.uid)


            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }


}