package com.example.jacksonstateuniversity

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jacksonstateuniversity.Activity.ChatActivity
import com.example.jacksonstateuniversity.Student.Faculty

class FacultyAdaptor(val context: Context, val userList:ArrayList<Faculty>):
    RecyclerView.Adapter<FacultyAdaptor.UserViewHolder>(){

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        val textYear = itemView.findViewById<TextView>(R.id.txt_year)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.activity_faculty, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.textName.text = currentUser.firstname + " " + currentUser.lastname
        holder.textYear.text = currentUser.roll

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)

            intent.putExtra("name", currentUser.firstname + " " + currentUser.lastname)
            intent.putExtra("uid", currentUser.uid)

            context.startActivity(intent)
        }

    }
    override fun getItemCount(): Int {
        return userList.size
    }
}